package pl.sapusers.mfsplc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.sapusers.mfsplc.sim.TelegramStyle;

public class Configurator {
	public static void main(String[] args) {
		Configurator configurator = null;

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
		
		System.out.println(configurator.getHandshakeRequest());
		System.out.println(configurator.getHandshakeRequest());
		System.out.println(configurator.getHandshakeRequest());
		System.out.println(configurator.getSendingFM());
	}
	private Properties configProperties;
	private String configPropertiesFileName;

	private Logger logger = LogManager.getLogger(Configurator.class.getName());

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

		for (String propertyKey : new TreeSet<String>(configProperties.stringPropertyNames())) {
			logger.debug(propertyKey + " = " + configProperties.getProperty(propertyKey));
		}
		
	}

	public String getHandshakeConfirmation() {
		return getProperty("handshakeConfirmation");
	}

	public String getHandshakeRequest() {
		return getProperty("handshakeRequest");
	}

	public String getJCoDestination() {
		return getProperty("jcoDestination");
	}	
	
	public String getJCoServer() {
		return getProperty("jcoServer");
	}
	
	public String getSendingFM() {
		return getProperty("sendingFM");
	}

	public String getStartingFM() {
		return getProperty("startingFM");
	}

	public String getStatusFM() {
		return getProperty("statusFM");
	}

	public String getStoppingFM() {
		return getProperty("stoppingFM");
	}

	public Boolean getSwitchSenderReceiver() {
		return Boolean.parseBoolean(getProperty("switchSenderReceiver"));
	}

	public String getTelegramStructure(String telegramType) {
		return getProperty("telegramStructure." + telegramType);	
	}
	
	public String getTelegramStructureHeader() {
		return getProperty("telegramStructureHeader");
	}
	
	public List<TelegramStyle> getTelegramStyles() {
		List<TelegramStyle> styles = new ArrayList<TelegramStyle>();

		Set<String> propertyKeys = configProperties.stringPropertyNames();

		for (String propertyKey : propertyKeys) {
			if (propertyKey.contains("style")) {
				styles.add(new TelegramStyle(propertyKey, configProperties.getProperty(propertyKey)));
			}
		}

		return styles;
	}

	public String getTelegramType(String type) {
		return getProperty("telegramType." + type);
	}
	
	private String getProperty(String property) {
		String value = configProperties.getProperty(property);

		if (value == null || value.equals(""))
			logger.error("Property " + property + " not defined in the config file: " + configPropertiesFileName);

		return value;
	}

}
