package cn.kanyun.webview_demo;

import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载
 */
public class FileDownLoadListener implements DownloadListener {
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
