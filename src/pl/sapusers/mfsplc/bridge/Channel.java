package pl.sapusers.mfsplc.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

public class Channel implements Runnable {
	private Logger logger = LogManager.getLogger(Channel.class.getName());

	/**
	 * Channel address as provided by SAP during channel start
	 */
	private String address;
	
	/**
	 * Channel port as provided by SAP during channel start
	 */
	private String port;
	private Socket socket;
	private BufferedReader reader;
	private OutputStreamWriter writer;
	
	/**
	 * SAP destination name that received telegrams with RFC call to /SCWM/MFS_RECEIVE2 function
	 */
	private String destination;
	private Thread thread;

	public String getDestination() {
		return destination;
	}

	public Channel(String destination, String address, String port) {
		this.destination = destination;
		this.address = address;
		this.port = port;
		this.thread = new Thread(this);
		this.thread.setName("Channel-" + this.thread.getName());
	}

	public void start() {
		thread.start();
	}
	
	public void createSocket() throws IOException {
		socket = new Socket(address, Integer.parseUnsignedInt(port));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new OutputStreamWriter(socket.getOutputStream());
	}

	public void closeSocket() throws IOException {
		socket.close();
	}

	public String getAddress() {
		return address + ":" + port;
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	public String getSocketStatus() {
		return "local port:" + socket.getLocalPort() + " isBound:" + socket.isBound() + " isClosed:" + socket.isClosed() + " isConnected:"
				+ socket.isConnected();
	}

	public String getThreadStatus() {
		return "Thread name: " + thread.getName() + ", Thread state: " + thread.getState();
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				if (reader.ready()) {
					sendTelegramToSAP(reader.readLine());
				}
			} catch (IOException e) {
				logger.catching(e);
			}
		}
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			logger.catching(e);
		}

	}

	public void sendTelegramToTCP(String telegram) {
		try {
			writer.write(telegram + "\n");
			writer.flush();
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	/**
	 * Sends a telegram to SAP with RFC call to /SCWM/MFS_RECEIVE2 function
	 * 
	 * @param telegram telegram content as String
	 */
	private void sendTelegramToSAP(String telegram) {
//		FUNCTION /SCWM/MFS_RECEIVE2.
//		*"----------------------------------------------------------------------
//		*"*"Local Interface:
//		*"  IMPORTING
//		*"     VALUE(IV_LGNUM) TYPE  /SCWM/LGNUM OPTIONAL
//		*"     VALUE(IV_PLC) TYPE  /SCWM/DE_MFSPLC OPTIONAL
//		*"     VALUE(IV_CHANNEL) TYPE  /SCWM/DE_MFSCCH OPTIONAL
//		*"     VALUE(IV_IPADDRESS) TYPE  /SCWM/DE_MFS_HOST OPTIONAL
//		*"     VALUE(IV_PORT) TYPE  ME_PORT OPTIONAL
//		*"     VALUE(IV_TELEGRAM) TYPE  /SCWM/DE_MFSTELE
//		*"  EXPORTING
//		*"     VALUE(EV_ERROR) TYPE  XFELD
//		*"----------------------------------------------------------------------	

		logger.info("Sending telegram to SAP: " + telegram);
		try {
			JCoFunction function = JCoDestinationManager.getDestination(destination).getRepository()
					.getFunction("/SCWM/MFS_RECEIVE2");
//			function.getImportParameterList().setValue("IV_LGNUM", "");
//			function.getImportParameterList().setValue("IV_PLC", "");
//			function.getImportParameterList().setValue("IV_CHANNEL", "");
			function.getImportParameterList().setValue("IV_IPADDRESS", address);
			function.getImportParameterList().setValue("IV_PORT", port);
			function.getImportParameterList().setValue("IV_TELEGRAM", telegram);
			function.execute(JCoDestinationManager.getDestination(destination));

			if (function.getExportParameterList().getString("EV_ERROR").equals("X"))
				logger.error("Call to /SCWM/MFS_RECEIVE2 returned error");

		} catch (JCoException e) {
			logger.catching(e);
		}
		logger.info("Telegram sent to SAP: " + telegram);
	}
}
