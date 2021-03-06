package com.jget.core.utils.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jget.core.download.DownloadConfig;
import com.jget.core.download.ReferencedURL;
import com.jget.core.manifest.ManifestProvider;

public class UrlUtils {

    public static boolean isEmailLink(String link) {

        if (link.startsWith("mailto"))
            return true;

        return EmailValidator.getInstance().isValid(link);

    }

    public static String concatLinks(String base, String relative) {

        URI baseUri = null;

        try {
            baseUri = new URI(base);
        } catch (URISyntaxException e) {
            logger.info("Failed to convert URL to URI: {}", base);
            return "";
        }

        if (StringUtils.isEmpty(base) && StringUtils.isEmpty(relative))
            return "";

        if (StringUtils.isEmpty(base) && !StringUtils.isEmpty(relative))
            return relative;

        if (!StringUtils.isEmpty(base) && StringUtils.isEmpty(relative))
            return base;

        if (base.endsWith("/") && relative.startsWith("/"))
            return base + relative.substring(1);

        if (base.endsWith("/") && !relative.startsWith("/"))
            return base + relative;

        if (!base.endsWith("/") && relative.startsWith("/")) {

            if (baseUri.isAbsolute()) {
                return base + relative;
            }

            else {
                return base.substring(0, base.lastIndexOf("/")) + relative;
            }
        }

        if (base.contains("/"))
            return base.substring(0, base.lastIndexOf("/")) + "/" + relative;

        return base + "/" + relative;

    }

    public static boolean doesLinkContainSeed(String url) {

        for (URI seed : ManifestProvider.getCurrentManifest().getSeeds()) {
            if (url.contains(seed.toString())) {
                return true;
            }
        }
        return false;

    }

    public static Set<URL> removeProcessLinks(Set<URL> pageLinksUrl) {

        Set<URL> newLinks = new HashSet<URL>();
        for (URL url : pageLinksUrl) {
            if (!ManifestProvider.getCurrentManifest().getLinkMap().containsKey(url))
                newLinks.add(url);
        }
        return newLinks;
    }

    public static Set<ReferencedURL> removeProcessReferencedLinks(Set<ReferencedURL> pageLinksUrl) {

        Set<ReferencedURL> newLinks = new HashSet<ReferencedURL>();
        for (ReferencedURL url : pageLinksUrl) {
            if (!hasLinkBeenProcessed(url.getURL()))
                newLinks.add(url);
        }
        return newLinks;
    }

    public static boolean hasLinkBeenProcessed(URL url) {
        return ManifestProvider.getCurrentManifest().getLinkMap().containsKey(url);
    }

