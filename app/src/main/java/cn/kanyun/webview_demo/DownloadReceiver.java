package cn.kanyun.webview_demo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载广播
 */
public class DownloadReceiver extends BroadcastReceiver {

    private static Map<Long, String> downLoadEntities = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("DownloadReceiver接收到广播：" + intent.getAction());

        if (intent.getAction().equals(FileDownLoadListener.DOWNLOAD_FILE_NAME_ACTION)) {
//            发送下载相关信息的广播,这个广播是自己写的广播
            DownLoadEntity downLoadEntity = (DownLoadEntity) intent.getSerializableExtra("file");
            downLoadEntities.put(downLoadEntity.getDownloadId(), downLoadEntity.getDownloadFileName());
        } else if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//            下载完成(这个广播是系统发送的)
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            获取下载的文件名
            String fileName = downLoadEntities.get(id);
            installApk(context, id, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + fileName);
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
        Uri uri = Uri.fromFile(new File(apkPath));
        downLoadEntities.remove(downloadApkId);
        if (uri != null) {
            InstallUtil.install(context, apkPath, false);
        } else {
            Logger.e("DownloadManager", "download error");
        }
    }
}
