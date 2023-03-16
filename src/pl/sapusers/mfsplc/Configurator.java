package pl.sapusers.mfsplc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.sapusers.mfsplc.sim.TcpServer;

public class Configurator {
	private Logger logger = LogManager.getLogger(Configurator.class.getName());
	private Properties config;
	
	public Configurator(String configProperties, String jcoDestination, String jcoServer) {

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
	}
	
	public String getSendingFM() {
		return config.getProperty("sendingFM");
	}
	
	public String getStartingFM() {
		return config.getProperty("startingFM");
	}
	
	public String getStoppingFM() {
		return config.getProperty("stoppingFM");
	}
	
	public String getStatusFM() {
		return config.getProperty("statusFM");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
	}

}
