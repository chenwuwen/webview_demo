package cn.kanyun.webview_demo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.orhanobut.logger.Logger;

/**
 * 下载广播
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("DownloadReceiver接收到广播：" + intent.getAction());
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//            下载完成
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            installApk(context, id, Environment.DIRECTORY_DOWNLOADS + "/sub/k2.apk");
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            //处理 如果还未完成下载，用户点击Notification ，跳转到下载中心
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }

    /**
     * 启动安装
     *
     * @param context
     * @param downloadApkId
     * @param apkPath
     */
    private static void installApk(Context context, long downloadApkId, String apkPath) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        if (downloadFileUri != null) {
            Logger.d("DownloadManager", downloadFileUri.toString());
            Intent install = new Intent(Intent.ACTION_VIEW);
//            install.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } else {
            Logger.e("DownloadManager", "download error");
        }
    }
}
