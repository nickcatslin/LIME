package io.github.chipppppppppp.lime.hooks;

public class Constants {
    public static final String PACKAGE_NAME = "jp.naver.line.android";
    public static final String MODULE_NAME = "io.github.chipppppppppp.lime";

    public static class HookTarget {
        public String className;
        public String methodName;

        public HookTarget(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }

    // Hook targets for LINE 26.19.0 (versionCode 261900178).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // mj8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("mj8.d", "i");
    // gy2.t0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("gy2.t0", "onPageFinished");
    // jk8.m1#a(eh8.b metadata, ne8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("jk8.m1", "a");
    // tn3.e$d#run(): Runnable that sends sendChatChecked for a chat (calls e.this.d(mid, msgId, flag))
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("tn3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("am3.d1", "invokeSuspend");
    // kk8.b2#b(u0, ce, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    // (registered via `new kk8.b2(be.NOTIFIED_READ_MESSAGE)`; extends receive-op base jk8.a)
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("kk8.b2", "b");
    // org.apache.thrift.m: TServiceClient; c = sendBase(name, args), b = receiveBase(name, result).
    // a() is a no-arg getter, so check these letters on every update: hooking by name does not fail
    // when they shift, it silently hooks the wrong method. 26.19.0: the class itself moved from o
    // to m (o is now TUnion, and hookAllMethods on it hooks nothing), so confirm the class too.
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.m", "c");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.m", "b");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // fj2.w0 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract sk2.j and implement a(String, fj2.w0, cl2.o, u3.q) returning sk2.h.
    // jj2.q: sk2.j<fj2.w0.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("jj2.q", "a");
    // hl2.l: sk2.j wrapper (holds yk2.b<T>) around every View-based module
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("hl2.l", "a");
    // sk2.h#e: static "empty module" result (sk2.h.a.b(null, ..., 3)), the value hl2.e0 returns
    // for unknown modules. jadx displays this field as f330458e ("renamed from: e"); use the real dex name.
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("sk2.h", "e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
