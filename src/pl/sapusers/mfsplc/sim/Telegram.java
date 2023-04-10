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
	private Configurator configurator;

	private JCoStructure telegramContent;
	private String telegramString;
	private LocalDateTime timeStamp;
	private String direction;

	public Telegram(Configurator configurator, String telegramString, String direction) {
		this.configurator = configurator;
		timeStamp = LocalDateTime.now();
		this.direction = direction;
		this.telegramString = telegramString;

		try {
			JCoStructure header = JCo
					.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
							.getRepository().getStructureDefinition(configurator.getTelegramStructureHeader()));

			header.setString(telegramString);

			telegramContent = JCo.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
					.getRepository().getStructureDefinition(header.getString("TELETYPE")));

			telegramContent.setString(telegramString);

		} catch (IllegalArgumentException | JCoException e) {
			telegramContent = null;
			logger.error(e);
		}
	}

	public Telegram(Configurator configurator, JCoStructure telegramContent, String direction) {
		this.configurator = configurator;
		this.telegramContent = telegramContent;
		this.telegramString = telegramContent.getString();
		timeStamp = LocalDateTime.now();
		this.direction = direction;
	}

	public Telegram getHandshakeConfirmation() {
		if (telegramContent == null)
			return null;

		if (this.getField("HANDSHAKE").equals(configurator.getHandshakeRequest())) {
			try {
				Telegram response = (Telegram) this.clone();
				response.setField("HANDSHAKE", configurator.getHandshakeConfirmation());

				if (configurator.getSwitchSenderReceiver()) {
					response.setField("SENDER", this.getField("RECEIVER"));
					response.setField("RECEIVER", this.getField("SENDER"));
				}
				return response;
			} catch (CloneNotSupportedException e) {
				logger.error(e);
				return null;
			}

		} else
			return null;
	}

	public String getDirection() {
		return direction;
	}

	public String getField(String field) {
		if (telegramContent == null)
			return null;
		else
			return telegramContent.getString(field);
	}

	public String getString() {
		if (telegramContent == null)
			return telegramString;
		else
			return telegramContent.getString();
	}

	public String getTimeStamp() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(timeStamp);
	}

	public void setField(String field, String value) {
		telegramContent.getField(field).setValue(value);
	}
}
