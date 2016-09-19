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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jget.core.manifest.ManifestProvider;
import com.jget.core.utils.html.HtmlAnalyser;
import com.jget.core.utils.html.HtmlUtils;
import com.jget.core.utils.url.UrlUtils;

@Component
public class DownloadPageTask implements Runnable, DownloadTask {

    @Override
    public void run() {

        logger.info("Downloading link: {}", this.getReferencedURL().getURL());
        logger.info("Found on page at: {}", this.getReferencedURL().getLocation());

        Optional<File> mediaFile = saveFileFromURL(this.getReferencedURL().getURL());

        if (!mediaFile.isPresent()) {
            logger.info("Failed to download file form url: {}", this.getReferencedURL().getURL());
            logger.info("Failed file origin url: {}", this.getReferencedURL().getLocation());
            return;
        }

        ManifestProvider.getCurrentManifest().getLinkMap().put(this.getReferencedURL().getURL(), mediaFile.get().toPath());

        Optional<URL> staticLinkWrapper = UrlUtils.convertDynamicLinkToStatic(this.getReferencedURL().getURL());
        if (staticLinkWrapper.isPresent())
            ManifestProvider.getCurrentManifest().getFileMap().put(mediaFile.get().toPath(), staticLinkWrapper.get());

        Document document = null;
        try {
            document = Jsoup.parse(mediaFile.get(), "UTF-8");
        } catch (IOException e) {
            logger.info("Invalid html file: {}", this.getReferencedURL().getURL());
            return;
        }

        HtmlUtils.removeComments(document);

        Set<URI> pageLinks = HtmlAnalyser.getAllValidLinks(document, this.getReferencedURL().getURL());
        logger.info("Total links found on page: {}", pageLinks.size());

        Set<ReferencedURL> referencedPageUrls = new HashSet<>();
        for (URI uri : pageLinks) {
            ReferencedURL referencedURL = new ReferencedURL();
            referencedURL.setLocation(this.getReferencedURL().getURL().toString());
            try {
                referencedURL.setURL(uri.toURL());
            } catch (IllegalArgumentException | MalformedURLException e) {
                logger.info("Issue converting URI to URL: {}", uri.toString());
                continue;
            }
            referencedPageUrls.add(referencedURL);
        }

        Set<ReferencedURL> newPageLinksUrl = UrlUtils.removeProcessReferencedLinks(referencedPageUrls);
        logger.info("Total new links found: {}\n", newPageLinksUrl.size());
        ManifestProvider.getCurrentManifest().getFrontier().addAll(newPageLinksUrl);

    }

    public Optional<File> saveFileFromURL(URL url) {

        boolean containsSeed = false;
        String urlSeed = url.getHost() + url.getPath();
        for (URI seedString : ManifestProvider.getCurrentManifest().getSeeds()) {
            if (urlSeed.startsWith(seedString.toString()))
                containsSeed = true;
        }

        if (!containsSeed) {
            logger.error("Seed path for url does not exist: {}", urlSeed);
            return Optional.empty();
        }

        Path seedPath = ManifestProvider.getCurrentManifest().getRootDir().resolve(url.getHost());

        String filePath = UrlUtils.getFilePathFromURL(url, seedPath);

        if (StringUtils.isEmpty(filePath))
            return Optional.empty();

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

    public DownloadPageTask(ReferencedURL referencedURL) {
        super();
        this.referencedURL = referencedURL;
    }

    public DownloadFileStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadFileStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public ReferencedURL getReferencedURL() {
        return referencedURL;
    }

    public void setReferencedURL(ReferencedURL referencedURL) {
        this.referencedURL = referencedURL;
    }

    private ReferencedURL referencedURL;
    private DownloadFileStatus downloadStatus;

    private static final Logger logger = LoggerFactory.getLogger(DownloadPageTask.class);

}
