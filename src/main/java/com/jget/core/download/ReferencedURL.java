package com.jget.core.download;

import java.net.URL;

public class ReferencedURL {

    public ReferencedURL() {
        this.location = "";
    }
    
    private String location;
    
    private URL url;

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
