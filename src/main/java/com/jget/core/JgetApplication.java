package com.jget.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.jget.core.download.DownloadManager;
import com.jget.core.download.ReferencedURL;
import com.jget.core.linkresolver.LinkResolverManager;
import com.jget.core.report.Report;
import com.jget.core.report.ReportProvider;
import com.jget.core.spring.ApplicationContextProvider;
import com.jget.core.utils.url.UrlUtils;

@Component
public class JgetApplication {

    private static final Path ROOT_DIR = Paths.get("D:\\Jget_Sites");

    public static final ImmutableSet<String> URL_SEEDS = ImmutableSet.of("twitter.com");

    public static final ImmutableSet<String> URL_STRING = ImmutableSet.of("https://twitter.com/");

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

    private void mainMethod() {
        Manifest manifest = new Manifest();
        manifest.setRootDir(ROOT_DIR);
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

        ManifestProvider.setManifest(manifest);

        logger.info("Testing {} rootUrls", ManifestProvider.getManifest().getRootUrls().size());
        for (String string : ManifestProvider.getManifest().getRootUrls()) {
            try {
                URL url = new URL(string);
                ReferencedURL referencedURL = new ReferencedURL();
                referencedURL.setLocation("");
                referencedURL.setURL(url);
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
