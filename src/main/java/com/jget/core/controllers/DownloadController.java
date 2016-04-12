package com.jget.core.controllers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;

@RestController
@RequestMapping(value = "/api/download")
public class DownloadController {

    @RequestMapping(value = "/api/download", method = RequestMethod.GET)
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
    
    @RequestMapping(value = "/getProject/{id}")
    @ResponseBody
    public Manifest getProject(@PathVariable String id, HttpServletRequest request) {
        logger.info("Service callded: getProject({}), from IP: {}", id, request.getRemoteAddr());    
        
        
        for(Manifest manifest: ManifestProvider.getManifests())
        {
            if(manifest.getId().toString().equals(id))
                return manifest;
        }
        
        throw new ResourceNotFoundException(); 
    }
    
    @RequestMapping(value = "/create",  method = RequestMethod.POST)
    @ResponseBody
    public Manifest create(@RequestBody @Valid Manifest inputManifest, HttpServletRequest request) {
        logger.info("Service callded: getProjects(), from IP: {}", request.getRemoteAddr());    
        
        logger.info("{}", inputManifest.toString());
        
        Manifest manifest = new Manifest();
        
        return manifest;
    }

    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

}
