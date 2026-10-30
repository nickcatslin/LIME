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

    // Hook targets for LINE 26.17.0 (versionCode 261700167).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // nr8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("nr8.d", "i");
    // rv2.o0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("rv2.o0", "onPageFinished");
    // ks8.n1#a(gp8.b metadata, pm8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("ks8.n1", "a");
    // pi3.e$d#run(): Runnable that sends sendChatChecked for a chat
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("pi3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("wg3.a1", "invokeSuspend");
    // ls8.x1#b(t0, Operation, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("ls8.x1", "b");
    // org.apache.thrift.o: TServiceClient; c = sendBase(name, args), b = receiveBase(name, result).
    // Since 26.17.0, a() is a no-arg getter, so check these letters on every update: hooking by name
    // does not fail when they shift, it silently hooks the wrong method.
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.o", "c");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.o", "b");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // eh2.a1 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract ri2.i and implement a(String, eh2.a1, bj2.s, j3.q) returning ri2.g.
    // ih2.p: ri2.i<eh2.a1.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("ih2.p", "a");
    // gj2.k: ri2.i wrapper around every View-based xi2.b module
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("gj2.k", "a");
    // ri2.g#e: static "empty module" result (ri2.g.a.b(null, ..., 3)), the value gj2.a0 returns
    // for unknown modules. jadx displays this field as f316867e ("renamed from: e"); use the real dex name.
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("ri2.g", "e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
