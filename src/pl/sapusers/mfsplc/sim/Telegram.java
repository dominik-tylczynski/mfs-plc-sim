package pl.sapusers.mfsplc.sim;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.server.JCoServer;

import pl.sapusers.mfsplc.Configurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Telegram {
	private Logger logger = LogManager.getLogger(Telegram.class.getName());
	
	public static final String TO_SAP = "outbound";
	public static final String FROM_SAP = "inbound";	

	private JCoStructure telegramContent;
	private LocalDateTime timeStamp;


	public Telegram(JCoRecordMetaData telegramMetadata, String telegramString) {
		telegramContent = JCo.createStructure(telegramMetadata);
		telegramContent.setString(telegramString);
		
		timeStamp = LocalDateTime.now();
	}

	public Telegram(JCoServer server, String telegramStructure, String telegramString) throws JCoException {
		this(server.getRepository().getStructureDefinition(telegramStructure), telegramString);
	}
	
	public Telegram(JCoStructure telegramContent) {
		this.telegramContent = telegramContent;
		timeStamp = LocalDateTime.now();
	}
	
	public Telegram(Configurator configurator, String telegramString) {
		
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
