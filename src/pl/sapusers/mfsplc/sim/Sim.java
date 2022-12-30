package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;

import pl.sapusers.mfsplc.bridge.JFieldTextValidator;

@SuppressWarnings("serial")
public class Sim extends JFrame {
	private Logger logger = LogManager.getLogger(Sim.class.getName());

// Configuration
	private String handshakeRequest;
	private String handshakeConfirmation;
	private boolean switchSenderReceiver;
	private JCoRecordMetaData telegramMetadata;

	private TcpServer server;
	private JTextPane textArea;
	private JToggleButton tglbtnLife;
	private JTextField textPort;
	private JToggleButton tglAutoHandshake;
	private JButton btnSend;

	class MfsProcessor implements Runnable {

		@Override
		public void run() {

			while (server.isRunning()) {

				if (server.isClientConnected()) {
					textPort.setBackground(Color.GREEN);
					btnSend.setEnabled(true);
				} else {
					textPort.setBackground(Color.YELLOW);
					btnSend.setEnabled(false);
				}

				String message = null;
				if ((message = server.receiveMessage()) != null) {

					JCoStructure telegram = JCo.createStructure(telegramMetadata);
					telegram.setString(message);

					if (telegram.getString("TELETYPE").equals("LIFE") && tglbtnLife.isSelected()
							|| !telegram.getString("TELETYPE").equals("LIFE")) {
						Document doc = textArea.getDocument();
						try {
							doc.insertString(doc.getLength(), message + "\n", null);
						} catch (BadLocationException e) {
							logger.catching(e);
						}
					}

					// send acknowledge telegram if needed
					if (telegram.getString("HANDSHAKE").equals(handshakeRequest) && tglAutoHandshake.isSelected()) {
						JCoStructure response = (JCoStructure) telegram.clone();
						response.getField("HANDSHAKE").setValue(handshakeConfirmation);

						if (switchSenderReceiver) {
							response.getField("SENDER").setValue(telegram.getString("RECEIVER"));
							response.getField("RECEIVER").setValue(telegram.getString("SENDER"));
						}
						server.sendMessage(response.getString());

						if (response.getString("TELETYPE").equals("LIFE") && tglbtnLife.isSelected()
								|| !response.getString("TELETYPE").equals("LIFE")) {
							Document doc = textArea.getDocument();
							try {
								doc.insertString(doc.getLength(), response.getString() + "\n", null);
							} catch (BadLocationException e) {
								logger.catching(e);
							}
						}
					}
				}
			}
			textPort.setBackground(Color.WHITE);
			btnSend.setEnabled(false);
		}
	}

	private static void printUsage() {
		System.out.println("Run MfsSim providing two arguments:");
		System.out.println("  1. name of SAP server");
		System.out.println("  2. properties file with MfsSim configuration");
	}

