package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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

import pl.sapusers.mfsplc.Configurator;

@SuppressWarnings("serial")
public class TelegramsTextPane extends JTextPane {
	private Logger logger = LogManager.getLogger(TelegramsTextPane.class.getName());
	private ArrayList<Telegram> telegrams;

	public TelegramsTextPane(Configurator configurator, JScrollPane scrollPane) {
		super();

		telegrams = new ArrayList<Telegram>();
		
		setEditable(false);
		setFont(new Font("Monospaced", Font.PLAIN, 12));

		List<TelegramStyle> telegramStyles = configurator.getTelegramStyles();
		for (TelegramStyle telegramStyle : telegramStyles) {
			Style style = this.addStyle(telegramStyle.getName(), null);
			StyleConstants.setForeground(style, telegramStyle.getColor());
			StyleConstants.setItalic(style, telegramStyle.isItalic());
			StyleConstants.setBold(style, telegramStyle.isBold());
			StyleConstants.setUnderline(style, telegramStyle.isUnderline());
			StyleConstants.setStrikeThrough(style, telegramStyle.isStrikeThrough());
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

						Telegram telegram = telegrams.get(line);
						new TelegramDialog(telegram, true, "Line: " + (line + 1) + "; " + telegram.getDirection() + "; " + telegram.getTimeStamp());

					} catch (IndexOutOfBoundsException | BadLocationException exc) {
						logger.catching(exc);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				scrollPane.getRowHeader().setViewPosition(new Point(0, scrollPane.getViewport().getViewPosition().y));
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
				scrollPane.getColumnHeader()
						.setViewPosition(new Point(scrollPane.getViewport().getViewPosition().x, 0));
			}
		});

		scrollPane.setColumnHeaderView(ruler);
	}

	public void clear() {
		telegrams.clear();
		setText(null);
	}

	public void addTelegram(Telegram telegram) {
		Style style;
		style = this.getStyle(telegram.getType() + "-" + telegram.getHandshake());
		if (style == null)
			style = this.getStyle("*-" + telegram.getHandshake());

		StyledDocument doc = this.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), telegram.getString() + "\n", style);
			telegrams.add(telegram);

		} catch (BadLocationException e) {
			logger.catching(e);
		}
	}
}
