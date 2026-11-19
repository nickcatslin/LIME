package io.github.chipppppppppp.lime.hooks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.chipppppppppp.lime.LimeOptions;

public class BlockTracking implements IHook {
    private static final Set<String> BLOCKED_REQUESTS = new HashSet<>(Arrays.asList(
            "noop",
            "reportAbuseEx",
            "reportDeviceState",
            "reportLocation",
            "reportNetworkStatus",
            "reportProfile",
            "reportPushRecvReports",
            "reportSetting"
    ));

    @Override
    public void hook(LimeOptions limeOptions, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!limeOptions.blockTracking.checked) return;

        // Every Thrift call is sendBase(name, args) followed by receiveBase(name, result).
        // Skipping only sendBase leaves the transport without a receiveSource, and the
        // subsequent receiveBase then crashes LINE with "receiveSource is not set." (issue #239).
        // Both halves must be skipped together so the caller sees an empty, exception-free result.
        XC_MethodHook skipBlockedRequest = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (BLOCKED_REQUESTS.contains(String.valueOf(param.args[0]))) {
                    param.setResult(null);
                }
            }
        };

        Class<?> serviceClient = loadPackageParam.classLoader.loadClass(Constants.REQUEST_HOOK.className);
        XposedBridge.hookAllMethods(serviceClient, Constants.REQUEST_HOOK.methodName, skipBlockedRequest);
        XposedBridge.hookAllMethods(serviceClient, Constants.RESPONSE_HOOK.methodName, skipBlockedRequest);
    }
}
