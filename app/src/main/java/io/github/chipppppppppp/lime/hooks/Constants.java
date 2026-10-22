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

    // Hook targets for LINE 26.16.0 (versionCode 261600106).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // wp8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("wp8.d", "i");
    // qt2.s0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("qt2.s0", "onPageFinished");
    // tq8.p1#a(pn8.b metadata, yk8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("tq8.p1", "a");
    // qg3.e$d#run(): Runnable that sends sendChatChecked for a chat
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("qg3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("we3.b1", "invokeSuspend");
    // uq8.z1#b(v0, Operation, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("uq8.z1", "b");
    // org.apache.thrift.o: TServiceClient; b = sendBase(name, args), a = receiveBase(name, result)
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.o", "b");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.o", "a");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // af2.d1 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract sg2.i and implement a(String, af2.d1, ch2.s, j3.r) returning sg2.g.
    // jf2.q: sg2.i<af2.d1.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("jf2.q", "a");
    // hh2.l: sg2.i wrapper around every View-based yg2.b module
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("hh2.l", "a");
    // sg2.g#e: static "empty module" result (sg2.g.a.b(null, ..., 3)), the value hh2.c0 returns
    // for unknown modules. jadx displays this field as f321412e ("renamed from: e"); use the real dex name.
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("sg2.g", "e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
