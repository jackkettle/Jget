package com.jget.core.utils.url;

import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

public class UrlAnalysisResult {

    private boolean isValidLink;

    private ContentType contentType;

    private int fileSize;

    private int responseCode;

    private String location;

    private URL url;

    public UrlAnalysisResult() {
        super();
        this.isValidLink = false;
        this.contentType = ContentType.DEFAULT_TEXT;
        this.fileSize = 0;
        this.responseCode = 0;
        this.location = "";
    }

    public boolean isValidLink() {
        return isValidLink;
    }

    public void setValidLink(boolean isValidLink) {
        this.isValidLink = isValidLink;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType mime) {
        this.contentType = mime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public boolean isRedirect() {
        if (this.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY || this.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY)
            return true;

        return false;
    }

    @Override
    public String toString() {
        return "UrlAnalysisResult [isValidLink=" + isValidLink + ", contentType=" + contentType + ", fileSize=" + fileSize
                + ", responseCode=" + responseCode + ", location=" + location + ", url=" + url + "]";
    }

}
