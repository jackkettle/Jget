package com.jget.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableSet;
import com.jget.core.configuration.ConfigurationConstant;
import com.jget.core.configuration.ConfigurationManager;
import com.jget.core.spring.ApplicationContextProvider;

@SpringBootApplication
public class JgetApplication {
	
	@Autowired
	ConfigurationManager configurationManager;

    public static final ImmutableSet<String> URL_SEEDS = ImmutableSet.of("www.teagasc.ie/publications/");

    public static final ImmutableSet<String> URL_STRING = ImmutableSet.of("http://www.teagasc.ie/publications/PublicationsBy_T4.aspx");

    public static void main(String[] args) throws IOException {

        ApplicationContext applicationContext = SpringApplication.run(JgetApplication.class, args);
        ApplicationContextProvider.resetApplicationContext(applicationContext);

        logger.info("Running component");
        JgetApplication application = ApplicationContextProvider.getBean(JgetApplication.class);

        application.mainMethod();

    }

    private void mainMethod() {
    	logger.info ("Marker1: {}", configurationManager.getEntries ());
        logger.info("{}", configurationManager.getValue (ConfigurationConstant.FILESTORE));
    }

    private static final Logger logger = LoggerFactory.getLogger(JgetApplication.class);

}
