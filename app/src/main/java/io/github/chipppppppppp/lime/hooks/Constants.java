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

    // Hook targets for LINE 26.15.1 (versionCode 261510192).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // km8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("km8.d", "i");
    // qt2.k0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("qt2.k0", "onPageFinished");
    // hn8.i1#a(dk8.b metadata, mh8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("hn8.i1", "a");
    // pg3.e$d#run(): Runnable that sends sendChatChecked for a chat
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("pg3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("ve3.b1", "invokeSuspend");
    // in8.a2#b(u0, Operation, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("in8.a2", "b");
    // org.apache.thrift.o: TServiceClient; b = sendBase(name, args), a = receiveBase(name, result)
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.o", "b");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.o", "a");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // xe2.n0 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract yg2.j and implement a(String, xe2.n0, ih2.p, j3.q) returning yg2.h.
    // pf2.p: yg2.j<xe2.n0.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("pf2.p", "a");
    // nh2.l: yg2.j wrapper around every View-based eh2.b module
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("nh2.l", "a");
    // yg2.h#e: static "empty module" result (yg2.h.a.b(null, ..., 3)), the value nh2.a0 returns
    // for unknown modules. jadx displays this field as f398906e ("renamed from: e"); use the real dex name.
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("yg2.h", "e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
