package com.jget.core;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.jget.core.download.DownloadManager;
import com.jget.core.spring.ApplicationContextProvider;

@Component
public class JgetApplication {

    public static void main(String[] args) throws IOException {

        String springConfigXml = "spring-config.xml";
        logger.info("Configuring spring using {}", springConfigXml);
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(springConfigXml);
        ApplicationContextProvider.resetApplicationContext(applicationContext);

        logger.info("Running component");
        JgetApplication application = ApplicationContextProvider.getBean(JgetApplication.class);
        application.mainMethod();

        logger.info("Exiting application");
        System.exit(0);
    }

    private void mainMethod() throws IOException {

        Path rootDir = Paths.get("F:\\Java");
        String urlSeed = "www.terminalfour.com";
        String urlString = "http://www.terminalfour.com/";

        Manifest manifest = new Manifest();

        manifest.setRootDir(rootDir);
        manifest.getSeeds().add(urlSeed);
        manifest.getRootUrls().add(urlString);
        
        Boolean isValid = manifest.validate();

        if (!isValid) {
            logger.info("The manifest is invalid");
            return;
        }

        ManifestProvider.setManifest(manifest);
        
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.commenceDownload();

    }

    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
