package com.jget.core.utils.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jget.core.ManifestProvider;
import com.jget.core.download.DownloadConfig;
import com.jget.core.download.ReferencedURL;

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

        for (URI seed : ManifestProvider.getManifest().getSeeds()) {
            if (url.contains(seed.toString())) {
                return true;
            }
        }
        return false;

    }

    public static Set<URL> removeProcessLinks(Set<URL> pageLinksUrl) {

        Set<URL> newLinks = new HashSet<URL>();
        for (URL url : pageLinksUrl) {
            if (!ManifestProvider.getManifest().getLinkMap().containsKey(url))
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
        return ManifestProvider.getManifest().getLinkMap().containsKey(url);
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
        if(!relativePath.endsWith("/"))
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

            if (!UrlUtils.doesLinkContainSeed(uri.toString())) {
                logger.debug("Invalid link - not a seed host: {}", uri.toString());
                return Optional.empty();
            }

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


    private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);

}
