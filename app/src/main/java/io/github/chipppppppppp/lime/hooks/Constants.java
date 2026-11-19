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

    // Hook targets for LINE 26.18.2 (versionCode 261820153).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // kr8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("kr8.d", "i");
    // zw2.m0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("zw2.m0", "onPageFinished");
    // hs8.o1#a(cp8.b metadata, lm8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("hs8.o1", "a");
    // xj3.e$d#run(): Runnable that sends sendChatChecked for a chat (calls e.this.d(mid, msgId, flag))
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("xj3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("ei3.c1", "invokeSuspend");
    // is8.a2#b(v0, ee, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    // (registered via `new is8.a2(de.NOTIFIED_READ_MESSAGE)`; extends receive-op base hs8.a)
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("is8.a2", "b");
    // org.apache.thrift.o: TServiceClient; c = sendBase(name, args), b = receiveBase(name, result).
    // a() is a no-arg getter, so check these letters on every update: hooking by name does not fail
    // when they shift, it silently hooks the wrong method. (26.18.2: still c = send, b = receive.)
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.o", "c");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.o", "b");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // li2.z0 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract yj2.j and implement a(String, li2.z0, ik2.s, j3.r) returning yj2.h.
    // pi2.o: yj2.j<li2.z0.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("pi2.o", "a");
    // nk2.k: yj2.j wrapper (holds ek2.b<T>) around every View-based module
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("nk2.k", "a");
    // yj2.h#e: static "empty module" result (yj2.h.a.b(null, ..., 3)), the value nk2.a0 returns
    // for unknown modules. jadx displays this field as f405619e ("renamed from: e"); use the real dex name.
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("yj2.h", "e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
