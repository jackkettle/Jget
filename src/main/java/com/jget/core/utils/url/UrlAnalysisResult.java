package com.jget.core.utils.url;

import org.apache.http.entity.ContentType;

public class UrlAnalysisResult {

	private boolean isValidLink;
	
	private ContentType contentType;
	
	private int fileSize;

	public UrlAnalysisResult() {
		super();
		this.isValidLink = false;
		this.contentType = ContentType.DEFAULT_TEXT;
		this.fileSize = 0;
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
	
}
