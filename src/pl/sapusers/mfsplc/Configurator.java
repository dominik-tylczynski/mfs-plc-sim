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
	private Properties config;
	private String configProperties;

	public Configurator(String configProperties, String jcoDestination, String jcoServer) {
		this.configProperties = configProperties;

		// Load configuration from properties file
		config = new Properties();
		logger.debug("Loading properites file: " + configProperties);
		try (FileInputStream propertiesFile = new FileInputStream(configProperties)) {
			config.load(propertiesFile);
		} catch (FileNotFoundException e) {
			logger.catching(e);
		} catch (IOException e) {
			logger.catching(e);
		}
		
		if (jcoDestination != null && !jcoDestination.equals("")) 
			config.setProperty("jcoDestination", jcoDestination);
		
		if (jcoServer != null && !jcoServer.equals("")) 
			config.setProperty("jcoServer", jcoServer);		
	}

	private String getProperty(String property) {
		String value = config.getProperty(property);

		if (value == null || value.equals(""))
			logger.error(value + " request not defined in the config file: " + configProperties);

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

		Set<String> propertyKeys = config.stringPropertyNames();

		for (String propertyKey : propertyKeys) {
			if (propertyKey.contains("Style")) {
				styles.add(new TelegramStyle(propertyKey, config.getProperty(propertyKey)));
			}
		}

		return styles;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
