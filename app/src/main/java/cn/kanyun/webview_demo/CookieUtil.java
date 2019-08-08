package cn.kanyun.webview_demo;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * 如何手动添加 Cookie
 * 需要获得 CookieManager 的对象并将 cookie 设置进去。
 * 从服务器的返回头中取出 cookie 根据Http请求的客户端不同，获取 cookie 的方式也不同，请自行获取。
 */
public class CookieUtil {

    private Context context;

    /**
     * 将cookie设置到 WebView
     *
     * @param url    要加载的 url
     * @param cookie 要同步的 cookie
     */
    public void syncCookie(String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        /**
         * cookie 设置形式
         * cookieManager.setCookie(url, "key=value;" + "domain=[your domain];path=/;")
         **/
        cookieManager.setCookie(url, cookie);
    }

    /**
     * 这个两个在 API level 21 被抛弃
     * CookieManager.getInstance().removeSessionCookie();
     * CookieManager.getInstance().removeAllCookie();
     * <p>
     * 推荐使用这两个， level 21 新加的
     * CookieManager.getInstance().removeSessionCookies();
     * CookieManager.getInstance().removeAllCookies();
     **/
    public static void removeCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(MyApplication.getInstance());
            CookieSyncManager.getInstance().sync();
        }
    }
}
