package com.jget.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jget.core.JgetApplication;
import com.jget.core.Manifest;

@Component
public class DownloadMediaTask implements Runnable {
    
    @Override
    public void run() {
        
    	Optional<File> mediaFile = saveFileFromURL(this.getUrl());
    	
    	if(!mediaFile.isPresent())
    		logger.info("Failed to download file form url: {}", this.getUrl());
    }
    
    public Optional<File> saveFileFromURL(URL url) {

        Path filePath = Paths.get(Manifest.getRootDir() + url.getFile());
        try {
            Files.createDirectories(filePath.getParent());
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(Manifest.getRootDir() + url.getFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
            logger.info("Failed create file from url: {}", url.toString());
        	return Optional.empty();
        }

        return Optional.of(filePath.toFile());
    }
    
    public DownloadMediaTask(URL url){
        this.url = url;
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
    
    private URL url;
    private DownloadStatus downloadStatus;
    
    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
