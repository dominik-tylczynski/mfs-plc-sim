package pl.sapusers.mfsplc.sim;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.ConversionException;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoRuntimeException;
import com.sap.conn.jco.JCoStructure;

import pl.sapusers.mfsplc.Configurator;

public class Telegram {
	public static final String FROM_SAP = "inbound";
	public static final String TO_SAP = "outbound";
	private Configurator configurator;
	private String direction;

	private Logger logger = LogManager.getLogger(Telegram.class.getName());
	private JCoStructure telegramContent;
	@SuppressWarnings("unused")
	private String telegramString;
	private LocalDateTime timeStamp;

	public Telegram(Configurator configurator, String telegramString, String direction) {
		this.configurator = configurator;
		timeStamp = LocalDateTime.now();
		this.direction = direction;
		this.telegramString = telegramString;

		try {
			telegramContent = JCo.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
					.getRepository().getStructureDefinition(configurator.getTelegramStructureHeader()));

			telegramContent.setString(telegramString);

			telegramContent = JCo.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
					.getRepository()
					.getStructureDefinition(configurator.getTelegramStructure(telegramContent.getString("TELETYPE"))));

			telegramContent.setString(telegramString);

		} catch (IllegalArgumentException | JCoException e) {
			logger.error(e);
		}
	}

	public String getDirection() {
		return direction;
	}

	public String getField(String field) {
		try {
			return telegramContent.getString(field);
		} catch (ConversionException e) {
			logger.error(e);
			return null;
		} catch (JCoRuntimeException e) {
			logger.error(e);
			return null;
		}
	}

	public Telegram getHandshakeConfirmation() {
		if (this.getField("HANDSHAKE").equals(configurator.getHandshakeRequest())) {

			Telegram response = new Telegram(configurator, this.getString(), Telegram.TO_SAP);
			response.setField("HANDSHAKE", configurator.getHandshakeConfirmation());

			if (configurator.getSwitchSenderReceiver()) {
				response.setField("SENDER", this.getField("RECEIVER"));
				response.setField("RECEIVER", this.getField("SENDER"));
			}
			
			return response;
		} else
			return null;
	}

	public JCoRecordFieldIterator getRecordFieldIterator() {
		return telegramContent.getRecordFieldIterator();
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
