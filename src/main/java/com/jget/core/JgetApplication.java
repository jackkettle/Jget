package com.jget.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

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

        Path rootDir = Paths.get("C:\\media");
        logger.info("Root dir: {}", rootDir);
        
        if(rootDir == null || !Files.exists(rootDir))
        {
            logger.info("The rootPath is invalid");
            return;
        }
        String urlString = "http://www.programcreek.com/java-api-examples/includes/images/api-logo.png";

        Optional<File> downloadedFileWrapper = saveFileFromURL(rootDir, urlString);
        
        if(!downloadedFileWrapper.isPresent()){
            logger.info("Failed to download file from URL: {}", urlString);
        }
        
    }

    public Optional<File> saveFileFromURL(Path rootDir, String urlString) {
        URL website;
        try {
            website = new URL(urlString);
        } catch (MalformedURLException e1) {
            return Optional.empty();
        }

        Path filePath = Paths.get(rootDir + website.getFile());

        logger.info("File name: {}", website.getFile());
        logger.info("Saved path: {}", filePath);

        try {
            Files.createDirectories(filePath.getParent());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(rootDir + website.getFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.of(filePath.toFile());
    }

    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
