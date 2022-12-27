package pl.sapusers.mfsplc.bridge;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.AbapClassException;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoRuntimeException;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;

public class Bridge implements JCoServerFunctionHandler, JCoServerExceptionListener, JCoServerErrorListener,
		JCoServerStateChangedListener {
	private Logger logger = LogManager.getLogger(Bridge.class.getName());

	private JCoServer server;

// configuration settings	
	private String sendingFM;
	private String startingFM;
	private String stoppingFM;
	private String statusFM;

// communication channels 
	Map<String, Channel> channels = new HashMap<>();

	public Bridge(String serverName, String configProperties) throws JCoException {

		this.server = JCoServerFactory.getServer(serverName);

		loadConfiguration(configProperties);
		buildCustomRepository();

		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler("RFC_EXECUTE_COMMAND", this);
		factory.registerGenericHandler(this);
		server.setCallHandlerFactory(factory);

		server.addServerErrorListener(this);
		server.addServerExceptionListener(this);
		server.addServerStateChangedListener(this);
	}

	private void startRFCServer() {
		logger.info("Starting RFC server");
		server.start();
		logger.info("RFC server started");
	}

	private void stopRFCServer() {
		logger.info("Stopping RFC server");
		server.stop();
		logger.info("RFC server stopped");
	}

	private void loadConfiguration(String configProperties) {
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

		// Get RFC function modules for A "proprietary" type channel
		// TODO implement support of A type channel
		sendingFM = config.getProperty("sendingFM");
		startingFM = config.getProperty("startingFM");
		stoppingFM = config.getProperty("stoppingFM");
		statusFM = config.getProperty("statusFM");
	}

	private void buildCustomRepository() {

		JCoCustomRepository customRepository = JCo.createCustomRepository("MfsRepository");

		try {
			customRepository.setDestination(JCoDestinationManager.getDestination(server.getRepositoryDestination()));

//		FUNCTION RFC_EXECUTE_COMMAND .
//		*"----------------------------------------------------------------------
//		*"*"Local Interface:
//		*"  IMPORTING
//		*"     REFERENCE(IV_COMMAND) TYPE  CHAR20
//		*"  TABLES
//		*"      CT_DATA TYPE  /SCWM/TT_MFS_TELE
//		*"----------------------------------------------------------------------		
			JCoListMetaData imports = JCo.createListMetaData("IV_COMMAND");
			imports.add("IV_COMMAND", JCoListMetaData.TYPE_CHAR, customRepository.getRecordMetaData("CHAR20"),
					JCoListMetaData.IMPORT_PARAMETER);
			imports.lock();

			JCoListMetaData tables = JCo.createListMetaData("CT_DATA");
			tables.add("CT_DATA", JCoListMetaData.TYPE_TABLE, customRepository.getRecordMetaData("/SCWM/TT_MFS_TELE"),
					0);
			tables.lock();

			JCoFunctionTemplate function = JCo.createFunctionTemplate("RFC_EXECUTE_COMMAND", imports, null, null,
					tables, null);
			customRepository.addFunctionTemplateToCache(function);
		} catch (JCoRuntimeException | JCoException e) {
			logger.catching(e);
		}

		server.setRepository(customRepository);

// to test - save repository in json file	
//		try {
//			FileWriter writer = new FileWriter(new File("test1.json").getAbsolutePath());
//			customRepository.save(writer);
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	private void stopChannels() {
		Iterator<Map.Entry<String, Channel>> iterator = channels.entrySet().iterator();
		while (iterator.hasNext()) {
			try {
				iterator.next().getValue().closeSocket();
			} catch (IOException e) {
				logger.catching(e);
			}
			iterator.remove();
		}
	}

	private void printStatus() {
// Print RFC server status 
		System.out.println("*** RFC Server Status ***");
		System.out
				.println("Destination: " + server.getRepositoryDestination() + " Program ID: " + server.getProgramID());
		System.out.println("State: " + server.getState());

// Print channels' statuses
		System.out.println("*** Communication Channels Status ***");
		System.out.println("Number of channels: " + channels.size());
		for (var entry : channels.entrySet()) {
			System.out.println("Key: " + entry.getKey() + " Address: " + entry.getValue().getAddress()
					+ " Destination: " + entry.getValue().getDestination());
			System.out.println("   Socket status: " + entry.getValue().getSocketStatus());
			System.out.println("   " + entry.getValue().getThreadStatus());
		}
	}

	private static void printUsage() {
		System.out.println("Run MfsBridge providing two arguments:");
		System.out.println("  1. name of JCo RFC server");
		System.out.println("  2. properties file with MfsBridge configuration");
	}

	private void printHelp() {
		System.out.println("The following commands are supported in the console:");
		System.out.println("  - stop, exit, bye - to stop and exit MfsBridge");
		System.out.println("  - status - to display MfsBridge status");
		System.out.println("  - help, ? - to display this help message");
	}

	public static void main(String[] args) throws JCoException {

		if (args.length != 2) {
			printUsage();
			System.exit(0);
		}

		Bridge bridge = new Bridge(args[0], args[1]);
		bridge.startRFCServer();
		System.out.println("RFC server started. Ready for telegrams.");

		BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
		String inputLine = null;

		// wait for stop or exit or bye from keyboard to stop RFC server and exit
		// application
		try {
			while ((inputLine = keyboardReader.readLine()) != null) {

				if (inputLine.equalsIgnoreCase("stop") || inputLine.equalsIgnoreCase("exit")
						|| inputLine.equalsIgnoreCase("bye")) {

					bridge.stopChannels();
					bridge.stopRFCServer();
					System.exit(0);
				} else if (inputLine.equalsIgnoreCase("status")) {
					bridge.printStatus();
				} else if (inputLine.equalsIgnoreCase("help") || inputLine.equalsIgnoreCase("?")) {
					bridge.printHelp();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {

		logger.debug("RFC call from SAP to: " + function.getName());
		logger.trace(XmlFormatter.format(function.toXML()));

		if (function.getName().equals("RFC_EXECUTE_COMMAND"))
			handleRfcExecuteCommand(serverCtx, function);
		else if (function.getName().equals(sendingFM))
			handleSendingFM(serverCtx, function);
		else if (function.getName().equals(startingFM))
			handleStartingFM(serverCtx, function);
		else if (function.getName().equals(stoppingFM))
			handleStoppingFM(serverCtx, function);
		else if (function.getName().equals(statusFM))
			handleStatusFM(serverCtx, function);
		else
			logger.warn(
					"RFC call from SAP to unimplemented function: " + function.getName() + " Call will be ignored.");
	}

	private void handleSendingFM(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {
		// TODO implement support of A type channel
	}

	private void handleStartingFM(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {
		// TODO implement support of A type channel
	}

	private void handleStoppingFM(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {
		// TODO implement support of A type channel
	}

	private void handleStatusFM(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {
		// TODO implement support of A type channel
	}

	private void handleRfcExecuteCommand(JCoServerContext serverCtx, JCoFunction function)
			throws AbapException, AbapClassException {

//		FUNCTION RFC_EXECUTE_COMMAND .
//		*"----------------------------------------------------------------------
//		*"*"Local Interface:
//		*"  IMPORTING
//		*"     REFERENCE(IV_COMMAND) TYPE  CHAR20
//		*"  TABLES
//		*"      CT_DATA TYPE  /SCWM/TT_MFS_TELE
//		*"----------------------------------------------------------------------			

		String iv_command;
		String telegramString = new String();
		String address = new String();
		String port = new String();

		iv_command = function.getImportParameterList().getString("IV_COMMAND");

		JCoTable ct_data = function.getTableParameterList().getTable("CT_DATA");
		for (int i = 0; i < ct_data.getNumRows(); i++) {
			ct_data.setRow(i);
			switch (i) {
			case 0: // IP address
				address = ct_data.getString();
				break;
			case 1: // port number
				port = ct_data.getString();
				break;
			case 2: // iv_command = SEND, telegram content, iv_command = START, telegram end char
				telegramString = ct_data.getString();
				break;
			case 4: // iv_command = SEND, telegram length
				telegramString = telegramString + " " + ct_data.getString();
				break;
			}
		}

		logger.debug("RFC_EXCUTE_COMMAND: IV_COMMAND: " + iv_command + " address: " + address + " port: " + port
				+ " telegram: " + telegramString);

		if (iv_command.equals("START")) {
			Channel channel = new Channel(server.getRepositoryDestination(), address, port);
			try {
				logger.debug("*** Starting channel: " + address + ":" + port);
				logger.debug("Creating socket: " + address + ":" + port);
				channel.createSocket();
				logger.debug("Socket created: " + address + ":" + port);
				channels.put(channel.getAddress(), channel);
				logger.debug("Starting new thread for channel: " + address + ":" + port);
				channel.thread.start();
				logger.debug("New thread started for channel: " + address + ":" + port);
				logger.debug("*** Channel started: " + address + ":" + port);
			} catch (IOException e) {
				logger.catching(e);
				throw new AbapException(e.getMessage() + " " + address + ":" + port);
			}

		}

		if (iv_command.equals("STOP")) {
			try {
				logger.info("*** Stopping channel: " + address + ":" + port);
				logger.info("Closing socket for channel: " + address + ":" + port);
				channels.get(address + ":" + port).closeSocket();
				logger.info("Socket for channel closed: " + address + ":" + port);
				channels.remove(address + ":" + port);
				logger.info("*** Channel stopped: " + address + ":" + port);
			} catch (IOException e) {
				logger.catching(e);
			}
		}

		if (iv_command.equals("SEND")) {
			Channel channel;
			if ((channel = channels.get(address + ":" + port)) == null) {
				logger.error("Can't send to channel as it is not started yet: " + address + ":" + port);
			} else {
				channel.sendTelegramToTCP(telegramString);
			}
		}

	}

	@Override
	public void serverExceptionOccurred(JCoServer server, String connectionID, JCoServerContextInfo serverCtx,
			Exception exception) {
		logger.catching(exception);
	}

	@Override
	public void serverErrorOccurred(JCoServer server, String connectionID, JCoServerContextInfo serverCtx,
			Error error) {
		logger.catching(error);
	}

	@Override
	public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState) {
		logger.info("RFC server state changed from " + oldState + " to " + newState);
	}
}