	private void loadConfiguration(String destination, String configProperties) {
		// Load configuration from properties file
		Properties config = new Properties();
		logger.debug("Loading properites file: " + configProperties);
		try (FileInputStream propertiesFile = new FileInputStream(configProperties)) {
			config.load(propertiesFile);
		} catch (FileNotFoundException e) {
			logger.catching(e);
		} catch (IOException e) {
			logger.catching(e);
		}

		// Get handshake request and confirmation strings
		handshakeRequest = config.getProperty("handshakeRequest");
		if (handshakeRequest == null || handshakeRequest.equals(""))
			logger.error("Handshake request not defined in the config file: " + configProperties);

		handshakeConfirmation = config.getProperty("handshakeConfirmation");
		if (handshakeConfirmation == null || handshakeConfirmation.equals(""))
			logger.error("Handshake confirmation not defined in the config file: " + configProperties);

		// Get sender / receiver switch setting
		switchSenderReceiver = Boolean.parseBoolean(config.getProperty("switchSenderReceiver"));

		// Get JCo metadata of telegram structure
		try {
			telegramMetadata = JCoDestinationManager.getDestination(destination).getRepository()
					.getStructureDefinition(config.getProperty("telegramStructure"));
		} catch (JCoException e) {
			logger.throwing(e);
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			printUsage();
			System.exit(0);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sim frame = new Sim();
					frame.loadConfiguration(args[0], args[1]);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Sim() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 823, 558);
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JPanel TelegramPanel = new JPanel();
		contentPanel.add(TelegramPanel, BorderLayout.CENTER);
		TelegramPanel.setLayout(new BorderLayout(0, 0));

		JPanel OutboundTelegramPanel = new JPanel();
		TelegramPanel.add(OutboundTelegramPanel, BorderLayout.NORTH);
		OutboundTelegramPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Outgoing Telegram",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		OutboundTelegramPanel.setLayout(new BorderLayout(0, 0));

		JTextField textTelegram = new JTextField();
		OutboundTelegramPanel.add(textTelegram, BorderLayout.NORTH);
		textTelegram.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JCoStructure telegram = JCo.createStructure(telegramMetadata);
					telegram.setString(textTelegram.getText());
					new TelegramDialog(telegram.getRecordFieldIterator(), false, "Outbound telegram");
					textTelegram.setText(telegram.getString());
				}
			}
		});
		textTelegram.setHorizontalAlignment(SwingConstants.LEFT);
		textTelegram.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textTelegram.setColumns(100);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(
				new TitledBorder(null, "Telegrams Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		TelegramPanel.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextPane();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPane.setViewportView(textArea);

		JPanel TopPanel = new JPanel();
		TopPanel.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(TopPanel, BorderLayout.NORTH);
		TopPanel.setLayout(new BorderLayout(0, 0));

		JPanel TopLeftPanel = new JPanel();
		TopPanel.add(TopLeftPanel, BorderLayout.WEST);

		JLabel lblPort = new JLabel("Port");
		TopLeftPanel.add(lblPort);

		textPort = new JTextField();
		TopLeftPanel.add(textPort);
		textPort.setToolTipText("IP port for TCP/IP server");
		textPort.setColumns(5);
		textPort.setDocument(new JFieldTextValidator(textPort.getColumns(), 65535));

		JButton btnStartStop = new JButton("Start");
		TopLeftPanel.add(btnStartStop);
		btnStartStop.setToolTipText("Start TCP/IP server");

		tglAutoHandshake = new JToggleButton("Handshake");
		TopLeftPanel.add(tglAutoHandshake);
		tglAutoHandshake.setToolTipText("Send handshake telegrams automatically");
		tglAutoHandshake.setSelected(true);
		tglAutoHandshake.setHorizontalAlignment(SwingConstants.RIGHT);

		tglbtnLife = new JToggleButton("Life");
		TopLeftPanel.add(tglbtnLife);
		tglbtnLife.setToolTipText("Show LIFE telegrams");

		JButton btnClear = new JButton("Clear");
		TopLeftPanel.add(btnClear);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ac) {
				if (textPort.isEditable()) {

					try {
						server = new TcpServer(Integer.parseUnsignedInt(textPort.getText()));
						Thread thread = new Thread(new MfsProcessor());
						thread.setName("Telegram processor " + thread.getName());
						thread.start();
						textPort.setEditable(false);
						textPort.setBackground(Color.YELLOW);
						textPort.setText(Integer.toString(Integer.parseUnsignedInt(textPort.getText())));
						btnStartStop.setText("Stop");
					} catch (NumberFormatException | IOException e) {
						JOptionPane.showMessageDialog(null, e, "Server could not be started",
								JOptionPane.ERROR_MESSAGE);
						logger.catching(e);
					}

				} else {
					textPort.setEditable(true);
					btnStartStop.setText("Start");
					textPort.setBackground(Color.WHITE);
					server.stopServer();
				}
			}
		});

		JPanel TopRightPanel = new JPanel();
		TopPanel.add(TopRightPanel, BorderLayout.EAST);

		btnSend = new JButton("Send");
		TopRightPanel.add(btnSend);
		btnSend.setEnabled(false);
		btnSend.setHorizontalAlignment(SwingConstants.LEFT);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textTelegram.getText().equals("")) {
					server.sendMessage(textTelegram.getText());

					Document doc = textArea.getDocument();
					try {
						doc.insertString(doc.getLength(), textTelegram.getText() + "\n", null);
					} catch (BadLocationException exc) {
						logger.catching(exc);
					}

					textTelegram.setText("");
				}
			}
		});

		JPanel BottomPannel = new JPanel();
		contentPanel.add(BottomPannel, BorderLayout.SOUTH);
		BottomPannel.setLayout(new BorderLayout(0, 0));

		JLabel lblAuthor = new JLabel("By Dominik Tylczyński");
		BottomPannel.add(lblAuthor, BorderLayout.WEST);

		JLabel lblLicense = new JLabel("Copyleft: GNU AGPLv3");
		BottomPannel.add(lblLicense, BorderLayout.EAST);
	}

	public JTextPane getTextArea() {
		return textArea;
	}

	public JToggleButton getTglbtnLife() {
		return tglbtnLife;
	}

	public JTextField getTextPort() {
		return textPort;
	}

	public JToggleButton getTglAutoHandshake() {
		return tglAutoHandshake;
	}

	public JButton getBtnSendButton() {
		return btnSend;
	}
}
