package com.jget.core.download;

import java.net.URL;

public class ReferencedURL {

    private String location;
    
    private URL url;
    
    public ReferencedURL() {
        this.location = "";
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
    
}
