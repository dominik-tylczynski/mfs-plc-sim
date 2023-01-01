package pl.sapusers.mfsplc.sim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpServer {
	private Logger logger = LogManager.getLogger(TcpServer.class.getName());
	private ServerSocket serverSocket;
	private Socket socket;
	private ArrayList<String> received = new ArrayList<String>();
	private ArrayList<String> outgoing = new ArrayList<String>();

	class MfsSocketSender implements Runnable {

		@Override
		public void run() {
			try {
				BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				while (!socket.isClosed()) {
					// Send messages from server to client
					if (outgoing.size() > 0) {
						String out = outgoing.get(0);
						logger.debug("TCP server thread received: " + out);
						toClient.write(out + "\n");
						toClient.flush();
						logger.debug("Send to TCP client: " + out);
						outgoing.remove(0);
					}
				}
				toClient.close();
			} catch (IOException e) {
				logger.catching(e);
			}
		}
	}

	class MfsSocketReceiver implements Runnable {

		@Override
		public void run() {
			while (!serverSocket.isClosed()) {
				try {
					socket = serverSocket.accept();
					logger.info("Client connected");

					Thread senderThread = new Thread(new MfsSocketSender());
					senderThread.setName("Socket Sender " + senderThread.getName());
					senderThread.start();

					BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					while (!socket.isClosed()) {
						// Input message from TCP client
						String message = fromClient.readLine();
						if (message == null) {
							logger.info("TCP client disconnected");
							socket.close();
						} else {
							logger.debug("Received from TCP client: " + message);
							received.add(message);
							logger.debug("Number of received message in the buffer: " + received.size());
						}
					}
					fromClient.close();
				} catch (IOException e) {
					logger.debug(e);
				}
			}
		}
	}

	public TcpServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		Thread receiverThread = new Thread(new MfsSocketReceiver());
		receiverThread.setName("Socket Receiver " + receiverThread.getName());
		receiverThread.start();
	}

	public void sendMessage(String message) {
		outgoing.add(message);
	}

	public String receiveMessage() {
		if (received.size() > 0) {
			String message = received.get(0);
			received.remove(0);
			return message;
		} else
			return null;
	}

	public void stopServer() {
		try {
			logger.debug("Stopping TCP server");
			if (socket != null)
				socket.close();
			serverSocket.close();
			logger.debug("Server socket closed");
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	public boolean isRunning() {
		return !serverSocket.isClosed();
	}

	public boolean isClientConnected() {
		if (socket == null)
			return false;
		else
			return !socket.isClosed();
	}

	public static void main(String args[]) throws IOException {
		TcpServer tcpServer = null;

		tcpServer = new TcpServer(6969);

		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				if (fromKeyboard.ready()) {
					String message = fromKeyboard.readLine();
					if (message.equals("exit")) {
						tcpServer.stopServer();
						System.exit(0);
					}
					tcpServer.sendMessage(message);
				}
			} catch (IOException e) {
			}
			String message = tcpServer.receiveMessage();
			if (message != null) {
				System.out.println("Main received: " + message);
			}

		}

	}

}
