package com.jget.core.download.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;

@RestController
@RequestMapping(value = "/api/download")
public class DownloadController {

    @RequestMapping(method = RequestMethod.GET)
    public void startDownload() {
        logger.info("Service callded: startDownload()");

        return;
    }

    @RequestMapping(value = "/setCurrentManifest", method = RequestMethod.POST)
    public void setCurrentManifest(String name) {
        logger.info("Service callded: startDownload()");

        return;
    }

    @RequestMapping(value = "/getProjects")
    @ResponseBody
    public Set<Manifest> getProjects(HttpServletRequest request) {
        logger.info("Service callded: getProjects(), from IP: {}", request.getRemoteAddr());    
        return ManifestProvider.getManifests();
    }

    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

}
