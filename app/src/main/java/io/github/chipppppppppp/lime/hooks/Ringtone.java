package io.github.chipppppppppp.lime.hooks;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.chipppppppppp.lime.LimeOptions;

/**
 * Plays the system ringtone when an incoming LINE call arrives, and stops it when the call screen
 * opens, the caller cancels, or after a safety timeout.
 */
public class Ringtone implements IHook {
    // Operation.toString() renders the type as ", type:<ENUM>, reqSeq:...".
    private static final String RECEIVED_CALL = "type:NOTIFIED_RECEIVED_CALL,";
    private static final String CANCEL_CALL = "type:CANCEL_CALL,";
    // LINE gives up on an unanswered call well before this, so the ringtone never outlives it.
    private static final long MAX_RING_MS = 60_000;

    private static android.media.Ringtone ringtone = null;
    // Created lazily: LSPosed loads this class while the process is still being specialised from
    // zygote, before the main Looper exists, and a static Handler there would abort the whole module.
    private static Handler handler = null;
    private static final Runnable stopRunnable = Ringtone::stop;

    private static synchronized Handler handler() {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        return handler;
    }

    @Override
    public void hook(LimeOptions limeOptions, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!limeOptions.callTone.checked) return;

        XposedBridge.hookAllMethods(
                loadPackageParam.classLoader.loadClass(Constants.RESPONSE_HOOK.className),
                Constants.RESPONSE_HOOK.methodName,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args[1] == null) return;
                        String response = param.args[1].toString();
                        // One sync response can carry both operations; the later one wins.
                        int received = response.lastIndexOf(RECEIVED_CALL);
                        int cancelled = response.lastIndexOf(CANCEL_CALL);
                        if (received > cancelled) {
                            handler().post(Ringtone::play);
                        } else if (cancelled >= 0) {
                            handler().post(Ringtone::stop);
                        }
                    }
                });

        // VoIPBaseFragment declares onAttach (onCreate is only inherited, so hooking it by name
        // installs nothing). Every call screen attaches one, i.e. the user has opened the call.
        XposedBridge.hookAllMethods(
                loadPackageParam.classLoader.loadClass("com.linecorp.voip2.common.base.VoIPBaseFragment"),
                "onAttach",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        handler().post(Ringtone::stop);
                    }
                });
    }

    private static void play() {
        if (ringtone != null && ringtone.isPlaying()) return;
        Context context = AndroidAppHelper.currentApplication();
        if (context == null) return;
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        if (ringtone == null) return;
        ringtone.play();
        handler().removeCallbacks(stopRunnable);
        handler().postDelayed(stopRunnable, MAX_RING_MS);
    }

    private static void stop() {
        handler().removeCallbacks(stopRunnable);
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        ringtone = null;
    }
}