    public static Optional<URL> getHostUrl(URL baseUrl) {
        String fullURL = "";
        String hostName = baseUrl.getHost();

        if (baseUrl.getProtocol().trim().toLowerCase().equals(DownloadConfig.HTTP))
            fullURL = DownloadConfig.HTTP + "://" + hostName;

        if (baseUrl.getProtocol().trim().toLowerCase().equals(DownloadConfig.HTTPS))
            fullURL = DownloadConfig.HTTPS + "://" + hostName;

        URL url = null;
        try {
            url = new URL(fullURL);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
        return Optional.of(url);

    }

    public static boolean exceedsUrlDepth(URL url) {

        Optional<URL> hostURL = UrlUtils.getHostUrl(url);

        String hostURLString = "";
        String relativePath = url.toString();

        if (hostURL.isPresent()) {
            hostURLString = hostURL.get().toString();
            relativePath = url.toString().replace(hostURLString.toString(), "");
        }

        int count = 0;
        if (!relativePath.endsWith("/"))
            count = -1;

        for (String node : relativePath.split("/")) {
            if (StringUtils.isEmpty(node))
                continue;

            count++;
        }

        if (count > DownloadConfig.URL_DEPTH)
            return true;

        return false;
    }

    private static Optional<String> handleNonAbsoluteLink(URI uri, URL baseUrl) {

        if (uri.toString().startsWith("/")) {
            Optional<URL> hostUrl = UrlUtils.getHostUrl(baseUrl);

            if (!hostUrl.isPresent()) {
                logger.debug("Unable to get host URL from link: {}", baseUrl.toString());
                return Optional.empty();
            }

            String fullUri = UrlUtils.concatLinks(hostUrl.get().toString(), uri.toString());
            return Optional.of(fullUri);

        } else {
            String fullUri = UrlUtils.concatLinks(baseUrl.toString(), uri.toString());
            return Optional.of(fullUri);
        }

    }

    public static Optional<URI> normalizeLink(String linkString, URL baseUrl) {

        if (StringUtils.isEmpty(linkString)) {
            logger.debug("Invalid link - Empty link");
            return Optional.empty();
        }

        if (UrlUtils.isEmailLink(linkString)) {
            logger.debug("Invalid link - Email link: {}", linkString);
            return Optional.empty();
        }

        if (linkString.startsWith("#")) {
            logger.debug("Invalid link - Anchor link: {}", linkString);
            return Optional.empty();
        }

        if (linkString.startsWith("//")) {
            logger.debug("Fixing protocal relative link: {}", linkString);
            linkString = "http:" + linkString;
        }

        URI uri = null;
        try {
            uri = new URI(linkString);
        } catch (URISyntaxException e) {
            logger.debug("Invalid link - Failed to parse link: {}", linkString);
            return Optional.empty();
        }

        Optional<String> fullURI = null;

        if (uri.isAbsolute()) {
            fullURI = Optional.of(uri.toString());
        } else {
            fullURI = handleNonAbsoluteLink(uri, baseUrl);
        }

        if (!fullURI.isPresent())
            return Optional.empty();

        try {
            uri = new URI(fullURI.get());
        } catch (URISyntaxException e) {
            logger.debug("Invalid link - Failed to parse link: {}", linkString);
            return Optional.empty();
        }

        return Optional.of(uri);

    }

    public static Optional<URL> convertDynamicLinkToStatic(URL dynamicLink) {

        String urlString = dynamicLink.toString();
        String lastFragment = urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length());
        String remainingUrl = urlString.substring(0, urlString.lastIndexOf("/") + 1);

        if (urlString.contains("?"))
            lastFragment = lastFragment.substring(0, lastFragment.lastIndexOf("?"));

        String staticString = remainingUrl + lastFragment;

        try {
            return Optional.of(new URL(staticString));
        } catch (MalformedURLException e) {
            logger.error("Issue with following link: {}", staticString, e);
            return Optional.empty();
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);

    public static Elements getApprovedURLs(Elements linkElements, URL baseURL) {

        Elements approvedLinkElements = new Elements();

        for (Element linkElement : linkElements) {
            String linkPathString = linkElement.attr("href");

            if (StringUtils.isEmpty(linkPathString))
                linkPathString = linkElement.attr("src");

            if (StringUtils.isEmpty(linkPathString))
                continue;

            Optional<URI> uriWrapper = UrlUtils.normalizeLink(linkPathString, baseURL);
            if (!uriWrapper.isPresent())
                continue;

            if (UrlUtils.doesLinkContainSeed(uriWrapper.get().toString())) {
                approvedLinkElements.add(linkElement);
            }
        }

        return approvedLinkElements;
    }

    public static String generateDynamicFileName(String fileName) {

        logger.info("Generating dynamic filename for {}", fileName);

        String fileNameNoExtension = "";
        String extension = "";
        String staticFileName = "";

        if (fileName.contains("?"))
            staticFileName = fileName.substring(0, fileName.indexOf('?'));

        if (fileName.contains("#"))
            staticFileName = fileName.substring(0, fileName.indexOf('#'));

        if (StringUtils.isEmpty(staticFileName))
            staticFileName = DownloadConfig.DEFAULT_INDEX_FILENAME;

        fileNameNoExtension = FilenameUtils.getBaseName(staticFileName);
        extension = FilenameUtils.getExtension(staticFileName);

        if (StringUtils.isEmpty(fileNameNoExtension))
            extension = DownloadConfig.INDEX;

        if (StringUtils.isEmpty(extension))
            extension = DownloadConfig.DEFAULT_HTML_EXTENSION;

        String returnString = "";

        String uniqueHash = RandomStringUtils.randomAlphanumeric(DownloadConfig.FILENAME_DYMANIC_MARKER_LENGTH);

        logger.info("Unique hash: {}", uniqueHash);

        while (ManifestProvider.getCurrentManifest().getUniqueIDs().contains(uniqueHash)) {
            uniqueHash = RandomStringUtils.randomAlphanumeric(DownloadConfig.FILENAME_DYMANIC_MARKER_LENGTH);
            logger.info("Unique hash: {}", uniqueHash);
        }

        ManifestProvider.getCurrentManifest().getUniqueIDs().add(uniqueHash);

        returnString = fileNameNoExtension + "-" + uniqueHash + "." + extension;
        logger.info("Dynmaic name generated: {}", returnString);
        return returnString;

    }

    public static String getFilePathFromURL(URL url, Path seedPath) {

        String filePathString = url.getFile();

        if (StringUtils.isEmpty(filePathString))
            return DownloadConfig.DEFAULT_INDEX_FILENAME;

        String filePathParentString = filePathString.substring(0, filePathString.lastIndexOf('/') + 1);
        String fileName = filePathString.substring(filePathString.lastIndexOf('/') + 1, filePathString.length());

        if (StringUtils.isEmpty(filePathParentString))
            filePathParentString = DownloadConfig.DEFAULT_INDEX_FILENAME;

        if (filePathParentString.startsWith("/"))
            filePathParentString = filePathParentString.substring(1);

        if (fileName.contains("?"))
            fileName = generateDynamicFileName(fileName);

        String fileExtension = FilenameUtils.getBaseName(fileName);

        if (StringUtils.isEmpty(fileExtension) || !fileExtension.toLowerCase().equals(DownloadConfig.DEFAULT_HTML_EXTENSION))
            fileExtension = DownloadConfig.DEFAULT_HTML_EXTENSION;

        if (StringUtils.isEmpty(fileName))
            fileName = DownloadConfig.INDEX;

        String returnString = filePathParentString + fileName + "." + fileExtension;

        return returnString;
    }
}
