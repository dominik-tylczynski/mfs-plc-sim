package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
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

	public TelegramsTextPane(Properties config, JCoRecordMetaData telegramMetadata, JScrollPane scrollPane) {
		super();
		this.telegramMetadata = telegramMetadata;

		setEditable(false);
		setFont(new Font("Monospaced", Font.PLAIN, 12));

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

// add left column with line numbers
		JTextArea lineNumbers = new JTextArea("1");
		lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 12));
		lineNumbers.setBackground(Color.LIGHT_GRAY);
		lineNumbers.setEditable(false);
		lineNumbers.setFocusable(false);
		lineNumbers.setHighlighter(null);
		lineNumbers.setMargin(getMargin());
		lineNumbers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					try {
						int caretPosition = lineNumbers.getCaretPosition();
						int line = lineNumbers.getDocument().getDefaultRootElement().getElementIndex(caretPosition);
						if (caretPosition > 1 && lineNumbers.getDocument().getText(caretPosition - 1, 1).equals("\n"))
							line--;
						Element element = getDocument().getDefaultRootElement().getElement(line);

						int start = element.getStartOffset();
						int end = element.getEndOffset();

						String text = getDocument().getText(start, end - start);
						text = text.replace("\n", "");
						if (!text.equals("")) {
							JCoStructure telegram = JCo.createStructure(telegramMetadata);
							telegram.setString(text);
							new TelegramDialog(telegram.getRecordFieldIterator(), true, "Line: " + (line + 1));
						}
					} catch (BadLocationException exc) {
						logger.catching(exc);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				scrollPane.getRowHeader().setViewPosition(new Point(0,scrollPane.getViewport().getViewPosition().y));			
			}
		});

		getDocument().addDocumentListener(new DocumentListener() {
			public String getText() {
				int caretPosition = getDocument().getLength();
				Element root = getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for (int i = 2; i < root.getElementIndex(caretPosition) + 1; i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}

			@Override
			public void changedUpdate(DocumentEvent de) {
				lineNumbers.setText(getText());
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				lineNumbers.setText(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				lineNumbers.setText(getText());
			}

		});

		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(this);
		scrollPane.setViewportView(noWrapPanel);
		scrollPane.setRowHeaderView(lineNumbers);

// add top ruler		
		JTextArea ruler = new JTextArea();
		ruler.setFont(new Font("Monospaced", Font.PLAIN, 12));
		ruler.setEditable(false);
		ruler.setFocusable(false);
		ruler.setMargin(getMargin());
		ruler.setBackground(Color.LIGHT_GRAY);
		for (int j = 0; j < 3; j++) {
			ruler.setText(ruler.getText() + (j == 0 ? "....:...." : "0....:...."));
			for (int i = 1; i < 10; i++) {
				ruler.setText(ruler.getText() + i + "....:....");
			}
		}
		
		ruler.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				scrollPane.getColumnHeader().setViewPosition(new Point(scrollPane.getViewport().getViewPosition().x,0));			
			}
		});
		
		scrollPane.setColumnHeaderView(ruler);
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
