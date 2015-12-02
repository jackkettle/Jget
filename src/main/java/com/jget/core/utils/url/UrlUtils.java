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
            if (!hasLinkBeenProcessed(url.getUrl()))
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

    private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);

}
