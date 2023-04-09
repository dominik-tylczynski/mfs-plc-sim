package pl.sapusers.mfsplc.sim;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoStructure;

import pl.sapusers.mfsplc.Configurator;

public class Telegram {
	public static final String TO_SAP = "outbound";
	public static final String FROM_SAP = "inbound";
	private Logger logger = LogManager.getLogger(Telegram.class.getName());

	private JCoStructure telegramContent;
	private LocalDateTime timeStamp;
	private String direction;

	public Telegram(Configurator configurator, String telegramString, String direction) {
		try {
			JCoStructure header = JCo
					.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
							.getRepository().getStructureDefinition(configurator.getTelegramStructureHeader()));

			header.setString(telegramString);
			
			telegramContent = JCo
					.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
							.getRepository().getStructureDefinition(header.getString("TELETYPE")));

			telegramContent.setString(telegramString);
			timeStamp = LocalDateTime.now();
			this.direction = direction;

		} catch (IllegalArgumentException | JCoException e) {
			logger.error(e);
		}
	}

	public Telegram(JCoStructure telegramContent, String direction) {
		this.telegramContent = telegramContent;
		timeStamp = LocalDateTime.now();
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

	public String getField(String field) {
		return telegramContent.getString(field);
	}

	public String getString() {
		return telegramContent.getString();
	}

	public String getTimeStamp() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(timeStamp);
	}

	public void setField(String field, String value) {
		telegramContent.getField(field).setValue(value);
	}
}
