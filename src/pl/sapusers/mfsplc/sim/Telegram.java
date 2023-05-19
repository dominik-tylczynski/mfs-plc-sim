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
import com.sap.conn.jco.JCoRecordMetaData;
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
	private LocalDateTime timeStamp;

	public Telegram(Configurator configurator, String telegramString, String direction) {
		this.configurator = configurator;
		this.timeStamp = LocalDateTime.now();
		this.direction = direction;

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

	public Telegram(Telegram request, JCoStructure structure) {
		this.configurator = request.configurator;
		this.timeStamp = LocalDateTime.now();
		this.direction = Telegram.TO_SAP;

		telegramContent = structure;
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
		Telegram response = null;

		if (this.getField("HANDSHAKE").equals(configurator.getHandshakeRequest())) {

			switch (configurator.getHandshakeMode()) {
			case "A": // complete telegram
				response = new Telegram(configurator, this.getString(), Telegram.TO_SAP);
				response.setField("HANDSHAKE", configurator.getHandshakeConfirmation());
				break;

			case "B": // Sender, Recipient, Telegram Type, Sequence Number
				response = new Telegram(this, createHandshakeStructure());
				response.setField("SENDER", this.getField("SENDER"));
				response.setField("RECEIVER", this.getField("RECEIVER"));
				response.setField("TELETYPE", this.getField("TELETYPE"));
				response.setField("SEQU_NO", this.getField("SEQU_NO"));
				break;

			case "C": // Do not send confirmation
				logger.error("Handshake confirmation building called for handshake mode C");
				return null;

			case "D": // Send telegram header
				try {
					response = new Telegram(this,
							JCo.createStructure(JCoDestinationManager.getDestination(configurator.getJCoDestination())
									.getRepository()
									.getStructureDefinition(configurator.getTelegramStructureHeader())));
				} catch (IllegalArgumentException | JCoException e) {
					logger.error(e);
					return null;
				}
				response.setString(this.getString());
				response.setField("HANDSHAKE", configurator.getHandshakeConfirmation());
				break;
			}

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
	
	public void setString(String content) {
		telegramContent.setString(content);
	}

	public String getTimeStamp() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(timeStamp);
	}

	public void setField(String field, String value) {
		telegramContent.getField(field).setValue(value);
	}

	private JCoStructure createHandshakeStructure() {
		JCoRecordMetaData field;

//		Sender, Recipient, Telegram Type, Sequence Number

		try {
			JCoRecordMetaData handshakeMetaData = JCo.createRecordMetaData("handshake", 4);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSSENDER");
			handshakeMetaData.add("SENDER", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSRECEIVER");
			handshakeMetaData.add("RECEIVER", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSTELETYPE");
			handshakeMetaData.add("TELETYPE", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSSN");
			handshakeMetaData.add("SEQU_NO", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			handshakeMetaData.lock();

			return JCo.createStructure(handshakeMetaData);
		} catch (IllegalArgumentException | JCoException e) {
			logger.error(e);
			return null;
		}
	}
}
