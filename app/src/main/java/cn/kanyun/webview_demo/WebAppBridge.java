package cn.kanyun.webview_demo;

import android.webkit.JavascriptInterface;

/**
 * 简单的说就是向网页注入一段 js, 在这段 js 里面设置回调到java中的方法 getResult()，由 WebAppBridge.getResult 来回收。
 * 其中js的核心代码为
 * oauth.getResult(str);
 * 其中 oauth 这个名称要与 webView.addJavascriptInterface()方法的第二个参数一样
 */
public class WebAppBridge {
    private OauthLoginImpl oauthLogin;

    public WebAppBridge(OauthLoginImpl oauthLogin) {
        this.oauthLogin = oauthLogin;
    }

    @JavascriptInterface
    public void getResult(String str) {
        if (oauthLogin != null)
            oauthLogin.getResult(str);
    }

    public interface OauthLoginImpl {
        void getResult(String s);
    }
}
