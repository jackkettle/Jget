package com.jget.core.linkresolver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

import com.jget.core.ManifestProvider;
import com.jget.core.download.DownloadConfig;
import com.jget.core.utils.file.FileSystemUtils;

public class LinkResolverManager {

    public void commenceResolving() {

        logger.info(DownloadConfig.LINE_BREAK);
        logger.info("Beginning link resolution");
        logger.info(DownloadConfig.LINE_BREAK);
        List<Path> htmlFiles = null;
        try {
            htmlFiles = FileSystemUtils.populateFilesList(ManifestProvider.getManifest().getRootDir());
        } catch (IOException e) {
            logger.info("Failed to populate file list", e);
            return;
        }
        logger.info("Total number of files to search: {}", htmlFiles.size());

        for (Path file : htmlFiles) {
            logger.info("Processing file {}", file.toString());
            Optional<Document> documentWrapper = processFile(file);

            if (documentWrapper.isPresent()) {
                logger.debug("Updating file {}", file.toString());
                updateFile(file, documentWrapper.get());
            }

        }
    }

    private Optional<Document> processFile(Path file) {
        Document documnent = null;
        try {
            documnent = Jsoup.parse(file.toFile(), "UTF-8");
        } catch (IOException e) {
            logger.error("Failed to parse file", e);
            return Optional.empty();
        }

        Elements linkElements = documnent.select("a");

        boolean fileModified = false;

        for (Element linkElement : linkElements) {

            Optional<Path> linkPathWrapper = processLinkElement(linkElement);

            if (!linkPathWrapper.isPresent())
                continue;

            fileModified = true;

            Path relativePath = file.relativize(linkPathWrapper.get());
            String relativePathString = relativePath.toString().replace("\\", "/");
            logger.info("Relative path {}\n", relativePathString);
            linkElement.attr("href", relativePathString);
        }

        if (fileModified)
            return Optional.of(documnent);

        return Optional.empty();

    }

    private Optional<Path> processLinkElement(Element linkElement) {
        String linkPathString = linkElement.attr("href");

        URL elementUrl = null;
        try {
            elementUrl = new URL(linkPathString);
        } catch (MalformedURLException e) {
            logger.debug("Failed to conver the string to a url: {}", linkPathString);
            return Optional.empty();
        }

        Path linkPath = ManifestProvider.getManifest().getLinkMap().get(elementUrl);

        if(linkPath == null)
            return Optional.empty();
        
        return Optional.of(linkPath);
    }

    private void updateFile(Path file, Document document) {
        File htmlFile = file.toFile();

        InputStream documentStream = new ByteArrayInputStream(document.toString().getBytes(StandardCharsets.UTF_8));

        if (htmlFile.isDirectory()) {
            logger.info("File is a directory, skipping: {}", file.toString());
            return;
        }

        try {
            logger.info("Saving file: {}", htmlFile.toString());
            Files.copy(documentStream, htmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Unable to update file {}", htmlFile.toString(), e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(LinkResolverManager.class);

}
