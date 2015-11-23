package com.jget.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jget.core.ManifestProvider;
import com.jget.core.utils.html.HtmlAnalyser;
import com.jget.core.utils.url.UrlUtils;

@Component
public class DownloadPageTask implements Runnable, DownloadTask {

    @Override
    public void run() {

        Optional<File> mediaFile = saveFileFromURL(this.getUrl());

        if (!mediaFile.isPresent())
            logger.info("Failed to download file form url: {}", this.getUrl());

        Document document = null;
        try {
            document = Jsoup.parse(mediaFile.get(), "UTF-8");
        } catch (IOException e) {
            logger.info("Invalid html file: {}", this.getUrl());
            return;
        }

        HtmlAnalyser.getAllValidLinks(document, this.getUrl());

    }

    public Optional<File> saveFileFromURL(URL url) {

        String fileName = "";

        if (StringUtils.isEmpty(url.getFile()) || url.getFile().equals("/")) {
            fileName = "index.html";
        }

        Path filePath = Paths.get(ManifestProvider.getManifest().getRootDir() + url.getFile() + fileName);
        logger.info("Blah: {}", filePath);

        try {
            Files.createDirectories(filePath.getParent());
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(ManifestProvider.getManifest().getRootDir() + url.getFile() + fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
            logger.error("Failed create file from url: {}", url.toString(), e);
            return Optional.empty();
        }

        return Optional.of(filePath.toFile());
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
