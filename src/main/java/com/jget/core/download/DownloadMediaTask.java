package com.jget.core.download;

import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class DownloadMediaTask implements Runnable {
    
    private URL url;
    private DownloadStatus downloadStatus;
    
    public DownloadMediaTask(URL url){
        this.url = url;
    }
    
    @Override
    public void run() {
        
        
        
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
