package pl.sapusers.mfsplc.bridge;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.server.JCoServer;

public class Telegram {

	private JCoStructure telegramContent;

	public Telegram(JCoRecordMetaData telegramMetadata, String telegramString) {
		telegramContent = JCo.createStructure(telegramMetadata);
		telegramContent.setString(telegramString);
	}

	public Telegram(JCoServer server, String telegramStructure, String telegramString) throws JCoException {
		this(server.getRepository().getStructureDefinition(telegramStructure), telegramString);
	}

	/**
	 * Gets a value from a field in the telegram
	 *
	 * @param field telegram field to get the value from
	 * @return value of the telegram's field
	 */
	public String getField(String field) {
		return telegramContent.getString(field);
	}

	public String getString() {
		return telegramContent.getString();
	}

	/**
	 * Sets a value to a field in the telegram
	 *
	 * @param field the telegram's field to set the value to
	 * @param value the value to be set to the telegram's field
	 */
	public void setField(String field, String value) {
		telegramContent.getField(field).setValue(value);
	}
}
