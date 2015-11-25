package com.jget.core.utils.url;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import com.jget.core.ManifestProvider;

public class UrlUtils {

    public static boolean isEmailLink(String link) {

        if (link.startsWith("mailto"))
            return true;

        return EmailValidator.getInstance().isValid(link);

    }

    public static String concatLinks(String base, String relative) {

        if (StringUtils.isEmpty(base) && StringUtils.isEmpty(relative))
            return "";

        if (StringUtils.isEmpty(base) && !StringUtils.isEmpty(relative))
            return relative;

        if (!StringUtils.isEmpty(base) && StringUtils.isEmpty(relative))
            return base;

        if (base.endsWith("/") && relative.startsWith("/"))
            return base + relative.substring(1);

        if (base.endsWith("/") && !relative.startsWith("/"))
            return base + relative;

        if (!base.endsWith("/") && relative.startsWith("/"))
            return base + relative;

        return base + "/" + relative;

    }

    public static boolean doesLinkContainSeed(String url) {

        for (URI seed : ManifestProvider.getManifest().getSeeds()) {
            if (url.contains(seed.toString())) {
                return true;
            }
        }
        return false;

    }
    
    public static Set<URL> removeProcessLinks(Set<URL> pageLinksUrl) {
        
        Set<URL> newLinks = new HashSet<URL>();
        for(URL url: pageLinksUrl){
            if(!ManifestProvider.getManifest().getLinkMap().containsKey(url))
                newLinks.add(url);
        }
        return newLinks;
    }

}
