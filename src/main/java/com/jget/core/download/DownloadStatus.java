package com.jget.core.download;

public class DownloadStatus {

    private int size;
    private int downloaded;
    private int status;

    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    public static final String STATUSES[] = { "Downloading", "Paused", "Complete", "Cancelled", "Error" };

    public DownloadStatus(int size) {
        super();
        this.size = size;
        this.downloaded = 0;
        this.status = DOWNLOADING;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}