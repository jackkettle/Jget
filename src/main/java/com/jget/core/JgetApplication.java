package com.jget.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jget.core.download.DownloadManager;
import com.jget.core.download.ReferencedURL;
import com.jget.core.linkresolver.LinkResolverManager;
import com.jget.core.spring.ApplicationContextProvider;
import com.jget.core.utils.url.UrlUtils;

@Component
public class JgetApplication {

    private static Path rootDir = Paths.get("D:\\Jget_Sites");
    private static String urlSeed = "www.terminalfour.com";
    private static String urlString = "http://www.terminalfour.com/blog/posts/its-time-for-universities-to-get-more-appy.html";

    public static void main(String[] args) throws IOException {

        String springConfigXml = "spring-config.xml";
        logger.info("Configuring spring using {}", springConfigXml);
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(springConfigXml);
        ApplicationContextProvider.resetApplicationContext(applicationContext);

        logger.info("Running component");
        JgetApplication application = ApplicationContextProvider.getBean(JgetApplication.class);

        promptUserForInputs();
        application.mainMethod();

        logger.info("Exiting application");
        System.exit(0);
    }

    private static void promptUserForInputs() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a root directory:\t");
        String rootTemp = scanner.nextLine();
        if (!StringUtils.isEmpty(rootTemp))
            rootDir = Paths.get(rootTemp);

        System.out.print("Enter a seed:\t");
        String seedTemp = scanner.nextLine();
        if (!StringUtils.isEmpty(seedTemp))
            urlSeed = seedTemp;

        System.out.print("Enter a url to start downloading:\t");
        String urlTemp = scanner.nextLine();
        if (!StringUtils.isEmpty(urlTemp))
            urlString = urlTemp;

        scanner.close();
    }

    private void mainMethod() {
        URI urlSeedUri = null;
        ;
        try {
            urlSeedUri = new URI(urlSeed);
        } catch (URISyntaxException e) {
            logger.error("Failed to add seed: {}", urlSeed, e);
            return;
        }

        Manifest manifest = new Manifest();
        manifest.setRootDir(rootDir);
        manifest.getSeeds().add(urlSeedUri);
        manifest.getRootUrls().add(urlString);

        Boolean isValid = manifest.validate();

        if (!isValid) {
            logger.info("The manifest is invalid");
            return;
        }

        ManifestProvider.setManifest(manifest);

        logger.info("Testing {} rootUrls", ManifestProvider.getManifest().getRootUrls().size());
        for (String string : ManifestProvider.getManifest().getRootUrls()) {
            try {
                URL url = new URL(string);
                ReferencedURL referencedURL = new ReferencedURL();
                referencedURL.setLocation("");
                referencedURL.setUrl(url);
                ManifestProvider.getManifest().getFrontier().add(referencedURL);
                addUrlToSeeds(url);
                logger.info("Root Url passed: {}", string);
            } catch (MalformedURLException e) {
                logger.error("Invalid root Url: {}", string, e);
                return;
            }
        }

        logger.info("Creating folders for {} seeds", ManifestProvider.getManifest().getSeeds().size());
        for (URI uri : ManifestProvider.getManifest().getSeeds()) {

            logger.info("Creating folder for {}", uri.toString());
            Path seedPath = rootDir.resolve(uri.toString());
            logger.info("seedPath: {}", seedPath.toString());
            try {
                Files.createDirectories(seedPath);
            } catch (IOException e) {
                logger.info("Failed to create seed path: {}", seedPath.toString());
                return;
            }
        }

        DownloadManager downloadManager = new DownloadManager();
        downloadManager.commenceDownload();
        
        LinkResolverManager linkResolverManager = new LinkResolverManager();
        linkResolverManager.commenceResolving();

    }

    private void addUrlToSeeds(URL url) {
        if (!UrlUtils.doesLinkContainSeed(url.toString())) {
            URI uriSeed = null;
            try {
                uriSeed = new URI(url.getHost());
                logger.info("Adding host to seeds: {}", uriSeed);
                ManifestProvider.getManifest().getSeeds().add(uriSeed);
            } catch (URISyntaxException e) {
                logger.error("Invalid seed Url: {}", uriSeed, e);
                return;
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
