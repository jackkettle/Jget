package com.jget.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jget.core.ManifestProvider;
import com.jget.core.utils.file.FileSystemUtils;

@Component
public class DownloadMediaTask implements Runnable, DownloadTask {

    @Override
    public void run() {
        
        Optional<File> mediaFile = saveFileFromURL(this.getReferencedURL().getURL());

        if (!mediaFile.isPresent()){
            logger.info("Failed to download file form url: {}", this.getReferencedURL().getURL());
            return;
        }

        ManifestProvider.getManifest().getLinkMap().put(this.getReferencedURL().getURL(), mediaFile.get().toPath());
    
    }

    public Optional<File> saveFileFromURL(URL url) {

        Path seedPath = ManifestProvider.getManifest().getRootDir().resolve(url.getHost());

        if (!FileSystemUtils.pathExists(seedPath)) {
            logger.error("Seed path for url does not exist: {}", seedPath);
            return Optional.empty();
        }

        logger.info("Seed path: {}", seedPath);
        String filePath = url.getFile();
        
        if(filePath.startsWith("/"))
            filePath = filePath.substring(1);
        
        logger.info("Relative path: {}", filePath);
        
        Path fullFilePath = seedPath.resolve(filePath);
        logger.info("Creating path: {}", fullFilePath);

        try {
            Files.createDirectories(fullFilePath.getParent());
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(fullFilePath.toString());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
            logger.info("Failed to create file from url: {}", url.toString());
            return Optional.empty();
        }

        logger.info("Successfully saved file to location: {}\n", fullFilePath.toString());
        return Optional.of(fullFilePath.toFile());
    }

    public DownloadMediaTask() {

    }

    public DownloadMediaTask(ReferencedURL referencedURL) {
        super();
        this.referencedURL = referencedURL;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public ReferencedURL getReferencedURL() {
        return referencedURL;
    }

    public void setUrl(ReferencedURL referencedURL) {
        this.referencedURL = referencedURL;
    }

    private ReferencedURL referencedURL;
    private DownloadStatus downloadStatus;

    private static final Logger logger = LoggerFactory.getLogger(DownloadMediaTask.class);

}
