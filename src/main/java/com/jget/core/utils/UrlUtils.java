package com.jget.core.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class UrlUtils {

	public static void main(String [] args) throws IOException{
		
		String urlString = "https://example.com/";
		
		URL url = new URL(urlString);
		Optional<HttpURLConnection> httpHeaderWrapper = getMimeTpe(url);
		System.out.println(httpHeaderWrapper.get().getResponseCode());
		System.out.println(httpHeaderWrapper.get().getContentType());
		System.out.println(httpHeaderWrapper.get().getContentLength());

	}
	
	public static Optional<HttpURLConnection> getMimeTpe(URL url) throws IOException{
		HttpURLConnection connection = (HttpURLConnection)  url.openConnection();
		connection.setRequestMethod("HEAD");
		connection.connect();
		return Optional.of(connection);
	}
	
}
