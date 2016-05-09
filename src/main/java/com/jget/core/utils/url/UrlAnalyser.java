package com.jget.core.utils.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FilenameUtils;
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

            if (connection.getContentType() != null) {
                urlAnalysisResult.setContentType(ContentType.parse(connection.getContentType()));
            } else {
                String fileName = FilenameUtils.getName(url.toString());
                MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
            }

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
