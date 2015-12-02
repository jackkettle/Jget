package com.jget.core.utils.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jget.core.download.DownloadConfig;
import com.jget.core.utils.url.UrlUtils;

public class HtmlAnalyser {

    public static Set<URI> getAllValidLinks(Document document, URL baseUrl) {

        Set<URI> validLinks = new HashSet<URI>();

        Elements elements = document.select(DownloadConfig.LINK_SELECTOR);

        if (!elements.isEmpty())
            logger.info("Searching html file for links: {}", document.location());

        for (Element element : elements) {

            String link = element.attr("href");
            if (StringUtils.isEmpty(link))
                link = element.attr("src");

            if (StringUtils.isEmpty(link)) {
                logger.debug("Empty link");
                continue;
            }

            if (link.startsWith("//")) {
                logger.debug("Fixing protocal relative link: {}", link);
                link = "http:" + link;
            }

            if (UrlUtils.isEmailLink(link)) {
                logger.debug("Invalid link: {}", link);
                continue;
            }

            if (link.startsWith("#")) {
                logger.debug("Invalid link: {}", link);
                continue;
            }

            URI uri = null;
            try {
                uri = new URI(link);
            } catch (URISyntaxException e) {
                logger.debug("Failed to parse link: {}", link);
                continue;
            }

            String fullUri = "";

            if (!uri.isAbsolute()) {

                fullUri = handleNonAbsoluteLink(uri, baseUrl);

            } else {
                if (!UrlUtils.doesLinkContainSeed(uri.toString())) {
                    logger.debug("Invalid link: {}", uri.toString());
                    continue;
                }
                fullUri = uri.toString();
            }

            if(StringUtils.isEmpty(fullUri)){
                continue;
            }
            
            try {
                uri = new URI(fullUri);
            } catch (URISyntaxException e) {
                logger.info("Failed to parse link: {}", link);
                continue;
            }

            logger.debug("Adding valid link: {}", uri.toString());
            validLinks.add(uri);

        }
        return validLinks;
    }

    private static String handleNonAbsoluteLink(URI uri, URL baseUrl) {

        String fullUri = "";

        if (uri.toString().startsWith("/")) {
            Optional<URL> hostUrl = UrlUtils.getHostUrl(baseUrl);

            if (!hostUrl.isPresent()) {
                logger.debug("Unable to get host URL from link: {}", baseUrl.toString());
                return "";
            }

            fullUri = UrlUtils.concatLinks(hostUrl.get().toString(), uri.toString());
        } else {
            logger.debug("Concatenating links: {} - {}", baseUrl.toString(), uri.toString());
            fullUri = UrlUtils.concatLinks(baseUrl.toString(), uri.toString());
        }

        return fullUri;

    }

    private static final Logger logger = LoggerFactory.getLogger(HtmlAnalyser.class);

}
