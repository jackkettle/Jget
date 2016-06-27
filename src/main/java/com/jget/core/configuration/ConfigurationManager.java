package com.jget.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class ConfigurationManager {
    
    private static Map<String, Object> configurationMap;

    public ConfigurationManager() {
        configurationMap = new HashMap<String, Object>();
    }
    
    public void init(Environment environment) {

        logger.info("Populating values from config.properties file");
      
        for(ConfigurationConstant configurationConstant: ConfigurationConstant.values()){
            String value = environment.getProperty(configurationConstant.toString());
            if(StringUtils.isEmpty(value))
            {
                logger.info("Failed to load following variable form environment: {}", ConfigurationConstant.FILESTORE.toString());
                continue;
            }
            logger.info("{}", configurationMap);
            configurationMap.put(ConfigurationConstant.FILESTORE.toString(), value);
            logger.info("Loaded following environmental value into configuration: { key: {}, value: {} }", ConfigurationConstant.FILESTORE.toString(), value);
        }
    }

    public static Optional<Object> getValue(ConfigurationConstant configurationConstant) {
        
        if(configurationMap.get(configurationConstant) == null)
            return Optional.empty();
        
       return Optional.of(configurationMap.get(configurationConstant));
    }
    
    public static void setValue(String key, Object value) {
        configurationMap.put(key, value);
    }

    private void saveValuesToFile() {
        // TODO Auto-generated method stub
        //return null;
    }

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    
}
