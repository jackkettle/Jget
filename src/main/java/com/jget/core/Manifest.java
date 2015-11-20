package com.jget.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class Manifest {

    private Path rootDir;

    private List<String> seeds;

    private String rootUrl;

    public boolean validate() {

        if (rootDir == null) {
            logger.info("The rootDir has not been set");
            return false;
        }

        if (!Files.exists(rootDir)) {
            logger.info("The following path does not exist: {}", rootDir);
            return false;
        }

        if (StringUtils.isEmpty(rootUrl)) {
            logger.info("The rootUrl has not been set");
            return false;
        }

        if (!(seeds.size() > 0)) {
            logger.info("No seeds have been added");
            return false;
        }

        return true;
    }

    public Manifest() {
        super();
        rootUrl = "";
        seeds = new ArrayList<String>();
    }

    public List<String> getSeeds() {
        return this.seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public Path getRootDir() {
        return this.rootDir;
    }

    public void setRootDir(Path rootDir) {
        this.rootDir = rootDir;
    }

    public String getRootUrl() {
        return this.rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    private static final Logger logger = LoggerFactory.getLogger(Manifest.class);

}
