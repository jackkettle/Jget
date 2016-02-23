package com.jget.core.linkresolver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jget.core.ManifestProvider;
import com.jget.core.download.DownloadConfig;
import com.jget.core.utils.file.FileSystemUtils;
import com.jget.core.utils.url.UrlUtils;

public class LinkResolverManager {

    public void commenceResolving() {

        logger.info(DownloadConfig.LINE_BREAK);
        logger.info("Beginning link resolution");
        logger.info(DownloadConfig.LINE_BREAK);
        
        List<Path> htmlFiles = null;
        try {
            htmlFiles = FileSystemUtils.populateFilesList(ManifestProvider.getManifest().getRootDir(), DownloadConfig.DEFAULT_HTML_EXTENSION);
        } catch (IOException e) {
            logger.info("Failed to populate file list", e);
            return;
        }
        logger.info("Total number of files to search: {}", htmlFiles.size());

        for (Path file : htmlFiles) {
            logger.info("Processing file {}", file.toString());
            Optional<Document> documentWrapper = processFile(file);

            if (documentWrapper.isPresent()) {
                updateFile(file, documentWrapper.get());
            }

        }
    }

    private Optional<Document> processFile(Path file) {
        
        URL baseURL = ManifestProvider.getManifest().getFileMap().get(file);
        if(baseURL == null){
            logger.info("Failed to resolve links in file, no baseURL: {}\n", file);
            return Optional.empty();
        }
        
        Document documnent = null;
        try {
            documnent = Jsoup.parse(file.toFile(), "UTF-8");
        } catch (IOException e) {
            logger.error("Failed to parse file", e);
            return Optional.empty();
        }

        Elements linkElements = documnent.select(LINKS_SELECTOR);

        boolean fileModified = false;
        int count = 0;
        for (Element linkElement : linkElements) {
            Optional<Path> linkPathWrapper = processLinkElement(linkElement, baseURL);

            if (!linkPathWrapper.isPresent())
                continue;

            fileModified = true;
            count++;
            Path relativePath = file.getParent().relativize(linkPathWrapper.get());
            String relativePathString = relativePath.toString().replace("\\", "/");
            
            if(StringUtils.isEmpty(linkElement.attr("href")))
                linkElement.attr("src", relativePathString);
            else   
                linkElement.attr("href", relativePathString);
        }

        Elements approvedLinkElements = UrlUtils.getApprovedURLs(linkElements, baseURL);
        
        logger.info("Fixed links | Approved links | Total links - {} | {} | {}\n", count, approvedLinkElements.size(), linkElements.size());
        
        if (fileModified){
            return Optional.of(documnent);
        }
            
        return Optional.empty();

    }

    private Optional<Path> processLinkElement(Element linkElement, URL baseURL) {
        String linkPathString = linkElement.attr("href");

        if(StringUtils.isEmpty(linkPathString))
            linkPathString = linkElement.attr("src");
        
        if(StringUtils.isEmpty(linkPathString))
            return Optional.empty();
        
        logger.debug("Url in link: {}", linkPathString);
        
        Optional<URI> uriWrapper = UrlUtils.normalizeLink(linkPathString, baseURL);
        if(!uriWrapper.isPresent())
            return Optional.empty();
        
        logger.debug("Normalized link: {}", uriWrapper.get().toString());
        
        if (!UrlUtils.doesLinkContainSeed(uriWrapper.get().toString())) {
            logger.debug("Invalid link - not a seed host: {}", uriWrapper.get().toString());
            return Optional.empty();
        }
        
        URL urlFromMap = null;
        try {
            urlFromMap = uriWrapper.get().toURL();
        } catch (MalformedURLException e) {
            logger.error("Issue converting URI to URL: {}", uriWrapper.get().toString());
            return Optional.empty();
        }
        
        Path linkPath = ManifestProvider.getManifest().getLinkMap().get(urlFromMap);

        if (linkPath == null){
            logger.debug("No path found for link: {}", uriWrapper.get().toString());
            return Optional.empty();
        }
            
        logger.debug("Found path: {}", linkPath.toString());
        
        return Optional.of(linkPath);
    }

    private void updateFile(Path file, Document document) {
        File htmlFile = file.toFile();

        InputStream documentStream = new ByteArrayInputStream(document.toString().getBytes(StandardCharsets.UTF_8));

        if (htmlFile.isDirectory()) {
            logger.debug("File is a directory, skipping: {}", file.toString());
            return;
        }

        try {
            logger.info("Saving file: {}\n", htmlFile.toString());
            Files.copy(documentStream, htmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Unable to update file {}", htmlFile.toString(), e);
        }
    }
    
    private static final String LINKS_SELECTOR = "link, style, a, img, audio, video, script, iframe";

    private static final Logger logger = LoggerFactory.getLogger(LinkResolverManager.class);

}
