package cn.kanyun.webview_demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class MyWebViewClient extends WebViewClient {


    CurrentShowUrlViewModel currentShowUrlViewModel;

    public MyWebViewClient(CurrentShowUrlViewModel currentShowUrlViewModel) {
        this.currentShowUrlViewModel = currentShowUrlViewModel;
    }

    /**
     * 当WebView得页面Scale值发生改变时回调
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    /**
     * 是否在 WebView 内加载页面
     *
     * @param view
     * @param url
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        在这里判断url类型,因为并不是所有的都是http类型,有一些是需要调用手机上的应用的
//        if (url.startsWith("baiduboxlite://")) {
//////            百度盒子
////        } else if (url.startsWith("baidumap://")) {
//////            百度地图
////            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////            startActivity(intent);
////            return true;
////        } else if (url.startsWith("weixin://") || url.startsWith("alipays://") ||
////                url.startsWith("tel://")) {
////            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////            startActivity(intent);
////            return true;
////        }


        try {
//        上面的都太麻烦了,直接判断是否是http或者https
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                return true;
            } else {
                //            其他自定义的scheme,像是电话/微信/支付宝/邮件/地图 等
                //            不是Http的url都走Intent
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        } catch (Exception e) {
//            防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
//            没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            return true;
        }

    }

    /**
     * WebView 开始加载页面时回调，一次Frame加载对应一次回调
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    /**
     * WebView 完成加载页面时回调，一次Frame加载对应一次回调
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        WebViewActivity.currentPageTitle = view.getTitle();
        currentShowUrlViewModel.modifyShowUrl(url);
        super.onPageFinished(view, url);
    }

    /**
     * WebView 加载页面资源时会回调，每一个资源产生的一次网络加载，除非本地有当前 url 对应有缓存，否则就会加载。
     *
     * @param view WebView
     * @param url  url
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    /**
     * WebView 可以拦截某一次的 request 来返回我们自己加载的数据，这个方法在后面缓存会有很大作用。
     *
     * @param view    WebView
     * @param request 当前产生 request 请求
     * @return WebResourceResponse
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    /**
     * WebView 访问 url 出错
     *
     * @param view
     * @param request
     * @param error
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
            error) {
        super.onReceivedError(view, request, error);
    }

    /**
     * WebView 访问https出错，handler.cancel()取消加载，handler.proceed()错误也继续加载
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
//        接受证书  默认的处理方式(handler.cancel();)，WebView变成空白页
        handler.proceed();
    }
}
