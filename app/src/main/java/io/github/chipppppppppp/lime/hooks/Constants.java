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

    // Hook targets for LINE 26.14.0 (versionCode 261400121).
    // Obfuscated names change on every LINE release; re-locate them with jadx when updating.

    // lf8.d#i(Context): builds the "ANDROID\t<ver>\tAndroid OS\t<os>" application string
    static final HookTarget USER_AGENT_HOOK = new HookTarget("lf8.d", "i");
    // qn2.v0: WebViewClientCompat used by the in-app browser (IabWebViewCallbackImpl)
    static final HookTarget WEBVIEW_CLIENT_HOOK = new HookTarget("qn2.v0", "onPageFinished");
    // ig8.e1#a(ed8.b metadata, na8.f muteType): writes the mute flag into outgoing message metadata
    static final HookTarget MUTE_MESSAGE_HOOK = new HookTarget("ig8.e1", "a");
    // na3.e$d#run(): Runnable that sends sendChatChecked for a chat
    static final HookTarget MARK_AS_READ_HOOK = new HookTarget("na3.e$d", "run");
    // MainChatDataManager$getUnarchivedChatDataListOrderedByLastMessage$2#invokeSuspend
    static final HookTarget ARCHIVE_HOOK = new HookTarget("u83.c1", "invokeSuspend");
    // jg8.y1#b(r0, Operation, Continuation): receive-operation handler for NOTIFIED_READ_MESSAGE
    static final HookTarget NOTIFICATION_READ_HOOK = new HookTarget("jg8.y1", "b");
    // org.apache.thrift.n: TServiceClient; b = sendBase(name, args), a = receiveBase(name, result)
    static final HookTarget REQUEST_HOOK = new HookTarget("org.apache.thrift.n", "b");
    static final HookTarget RESPONSE_HOOK = new HookTarget("org.apache.thrift.n", "a");

    // Compose-hosted GCS home tab modules (see RemoveGcsModules). Anchors: module types implement
    // y82.k0 with getType() returning "FLEX", "AdModel", "HomePerformanceAd"; factories extend the
    // abstract za2.j and implement a(String, y82.k0, jb2.q, h3.s) returning za2.h.
    // q92.n: za2.j<y82.k0.f> (FLEX, GcsFlexContents)
    static final HookTarget GCS_FLEX_MODULE_HOOK = new HookTarget("q92.n", "a");
    // ob2.l: za2.j wrapper around every View-based fb2.b module (instantiated in jb2.g2)
    static final HookTarget GCS_VIEW_MODULE_HOOK = new HookTarget("ob2.l", "a");
    // za2.h#f393361e: static "empty module" result (za2.h.a.b(null, new za2.g(), 3)), the value ob2.c0 returns for unknown modules
    static final HookTarget GCS_EMPTY_MODULE = new HookTarget("za2.h", "f393361e");

    // Non-obfuscated classes that moved packages between releases
    static final String IN_APP_BROWSER_ACTIVITY = "com.linecorp.line.iab.browser.impl.InAppBrowserActivity";
    static final String WELCOME_FRAGMENT = "com.linecorp.line.registration.ui.fragment.WelcomeFragment";
}
