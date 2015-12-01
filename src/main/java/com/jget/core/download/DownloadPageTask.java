package com.jget.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.jget.core.ManifestProvider;
import com.jget.core.utils.file.FileSystemUtils;
import com.jget.core.utils.html.HtmlAnalyser;
import com.jget.core.utils.url.UrlUtils;

@Component
public class DownloadPageTask implements Runnable, DownloadTask {

    @Override
    public void run() {

        Optional<File> mediaFile = saveFileFromURL(this.getUrl());

        if (!mediaFile.isPresent()) {
            logger.info("Failed to download file form url: {}", this.getUrl());
            return;
        }

        ManifestProvider.getManifest().getLinkMap().put(this.getUrl(), mediaFile.get().toPath());
        
        Document document = null;
        try {
            document = Jsoup.parse(mediaFile.get(), "UTF-8");
        } catch (IOException e) {
            logger.info("Invalid html file: {}", this.getUrl());
            return;
        }

        Set<URI> pageLinks = HtmlAnalyser.getAllValidLinks(document, this.getUrl());
        logger.info("Total links found on page: {}", pageLinks.size());

        Set<URL> pageLinksUrl = FluentIterable.from(pageLinks).transform(new Function<URI, URL>() {
            @Override
            public URL apply(URI input) {
                try {
                    return input.toURL();
                } catch (MalformedURLException e) {
                    logger.info("Failed to convert URL to URI: {}", input.toString());
                }
                return null;
            }
        }).toSet();

        Set<URL> newPageLinksUrl = UrlUtils.removeProcessLinks(pageLinksUrl);
        logger.info("Total new links found: {}", newPageLinksUrl.size());
        ManifestProvider.getManifest().getFrontier().addAll(newPageLinksUrl);
        
    }

    public Optional<File> saveFileFromURL(URL url) {

        String fileName = "";
        
        if (StringUtils.isEmpty(url.getFile()) 
                || url.getFile().equals("/") 
                || url.getFile().charAt(url.getFile().length() - 1) == '/') {
            fileName = "index.html";
        }

        Path seedPath = ManifestProvider.getManifest().getRootDir().resolve(url.getHost());

        if (!FileSystemUtils.pathExists(seedPath)) {
            logger.error("Seed path for url does not exist: {}", seedPath);
            return Optional.empty();
        }

        logger.info("Seed path: {}", seedPath);
        String filePath = url.getFile() + fileName;

        if (filePath.startsWith("/"))
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
            logger.error("Failed to create file from url: {}", url.toString(), e);
            return Optional.empty();
        }

        logger.info("Successfully saved file to location: {}\n", fullFilePath.toString());
        return Optional.of(fullFilePath.toFile());
    }

    public DownloadPageTask() {

    }

    public DownloadPageTask(URL url) {
        super();
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

    private static final Logger logger = LoggerFactory.getLogger(DownloadPageTask.class);

}
