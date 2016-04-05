package com.jget.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableSet;
import com.jget.core.download.DownloadManager;
import com.jget.core.download.ReferencedURL;
import com.jget.core.linkresolver.LinkResolverManager;
import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;
import com.jget.core.report.Report;
import com.jget.core.report.ReportProvider;
import com.jget.core.spring.ApplicationContextProvider;
import com.jget.core.utils.url.UrlUtils;

@SpringBootApplication
public class JgetApplication {

    private static final String PROJECT_NAME = "JgetDevProject";
    
    private static final Path ROOT_DIR = Paths.get("D:\\Jget_Sites");

    public static final ImmutableSet<String> URL_SEEDS = ImmutableSet.of("www.teagasc.ie/publications/");

    public static final ImmutableSet<String> URL_STRING = ImmutableSet.of("http://www.teagasc.ie/publications/PublicationsBy_T4.aspx");

    public static void main(String[] args) throws IOException {

        ApplicationContext applicationContext = SpringApplication.run(JgetApplication.class, args);
        ApplicationContextProvider.resetApplicationContext(applicationContext);

        logger.info("Running component");
        JgetApplication application = ApplicationContextProvider.getBean(JgetApplication.class);

        application.mainMethod();

        //logger.info("Exiting application");
        //System.exit(0);
    }

    private void mainMethod() {
        Manifest manifest = new Manifest();
        manifest.setName(PROJECT_NAME);
        manifest.setRootDir(ROOT_DIR);
        manifest.configureRootDir();
        manifest.getRootUrls().addAll(URL_STRING);
         
        ReportProvider.setReport(new Report());

        URI urlSeedUri = null;
        for (String urlString : URL_SEEDS) {
            try {
                urlSeedUri = new URI(urlString);
                manifest.getSeeds().add(urlSeedUri);
            } catch (URISyntaxException e) {
                logger.error("Failed to add seed: {}", urlString, e);
                return;
            }
        }

        Boolean isValid = manifest.validate();

        if (!isValid) {
            logger.info("The manifest is invalid");
            return;
        }

        ManifestProvider.setCurrentManifest(manifest);
        ManifestProvider.getManifests().add(manifest);
        
        return; 
        
        /*
        logger.info("Testing {} rootUrls", ManifestProvider.getCurrentManifest().getRootUrls().size());
        for (String string : ManifestProvider.getCurrentManifest().getRootUrls()) {
            try {
                URL url = new URL(string);
                ReferencedURL referencedURL = new ReferencedURL();
                referencedURL.setLocation("");
                referencedURL.setURL(url);
                ManifestProvider.getCurrentManifest().getFrontier().add(referencedURL);
                addUrlToSeeds(url);
                logger.info("Root Url passed: {}", string);
            } catch (MalformedURLException e) {
                logger.error("Invalid root Url: {}", string, e);
                return;
            }
        }

        logger.info("Creating folders for {} seeds", ManifestProvider.getCurrentManifest().getSeeds().size());
        for (URI uri : ManifestProvider.getCurrentManifest().getSeeds()) {

            logger.info("Creating folder for {}", uri.toString());
            Path seedPath = ROOT_DIR.resolve(uri.toString());
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

        ReportProvider.printReportSummaryString();
*/
    }

    private void addUrlToSeeds(URL url) {
        if (!UrlUtils.doesLinkContainSeed(url.toString())) {
            URI uriSeed = null;
            try {
                uriSeed = new URI(url.getHost());
                logger.info("Adding host to seeds: {}", uriSeed);
                ManifestProvider.getCurrentManifest().getSeeds().add(uriSeed);
            } catch (URISyntaxException e) {
                logger.error("Invalid seed Url: {}", uriSeed, e);
                return;
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
