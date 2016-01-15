package com.jget.core.download;

public class DownloadConfig {

	public static final String LINK_SELECTOR = "a[href], [src], link[href]";
	
	public static final int MAX_REDIRECT_DEPTH = 2;
	
	public static final int MAX_TOTAL_LINKS = 1000;

    public static final String HTTP = "http";
    
    public static final String HTTPS = "https";

    public static final String HTML_EXTENSION = "html";
    
    public static final String LINE_BREAK = "----------------------------";
    
    public static final String LINE_SEPERATOR = System.getProperty("line.separator");
    
    public static final int URL_DEPTH = 20;
    
}
