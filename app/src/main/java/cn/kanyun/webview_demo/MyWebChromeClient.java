package cn.kanyun.webview_demo;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;


public class MyWebChromeClient extends WebChromeClient {

    ProgressBar progressBar;

    public MyWebChromeClient(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * 获取网页的图标
     *
     * @param view
     * @param icon
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    /**
     * 接收web页面的 Title
     * 获取网页的标题
     *
     * @param view
     * @param icon
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    /**
     * 如何使 HTML5 video 在 WebView 全屏显示
     * 网页全屏播放视频时会调用
     * WebChromeClient.onShowCustomView() 方法
     * 所以可以通过将 video 播放的视图全屏达到目的
     * @param view
     * @param callback
     */
//    @Override
//    public void onShowCustomView(View view, CustomViewCallback callback) {
//        super.onShowCustomView(view, callback);
//        if (view instanceof FrameLayout && fullScreenView != null) {
//            // A video wants to be shown
//            this.videoViewContainer = (FrameLayout) view;
//            this.videoViewCallback = callback;
//            fullScreenView.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            fullScreenView.setVisibility(View.VISIBLE);
//            isVideoFullscreen = true;
//        }
//    }

//    @Override
//    public void onHideCustomView() {
//        super.onHideCustomView();
//        if (isVideoFullscreen && fullScreenView != null) {
//            // Hide the video view, remove it, and show the non-video view
//            fullScreenView.setVisibility(View.INVISIBLE);
//            fullScreenView.removeView(videoViewContainer);
//
//            // Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
//            if (videoViewCallback != null && !videoViewCallback.getClass().getName().contains(".chromium.")) {
//                videoViewCallback.onCustomViewHidden();
//            }
//
//            isVideoFullscreen = false;
//            videoViewContainer = null;
//            videoViewCallback = null;
//        }
//    }

    /**
     * 当前 WebView 加载网页进度
     * 怎么为 WebView 的加载添加进度条
     * onPageFinished() 有个问题，不能在这里监听页面是否加载完毕（我自己测试的时候，好像在重定向和加载完 iframes 时都会调用这个方法）
     * 把页面加载完毕的判断放在 onProgressChanged() 里可能会更为准确
     *
     * @param view
     * @param position
     */
    @Override
    public void onProgressChanged(WebView view, int position) {
        if (position == 100) {
//            加载完网页进度条消失
            progressBar.setVisibility(View.GONE);
        } else {
//            开始加载网页时显示进度条
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(position);
        }
        super.onProgressChanged(view, position);
    }


    /**
     * 输出 Web 端日志
     *
     * @param consoleMessage
     * @return
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    /**
     * 处理 JS 中的 Prompt对话框
     *
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * Js 中调用 alert() 函数，产生的对话框
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }
}
