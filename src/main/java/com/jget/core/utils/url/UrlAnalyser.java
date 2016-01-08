package com.jget.core.utils.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlAnalyser {

    public static UrlAnalysisResult analyse(URL url) {

        UrlAnalysisResult urlAnalysisResult = new UrlAnalysisResult();
        urlAnalysisResult.setValidLink(false);
        urlAnalysisResult.setURL(url);

        HttpURLConnection connection;
        try {
            logger.info(url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("HEAD");
            connection.connect();

            urlAnalysisResult.setResponseCode(connection.getResponseCode());

            if (urlAnalysisResult.getResponseCode() == HttpStatus.SC_NOT_FOUND) {
                return urlAnalysisResult;
            }
            
            if (urlAnalysisResult.isRedirect()) {
                urlAnalysisResult.setLocation(connection.getHeaderField(HttpHeaders.LOCATION));
                urlAnalysisResult.setValidLink(true);
                return urlAnalysisResult;
            }

            urlAnalysisResult.setContentType(ContentType.parse(connection.getContentType()));
            urlAnalysisResult.setFileSize(connection.getContentLength());
            urlAnalysisResult.setValidLink(true);

        } catch (IOException e) {
            logger.info("Failed to connect to url: {}", url.toString());
            return urlAnalysisResult;
        }

        return urlAnalysisResult;
    }

    private static final Logger logger = LoggerFactory.getLogger(UrlAnalyser.class);

}
