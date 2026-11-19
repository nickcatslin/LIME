package io.github.chipppppppppp.lime.hooks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.chipppppppppp.lime.LimeOptions;

/**
 * Removes server-driven GCS modules from the Compose-hosted home tab (LINE 26.x).
 * <p>
 * The home page is a list of modules whose types are plain strings ("FLEX", "AdModel", ...).
 * Each module is materialised by a Compose factory returning a render result; returning the
 * SDK's own "empty module" result makes the module render nothing, which is what LINE itself
 * does for unknown module types. This works regardless of whether the module is drawn with
 * Views or Compose, so it also covers what the View-level hooks in RemoveAds and
 * RemoveFlexibleContents cannot reach.
 */
public class RemoveGcsModules implements IHook {
    // Google/LINE ad modules on the home tab.
    private static final Set<String> AD_MODULE_TYPES = new HashSet<>(Arrays.asList(
            "AdModel",           // LINE Ads SDK v2 (LyadAdView) GCS ad section
            "HomePerformanceAd"  // LINE Ads SDK v1 (LadAdView) performance ad rows
    ));
    // Server-pushed recommendation cards (the "FLEX" module renders arbitrary Flex content,
    // including sponsored cards with an "AD" badge). Same intent as the removeRecommendation
    // option on the older View-based home tab.
    private static final Set<String> RECOMMENDATION_MODULE_TYPES = new HashSet<>(Arrays.asList(
            "FLEX"
    ));

    @Override
    public void hook(LimeOptions limeOptions, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!limeOptions.removeAds.checked && !limeOptions.removeRecommendation.checked) return;

        // Let a missing target propagate: Main logs it, so a LINE update that renames these classes
        // shows up in the LSPosed log instead of silently bringing the ads back.
        Object emptyModule = XposedHelpers.getStaticObjectField(
                loadPackageParam.classLoader.loadClass(Constants.GCS_EMPTY_MODULE.className),
                Constants.GCS_EMPTY_MODULE.methodName
        );

        XC_MethodHook replaceWithEmptyModule = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // a(String moduleKey, module, pageState, composer)
                if (param.args.length != 4 || param.args[1] == null) return;
                String type = String.valueOf(XposedHelpers.callMethod(param.args[1], "getType"));
                if (limeOptions.removeAds.checked && AD_MODULE_TYPES.contains(type)
                        || limeOptions.removeRecommendation.checked && RECOMMENDATION_MODULE_TYPES.contains(type)) {
                    param.setResult(emptyModule);
                }
            }
        };

        // Hook every factory even if one is missing, then report the first failure.
        Throwable failure = null;
        for (Constants.HookTarget target : new Constants.HookTarget[]{Constants.GCS_FLEX_MODULE_HOOK, Constants.GCS_VIEW_MODULE_HOOK}) {
            try {
                XposedBridge.hookAllMethods(
                        loadPackageParam.classLoader.loadClass(target.className),
                        target.methodName,
                        replaceWithEmptyModule
                );
            } catch (Throwable t) {
                if (failure == null) failure = t;
            }
        }
        if (failure != null) throw failure;
    }
}
