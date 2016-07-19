package com.jget.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class ConfigurationManager {

	private Map<String, Object> configurationMap;

	public ConfigurationManager () {
		configurationMap = new HashMap<String, Object> ();
	}

	public void init (Environment environment) {

		logger.info ("Populating values from config.properties file");

		for (ConfigurationConstant configurationConstant : ConfigurationConstant.values ()) {
			String value = environment.getProperty (configurationConstant.toString ());
			if (StringUtils.isEmpty (value)) {
				logger.info ("Failed to load following variable form environment: {}", ConfigurationConstant.FILESTORE.toString ());
				continue;
			}
			logger.info ("Found value: {}", value);
			configurationMap.put (ConfigurationConstant.FILESTORE.toString (), value);
			logger.info ("Loaded following environmental value into configuration: { key: {}, value: {} }", ConfigurationConstant.FILESTORE,
					configurationMap.get (ConfigurationConstant.FILESTORE));
		}
	}

	public Optional<Object> getValue (ConfigurationConstant configurationConstant) {

		if (configurationConstant == null || configurationMap.get (configurationConstant.toString ()) == null)
			return Optional.empty ();

		return Optional.of (configurationMap.get (configurationConstant.toString ()));
	}

	public void setValue (String key, Object value) {
		configurationMap.put (key, value);
	}

	public String getEntries () {
		StringBuilder sb = new StringBuilder ();
		for (Map.Entry<String, Object> entry : configurationMap.entrySet ()) {
			sb.append ("{ " + entry.getKey () + " : " + entry.getValue () + " }\n");
		}
		return sb.toString ();
	}

	public void saveValuesToFile ()
			throws ConfigurationException {
		// TODO: Sort this mess out
		PropertiesConfiguration config = new PropertiesConfiguration ("src/main/resources/config.properties");
		for (Map.Entry<String, Object> entry : configurationMap.entrySet ()) {
			config.setProperty (entry.getKey (), entry.getValue ().toString ());
		}
		config.save ();
	}

	private static final Logger logger = LoggerFactory.getLogger (ConfigurationManager.class);

}
