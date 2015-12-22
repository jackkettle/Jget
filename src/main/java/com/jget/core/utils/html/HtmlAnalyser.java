package com.jget.core.utils.html;

import java.net.URI;
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
            if (StringUtils.isEmpty(link)) {
                link = element.attr("src");
                if (StringUtils.isEmpty(link))
                    continue;
            }

            Optional<URI> uriWrapper = UrlUtils.normalizeLink(link, baseUrl);

            if(!uriWrapper.isPresent())
                continue;
            
            logger.debug("Adding valid link: {}", uriWrapper.get().toString());
            validLinks.add(uriWrapper.get());

        };
        return validLinks;
    }

    private static final Logger logger = LoggerFactory.getLogger(HtmlAnalyser.class);

}
