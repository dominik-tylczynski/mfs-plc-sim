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
	private Logger logger = LogManager.getLogger(Telegram.class.getName());

	public static final String TO_SAP = "outbound";
	public static final String FROM_SAP = "inbound";

	private JCoStructure telegramContent;
	private LocalDateTime timeStamp;

	public Telegram(JCoStructure telegramContent) {
		this.telegramContent = telegramContent;
		timeStamp = LocalDateTime.now();
	}

	public Telegram(Configurator configurator, String telegramString) {
		try {
			JCoStructure header = JCo
					.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
							.getRepository().getStructureDefinition(configurator.getTelegramStructureHeader()));
			
			telegramContent = JCo
					.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
							.getRepository().getStructureDefinition(header.getString("TELETYPE")));
			
			telegramContent.setString(telegramString);
			timeStamp = LocalDateTime.now();
			
		} catch (IllegalArgumentException | JCoException e) {
			logger.error(e);
		}
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
