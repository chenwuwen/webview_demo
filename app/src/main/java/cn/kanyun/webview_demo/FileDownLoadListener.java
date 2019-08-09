package cn.kanyun.webview_demo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.webkit.DownloadListener;

import com.blankj.utilcode.util.ToastUtils;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载
 * WebView 默认是不支持下载的，需要开发者自己实现:
 * 主要思路有这么几种：
 * 1.跳转浏览器下载
 * 2.使用系统的下载服务
 * 3.自定义下载任务
 */
public class FileDownLoadListener implements DownloadListener {


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

//         调用浏览器下载
//        Uri uri = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);

        String fileName = url.substring(url.lastIndexOf("/") + 1);
//        使用系统下载服务下载
        downloadBySystem(url, "正在下载", BuildConfig.APPLICATION_ID, fileName);
//        发送下载广播 (系统自动发广播，所以我们只需要做两件事,1.编写广播接收者，2.是注册广播接收者)
//        当Android DownloadManager下载某一个任务完成时候，可以立即获得下载任务完成的消息通知。
//        Android DownloadManager通过注册一个广播监听系统的广播事件完成此操作，在创建广播时候，
//        需要指明过滤器为：DownloadManager.ACTION_DOWNLOAD_COMPLETE

    }


    /**
     * 系统下载服务
     *
     * @param url
     * @param contentDisposition
     * @param mimeType
     */
    public long downloadBySystem(String url, String title, String desc, String subPath) {
        DownloadManager downloadManager = (DownloadManager) MyApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        long ID;

        //以下两行代码可以让下载的apk文件被直接安装而不用使用FileProvider,系统7.0或者以上才启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(localBuilder.build());
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        // 仅允许在WIFI连接情况下下载
//        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);


        // 通知栏中将出现的内容
        request.setTitle(title);
        request.setDescription(desc);

        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }

        //制定下载的文件类型为APK
        request.setMimeType("application/vnd.android.package-archive");

        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath);
//        另外可选一下方法，自定义下载路径
//        指定下载文件地址，使用这个指定地址可不需要WRITE_EXTERNAL_STORAGE权限。
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()

        //大于11版本手机允许扫描
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //表示允许MediaScanner扫描到这个文件，默认不允许。
            request.allowScanningByMediaScanner();
        }

        //启动系统下载界面(这会使页面跳到文件管理中的DownLoad目录)
//        startActivity(new android.content.Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        ID = downloadManager.enqueue(request);
        ToastUtils.showLong("正在下载,下载ID:" + ID);
        return ID;
    }


    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(long downloadId) {
        DownloadManager dm = (DownloadManager) MyApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
