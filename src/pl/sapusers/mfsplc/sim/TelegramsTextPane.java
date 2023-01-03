package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.util.Properties;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;

@SuppressWarnings("serial")
public class TelegramsTextPane extends JTextPane {
	private Logger logger = LogManager.getLogger(TelegramsTextPane.class.getName());
	private JCoRecordMetaData telegramMetadata;

	public TelegramsTextPane(Properties config, JCoRecordMetaData telegramMetadata) {
		super();
		this.telegramMetadata = telegramMetadata;

		Set<String> propertyKeys = config.stringPropertyNames();

		for (String propertyKey : propertyKeys) {
			if (propertyKey.contains("Style")) {
				String styleName = propertyKey.substring(6);
				String[] styleParts = config.getProperty(propertyKey).split(",");

				Style style = this.addStyle(styleName, null);
				StyleConstants.setForeground(style, new Color(Integer.parseInt(styleParts[0]),
						Integer.parseInt(styleParts[1]), Integer.parseInt(styleParts[2])));

				if (styleParts.length == 4) {
					if (styleParts[3].contains("I"))
						StyleConstants.setItalic(style, true);
					if (styleParts[3].contains("B"))
						StyleConstants.setBold(style, true);
					if (styleParts[3].contains("U"))
						StyleConstants.setUnderline(style, true);
					if (styleParts[3].contains("S"))
						StyleConstants.setStrikeThrough(style, true);
				}
			}
		}
	}

	public void addTelegram(String message) {
		JCoStructure telegram = JCo.createStructure(telegramMetadata);
		telegram.setString(message);
		addTelegram(telegram);
	}

	public void addTelegram(JCoStructure telegram) {

		Style style;
		style = this.getStyle(telegram.getString("TELETYPE") + "-" + telegram.getString("HANDSHAKE"));
		if (style == null)
			style = this.getStyle("*-" + telegram.getString("HANDSHAKE"));

		StyledDocument doc = this.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), telegram.getString() + "\n", style);
		} catch (BadLocationException e) {
			logger.catching(e);
		}
	}
}


