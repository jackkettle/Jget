package com.jget.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class Manifest {

	private static Path rootDir;

	private static List<String> seeds;
	
	private static String rootUrl;

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
			logger.info("The following path does not exist: {}", rootDir);
			return false;		
		}
		
		return true;
	}

	public Manifest(){
		super();
		rootUrl = "";
		seeds = new ArrayList<String>();
	}
	

	public static List<String> getSeeds() {
		return seeds;
	}

	public static void setSeeds(List<String> seeds) {
		Manifest.seeds = seeds;
	}
	
	public static Path getRootDir() {
		return rootDir;
	}

	public static void setRootDir(Path rootDir) {
		Manifest.rootDir = rootDir;
	}

	public static String getRootUrl() {
		return rootUrl;
	}

	public static void setRootUrl(String rootUrl) {
		Manifest.rootUrl = rootUrl;
	}

	private static final Logger logger = LoggerFactory.getLogger(Manifest.class);

}
