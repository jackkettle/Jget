package com.jget.core.controllers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jget.core.configuration.ConfigurationConstants;
import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;
import com.jget.core.utils.manifest.ManifestUtils;

@RestController
@RequestMapping(value = "/api/project")
public class DownloadController {

    public void startDownload() {
        logger.info("Service called: startDownload()");

        return;
    }

    @RequestMapping(value = "/setCurrentManifest", method = RequestMethod.POST)
    public void setCurrentManifest(String name) {
        logger.info("Service called: startDownload()");

        return;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public ResponseEntity<Set<Manifest>> getProjects(HttpServletRequest request) {
        logger.info("Service called: getProjects(), from IP: {}", request.getRemoteAddr());
        return ResponseEntity.ok(ManifestProvider.getManifests());
    }

    @RequestMapping(value = "/get/{id}")
    @ResponseBody
    public ResponseEntity<Manifest> getProject(@PathVariable String id, HttpServletRequest request) {
        logger.info("Service called: getProject({}), from IP: {}", id, request.getRemoteAddr());

        for (Manifest manifest : ManifestProvider.getManifests()) {
            if (manifest.getId().toString().equals(id))
                return ResponseEntity.ok(manifest);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ResponseEntity<Manifest> deleteProject(@PathVariable String id, HttpServletRequest request) {
        logger.info("Service called: deleteProject({}), from IP: {}", id, request.getRemoteAddr());

        Set<Manifest> manifests = ManifestProvider.getManifests();

        for (Manifest manifest : ManifestProvider.getManifests()) {
            if (manifest.getId().toString().equals(id)) {
                manifests.remove(manifest);
                ManifestProvider.setManifests(manifests);
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Manifest> create(@RequestBody Manifest inputManifest, HttpServletRequest request) {
        logger.info("Service called: create(), from IP: {}", request.getRemoteAddr());

        if (ManifestUtils.manifestNameExists(inputManifest.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        if (inputManifest.getRootDir() == null || StringUtils.isEmpty(inputManifest.getRootDir().toString())) {
            inputManifest.setRootDir(ConfigurationConstants.ROOT_DIR);
        }

        if (!inputManifest.validate()) {
            logger.info("Invalid manifest provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ManifestProvider.getManifests().add(inputManifest);
        return ResponseEntity.ok(inputManifest);

    }

    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

}
