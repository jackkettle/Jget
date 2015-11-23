package com.jget.core.utils.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlAnalyser {

	public static UrlAnalysisResult analyse(URL url) {

		UrlAnalysisResult urlAnalysisResult = new UrlAnalysisResult();
		urlAnalysisResult.setValidLink(false);

		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
		} catch (IOException e) {
			logger.info("Failed to connect to url: {}", url.toString());
			return urlAnalysisResult;
		}

		urlAnalysisResult.setContentType(ContentType.parse(connection.getContentType()));
		urlAnalysisResult.setFileSize(connection.getContentLength());
		urlAnalysisResult.setValidLink(true);

		return urlAnalysisResult;
	}

	private static final Logger logger = LoggerFactory.getLogger(UrlAnalyser.class);

}
