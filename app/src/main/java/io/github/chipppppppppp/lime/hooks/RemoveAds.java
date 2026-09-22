package io.github.chipppppppppp.lime.hooks;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.chipppppppppp.lime.LimeOptions;

public class RemoveAds implements IHook {

    static final List<String> adClassNames = new ArrayList<>(List.of());

    @Override
    public void hook(LimeOptions limeOptions, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!limeOptions.removeAds.checked) return;

        XposedBridge.hookAllMethods(
                loadPackageParam.classLoader.loadClass(Constants.REQUEST_HOOK.className),
                Constants.REQUEST_HOOK.methodName,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String request = param.args[0].toString();
                        if (request.equals("getBanners") || request.equals("getPrefetchableBanners")) {
                            // These calls go through the OkHttp-backed Thrift transport, where sendBase is
                            // invoked lazily while serializing the request body. Returning null here would
                            // still send an empty POST to the ad server; failing instead makes the body
                            // serializer throw a ProtocolException so OkHttp aborts before sending anything.
                            // The caller already treats that as a failed (empty) banner response.
                            param.setThrowable(new IllegalStateException("Blocked by LIME: " + request));
                        }
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                loadPackageParam.classLoader.loadClass("com.linecorp.line.admolin.smartch.v2.view.SmartChannelViewLayout"),
                "dispatchDraw",
                Canvas.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ((View) ((View) param.thisObject).getParent()).setVisibility(View.GONE);
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                loadPackageParam.classLoader.loadClass("com.linecorp.line.ladsdk.ui.common.view.lifecycle.LadAdView"),
                "onAttachedToWindow",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View) ((View) param.thisObject).getParent().getParent();
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.height = 0;
                        view.setLayoutParams(layoutParams);
                        view.setVisibility(View.GONE);
                    }
                }
        );

        // LINE Ads SDK v2 (LyadAdView). LINE 26.x renders the GCS ad modules with it: the home tab
        // "gcs_ad_section" card, the chat list / OpenChat tab GCS ads, album, calendar and note ads.
        // The view is the root of every ladsdk_*v2*/gcs_* layout and is wrapped in a FrameLayout
        // that the module drops into its section container, so collapsing the single-child chain
        // above it removes the whole module without touching containers shared with other content.
        try {
            XposedHelpers.findAndHookMethod(
                    loadPackageParam.classLoader.loadClass("com.linecorp.line.ladsdk.ui.v2.common.lifecycle.LyadAdView"),
                    "onAttachedToWindow",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            collapseAdView((View) param.thisObject);
                        }
                    }
            );
        } catch (Throwable ignored) {
        }

        XposedHelpers.findAndHookMethod(
                ViewGroup.class,
                "addView",
                View.class,
                ViewGroup.LayoutParams.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View) param.args[0];
                        String className = view.getClass().getName();
                        if (className.contains("Ad")) {
                            if (!adClassNames.contains(className)) {
                                adClassNames.add(className);
                            }
                            view.setVisibility(View.GONE);
                        }
                    }
                }
        );

        for (String adClassName : adClassNames) {
            XposedBridge.hookAllConstructors(
                    loadPackageParam.classLoader.loadClass(adClassName),
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            View view = (View) param.thisObject;
                            view.setVisibility(View.GONE);
                            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    if (view.getVisibility() != View.GONE) {
                                        view.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
            );
        }

        XposedHelpers.findAndHookMethod(
                loadPackageParam.classLoader.loadClass(Constants.WEBVIEW_CLIENT_HOOK.className),
                Constants.WEBVIEW_CLIENT_HOOK.methodName,
                WebView.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        WebView webView = (WebView) param.args[0];
                        webView.evaluateJavascript("(() => {\n" +
                                "    const observer = new MutationObserver(mutations => {\n" +
                                "        mutations.forEach(mutation => {\n" +
                                "            mutation.addedNodes.forEach(node => {\n" +
                                "                if (!node.querySelectorAll) return;\n" +
                                "                node.querySelectorAll('.ad_wrap, .lc__ad_root, .lc__ad_element').forEach(ad => ad.remove());\n" +
                                "            });\n" +
                                "        });\n" +
                                "    });\n" +
                                "    const config = {\n" +
                                "        childList: true,\n" +
                                "        subtree: true\n" +
                                "    };\n" +
                                "    observer.observe(document.body, config);\n" +
                                "})();", null);
                    }
                }
        );

        hookMinorRegionAds(loadPackageParam);
    }

    // Google Ad Manager banners shown outside Japan ("minor region" ads): the top banner on the
    // home tab (home_tab_top_banner_row) and the chat list header (chat_tab_google_ad). They do not
    // go through the LINE Ads SDK, so none of the hooks above catch them.
    //
    // Only the component factory has a stable name. It returns a view-controller factory whose
    // single method takes the ad container ViewGroup and returns the controller; the controller's
    // single suspend method loads an ad into that container. Everything below is resolved at
    // runtime from the objects those calls return, so obfuscated names do not matter.
    private static final String MINOR_REGION_AD_FACTORY =
            "com.linecorp.line.minor.region.ad.impl.viewcontroller.DelegatedMinorRegionAdViewControllerFactory";

    private void hookMinorRegionAds(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> factoryClass;
        Class<?> continuationClass;
        Object unit;
        try {
            factoryClass = loadPackageParam.classLoader.loadClass(MINOR_REGION_AD_FACTORY);
            continuationClass = loadPackageParam.classLoader.loadClass("kotlin.coroutines.Continuation");
            unit = XposedHelpers.getStaticObjectField(loadPackageParam.classLoader.loadClass("kotlin.Unit"), "INSTANCE");
        } catch (Throwable ignored) {
            return;
        }

        Set<Class<?>> hookedClasses = new HashSet<>();

        XposedHelpers.findAndHookMethod(factoryClass, "createComponent", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Object controllerFactory = param.getResult();
                if (controllerFactory == null || !hookedClasses.add(controllerFactory.getClass())) return;

                for (Method method : controllerFactory.getClass().getDeclaredMethods()) {
                    Class<?>[] params = method.getParameterTypes();
                    if (method.isSynthetic() || Modifier.isStatic(method.getModifiers())) continue;
                    if (params.length != 1 || !ViewGroup.class.isAssignableFrom(params[0])) continue;

                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            hideAdContainer((ViewGroup) param.args[0]);

                            Object controller = param.getResult();
                            if (controller == null || !hookedClasses.add(controller.getClass())) return;

                            for (Method loadMethod : controller.getClass().getDeclaredMethods()) {
                                Class<?>[] loadParams = loadMethod.getParameterTypes();
                                if (loadMethod.isSynthetic() || Modifier.isStatic(loadMethod.getModifiers())) continue;
                                if (loadParams.length == 0 || !continuationClass.isAssignableFrom(loadParams[loadParams.length - 1])) continue;

                                XposedBridge.hookMethod(loadMethod, new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) {
                                        for (Field field : param.thisObject.getClass().getDeclaredFields()) {
                                            if (!ViewGroup.class.isAssignableFrom(field.getType())) continue;
                                            field.setAccessible(true);
                                            try {
                                                hideAdContainer((ViewGroup) field.get(param.thisObject));
                                            } catch (IllegalAccessException ignored) {
                                            }
                                        }
                                        // Complete the suspend function immediately without requesting an ad.
                                        param.setResult(unit);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    // Hides an ad view and every ancestor that exists only to hold it (wrapper FrameLayouts, the
    // module's section container), so wrap_content parents collapse to zero height. Stops at the
    // first ancestor that also holds other children, and never touches Compose-managed views.
    private static void collapseAdView(View adView) {
        View view = adView;
        while (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = 0;
                view.setLayoutParams(layoutParams);
            }
            view.setVisibility(View.GONE);

            if (!(view.getParent() instanceof ViewGroup)) return;
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent.getChildCount() != 1) return;
            if (parent.getClass().getName().startsWith("androidx.compose.")) return;
            view = parent;
        }
    }

    private static void hideAdContainer(ViewGroup container) {
        if (container == null) return;
        container.setVisibility(View.GONE);
        // The chat list wraps the container in a padded FrameLayout of its own; collapse that too.
        // The home tab row holds other banners next to the container, so leave multi-child parents alone.
        if (container.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) container.getParent();
            if (parent.getChildCount() == 1) {
                parent.setVisibility(View.GONE);
            }
        }
    }
}
