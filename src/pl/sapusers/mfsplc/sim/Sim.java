package pl.sapusers.mfsplc.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;

import pl.sapusers.mfsplc.Configurator;

@SuppressWarnings("serial")
public class Sim extends JFrame {
	private Logger logger = LogManager.getLogger(Sim.class.getName());

	private Configurator configurator;
	private JCoRecordMetaData telegramMetadata; // TODO remove
	private ArrayList<Telegram> telegrams;
	private Thread processor;
	private Thread monitor;

	private TcpServer server;
	private TelegramsTextPane textTelegrams;
	private JToggleButton tglbtnLife;
	private JTextField textPort;
	private JToggleButton tglAutoHandshake;
	private JButton btnSend;

	class TcpIpMonitor implements Runnable {
		@Override
		public void run() {
			while (server.isRunning()) {

				synchronized (server) {
					try {
						server.wait();

						if (server.isClientConnected()) {
							textPort.setBackground(Color.GREEN);
							btnSend.setEnabled(true);
						} else {
							textPort.setBackground(Color.YELLOW);
							btnSend.setEnabled(false);
						}

					} catch (InterruptedException e) {
						logger.debug(e);
					}
				}
			}
		}
	}

	class TcpIpProcessor implements Runnable {

		@Override
		public void run() {

			while (server.isRunning()) {
				String message = null;
				try {
					message = server.incoming.take();
					JCoStructure telegram = JCo.createStructure(telegramMetadata);
					telegram.setString(message);

					if (telegram.getString("TELETYPE").equals(configurator.getTelegramType("LIFE"))
							&& tglbtnLife.isSelected()
							|| !telegram.getString("TELETYPE").equals(configurator.getTelegramType("LIFE")))
						textTelegrams.addTelegram(telegram);

					// send acknowledge telegram if needed
					if (telegram.getString("HANDSHAKE").equals(configurator.getHandshakeRequest())
							&& tglAutoHandshake.isSelected()) {
						JCoStructure response = (JCoStructure) telegram.clone();
						response.getField("HANDSHAKE").setValue(configurator.getHandshakeConfirmation());

						if (configurator.getSwitchSenderReceiver()) {
							response.getField("SENDER").setValue(telegram.getString("RECEIVER"));
							response.getField("RECEIVER").setValue(telegram.getString("SENDER"));
						}
						server.outgoing.add(response.getString());

						if (response.getString("TELETYPE").equals(configurator.getTelegramType("LIFE"))
								&& tglbtnLife.isSelected()
								|| !response.getString("TELETYPE").equals(configurator.getTelegramType("LIFE")))
							textTelegrams.addTelegram(response);

					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static void printUsage() {
		System.out.println("Run MfsSim providing two arguments:");
		System.out.println("  1. name of SAP server");
		System.out.println("  2. properties file with MfsSim configuration");
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Sim frame = null;
				try {
					if (args.length == 1)
						frame = new Sim(new Configurator(args[0], null, null));
					else if (args.length == 2)
						frame = new Sim(new Configurator(args[1], args[0], null));
					else {
						printUsage();
						System.exit(0);
					}

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
	public Sim(Configurator configurator) {
		this.configurator = configurator;

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
		scrollPane.setAlignmentX(0.0f);
		scrollPane.setAlignmentY(0.0f);
		scrollPane.setViewportBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		TelegramPanel.add(scrollPane, BorderLayout.CENTER);

		textTelegrams = new TelegramsTextPane(this.configurator, telegramMetadata, scrollPane);

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
				textTelegrams.setText(null);
				telegrams.clear();
			}
		});
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ac) {

				if (textPort.isEditable()) {

					try {
						server = new TcpServer(Integer.parseUnsignedInt(textPort.getText()));
						processor = new Thread(new TcpIpProcessor());
						processor.setName("Telegram processor " + processor.getName());
						processor.start();
						monitor = new Thread(new TcpIpMonitor());
						monitor.setName("Server monitor " + monitor.getName());
						monitor.start();
						textPort.setEditable(false);
						textPort.setBackground(Color.YELLOW);
						textPort.setText(Integer.toString(Integer.parseUnsignedInt(textPort.getText())));
						btnStartStop.setText("Stop");
					} catch (NumberFormatException | IOException e) {
						JOptionPane.showMessageDialog(null, e, "Server could not be started",
								JOptionPane.ERROR_MESSAGE);
						logger.catching(e);

						textPort.setEditable(true);
						btnStartStop.setText("Start");
						textPort.setBackground(Color.WHITE);
						server.stopServer();
						processor.interrupt();
						monitor.interrupt();
					}

				} else {
					server.stopServer();
					processor.interrupt();
					monitor.interrupt();
					textPort.setEditable(true);
					btnStartStop.setText("Start");
					textPort.setBackground(Color.WHITE);
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
					server.outgoing.add(textTelegram.getText());
					textTelegrams.addTelegram(textTelegram.getText());
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
		return textTelegrams;
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
