package pl.sapusers.mfsplc.sim;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.server.JCoServer;

/**
 * Telegram object class
 * Currently not used
 * 
 */
public class Telegram {

	private JCoStructure telegramContent;

	/**
	 * Creates telegram object provided structure and content 
	 * 
	 * @param telegramMetadata Telegram structure description
	 * @param telegramString Telegram content
	 */
	public Telegram(JCoRecordMetaData telegramMetadata, String telegramString) {
		telegramContent = JCo.createStructure(telegramMetadata);
		telegramContent.setString(telegramString);
	}

	/**
	 * @param server JCoServer that is contacted to built telegram structure
	 * @param telegramStructure Name of telegram structure as defined in SAP Data Dictionary
	 * @param telegramString Telegram content
	 * @throws JCoException if SAP server can't be contacted or if telegram structure can't be built
	 */
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

	/**
	 * Returns telegram content as a string
	 * 
	 * @return telegram content as a string
	 */
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
