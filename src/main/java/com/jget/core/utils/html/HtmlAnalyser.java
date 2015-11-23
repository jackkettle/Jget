package com.jget.core.utils.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.jget.core.ManifestProvider;
import com.jget.core.download.DownloadConfig;
import com.jget.core.utils.url.UrlUtils;

public class HtmlAnalyser {

	public static List<URI> getAllValidLinks(Document document, URL baseUrl) {

		List<URI> validLinks = new ArrayList<URI>();

		Elements elements = document.select(DownloadConfig.LINK_SELECTOR);

		if (!elements.isEmpty())
			logger.info("Searching html file for links: {}", document.location());

		for (Element element : elements) {

			String link = element.attr("href");
			if (StringUtils.isEmpty(link))
				link = element.attr("src");
			
			if (StringUtils.isEmpty(link)){
				logger.info("Empty link");
				continue;
			}

			if (link.startsWith("//")){
				logger.info("Fixing protocal relative link: {}", link);
				link = "http:" + link;
			}

			URI uri = null;
			try {
				uri = new URI(link);
			} catch (URISyntaxException e) {
				logger.info("Failed to parse link: {}", link);
				continue;
			}

			if (UrlUtils.isEmailLink(link)) {
				logger.info("Invalid link: {}", uri.toString());
				continue;
			}

			if (link.startsWith("#")) {
				logger.info("Invalid link: {}", uri.toString());
				continue;
			}

			if (!uri.isAbsolute()) {
				try {
					String fullUrl = UrlUtils.concatLinks(baseUrl.toString(), uri.toString());
					uri = new URI(fullUrl);
				} catch (URISyntaxException e) {
					logger.info("Failed to parse link: {}", link);
					continue;
				}
			} else {
				if (!UrlUtils.doesLinkContainSeed(uri.toString())) {
					logger.info("Invalid link: {}", uri.toString());
					continue;
				}
			}

			logger.info("Adding valid link: {}", uri.toString());
			validLinks.add(uri);

		}
		return validLinks;
	}

	private static final Logger logger = LoggerFactory.getLogger(HtmlAnalyser.class);

}
