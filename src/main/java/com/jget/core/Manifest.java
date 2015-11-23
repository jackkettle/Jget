package com.jget.core;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Manifest {

    private Path rootDir;

    private List<String> seeds;

    private List<String> rootUrls;
    
    private ConcurrentLinkedQueue<URL> frontier;

    public boolean validate() {

        if (rootDir == null) {
            logger.info("The rootDir has not been set");
            return false;
        }

        if (!Files.exists(rootDir)) {
            logger.info("The following path does not exist: {}", rootDir);
            return false;
        }

        if (!(rootUrls.size() > 0)) {
            logger.info("The rootUrls has not been added");
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
        this.rootUrls = new ArrayList<String>();
        this.seeds = new ArrayList<String>();
        this.frontier = new ConcurrentLinkedQueue<URL>();
    }

    public List<String> getSeeds() {
        return this.seeds;
    }

    public List<String> getRootUrls() {
		return rootUrls;
	}

	public void setRootUrls(List<String> rootUrls) {
		this.rootUrls = rootUrls;
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

    public ConcurrentLinkedQueue<URL> getFrontier() {
		return this.frontier;
	}

	public void setFrontier(ConcurrentLinkedQueue<URL> frontier) {
		this.frontier = frontier;
	}

	private static final Logger logger = LoggerFactory.getLogger(Manifest.class);

}
