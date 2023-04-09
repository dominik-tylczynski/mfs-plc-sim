package pl.sapusers.mfsplc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.sapusers.mfsplc.sim.TelegramStyle;

public class Configurator {
	private Logger logger = LogManager.getLogger(Configurator.class.getName());
	private Properties configProperties;
	private String configPropertiesFileName;

	public Configurator(String configPropertiesFileName, String jcoDestination, String jcoServer) {
		this.configPropertiesFileName = configPropertiesFileName;

		// Load configuration from properties file
		configProperties = new Properties();
		logger.debug("Loading properites file: " + configPropertiesFileName);
		try (FileInputStream propertiesFile = new FileInputStream(configPropertiesFileName)) {
			configProperties.load(propertiesFile);
		} catch (FileNotFoundException e) {
			logger.catching(e);
		} catch (IOException e) {
			logger.catching(e);
		}

		if (jcoDestination != null && !jcoDestination.equals("")) {
			logger.debug("jcoDestination specified directly: " + jcoDestination);
			configProperties.setProperty("jcoDestination", jcoDestination);
		}

		if (jcoServer != null && !jcoServer.equals("")) {
			logger.debug("jcoServer specified directly: " + jcoServer);
			configProperties.setProperty("jcoServer", jcoServer);
		}

		Set<String> propertyKeys = configProperties.stringPropertyNames();

		for (String propertyKey : propertyKeys) {
			logger.debug(propertyKey + " = " + configProperties.getProperty(propertyKey));
		}
	}

	private String getProperty(String property) {
		String value = configProperties.getProperty(property);

		if (value == null || value.equals(""))
			logger.error("Property " + property + " not defined in the config file: " + configPropertiesFileName);

		return value;
	}

	public String getSendingFM() {
		return getProperty("sendingFM");
	}

	public String getStartingFM() {
		return getProperty("startingFM");
	}

	public String getStoppingFM() {
		return getProperty("stoppingFM");
	}

	public String getStatusFM() {
		return getProperty("statusFM");
	}

	public String getJCoDestination() {
		return getProperty("jcoDestination");
	}

	public String getJCoServer() {
		return getProperty("jcoServer");
	}

	public Boolean getSwitchSenderReceiver() {
		return Boolean.parseBoolean(getProperty("switchSenderReceiver"));
	}

	public List<TelegramStyle> getTelegramStyles() {
		List<TelegramStyle> styles = new ArrayList<TelegramStyle>();

		Set<String> propertyKeys = configProperties.stringPropertyNames();

		for (String propertyKey : propertyKeys) {
			if (propertyKey.contains("Style")) {
				styles.add(new TelegramStyle(propertyKey, configProperties.getProperty(propertyKey)));
			}
		}

		return styles;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Configurator configurator;

		switch (args.length) {
		case 1:
			configurator = new Configurator(args[0], null, null);
			break;
		case 2:
			configurator = new Configurator(args[0], args[1], null);
			break;
		case 3:
			configurator = new Configurator(args[0], args[1], args[2]);
			break;
		}
	}

}
