package com.jget.core;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jget.core.download.ReferencedURL;

public class Manifest {

    private Path rootDir;

    private List<URI> seeds;

    private List<String> rootUrls;
    
    private ConcurrentLinkedQueue<ReferencedURL> frontier;
    
    private HashMap<URL, Path> linkMap;
    
    private HashMap<Path, URL> fileMap;

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
        this.seeds = new ArrayList<URI>();
        this.frontier = new ConcurrentLinkedQueue<ReferencedURL>();
        this.linkMap = new HashMap<URL, Path>();
        this.fileMap = new HashMap<Path, URL>();
    }

    public List<URI> getSeeds() {
        return this.seeds;
    }

    public List<String> getRootUrls() {
		return this.rootUrls;
	}

	public void setRootUrls(List<String> rootUrls) {
		this.rootUrls = rootUrls;
	}

	public void setSeeds(List<URI> seeds) {
        this.seeds = seeds;
    }

    public Path getRootDir() {
        return this.rootDir;
    }

    public void setRootDir(Path rootDir) {
        this.rootDir = rootDir;
    }

    public ConcurrentLinkedQueue<ReferencedURL> getFrontier() {
		return this.frontier;
	}

	public void setFrontier(ConcurrentLinkedQueue<ReferencedURL> frontier) {
		this.frontier = frontier;
	}

	public HashMap<URL, Path> getLinkMap() {
        return this.linkMap;
    }

    public void setLinkMap(HashMap<URL, Path> linkMap) {
        this.linkMap = linkMap;
    }

    private static final Logger logger = LoggerFactory.getLogger(Manifest.class);

    public HashMap<Path, URL> getFileMap() {
        return this.fileMap;
    }

    public void setFileMap(HashMap<Path, URL> fileMap) {
        this.fileMap = fileMap;
    }

}
