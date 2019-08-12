package cn.kanyun.webview_demo;

import java.io.Serializable;

public class DownLoadEntity implements Serializable {

    private long downloadId;
    private String downloadFileName;

    public DownLoadEntity(long downloadId, String downloadFileName) {
        this.downloadId = downloadId;
        this.downloadFileName = downloadFileName;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }
}
