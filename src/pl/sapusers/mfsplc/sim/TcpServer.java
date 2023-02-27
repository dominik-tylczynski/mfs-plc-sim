package pl.sapusers.mfsplc.sim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpServer {
	private int QUEUE_SIZE = 1000; 
	
	private Logger logger = LogManager.getLogger(TcpServer.class.getName());
	private ServerSocket serverSocket;
	private Socket socket;
	
	public ArrayBlockingQueue<String> incoming = new ArrayBlockingQueue<String>(QUEUE_SIZE);
	public ArrayBlockingQueue<String> outgoing = new ArrayBlockingQueue<String>(QUEUE_SIZE);

	class MfsSocketSender implements Runnable {
		@Override
		public void run() {
			try {
				BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				while (!socket.isClosed()) {
					// Send messages from server to client
					try {
						String out = outgoing.take();
						logger.debug("TCP server thread received: " + out);
						toClient.write(out + "\n");
						toClient.flush();
						logger.debug("Send to TCP client: " + out);
					} catch (InterruptedException e) {
						logger.debug(e);
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
							incoming.add(message);
							logger.debug("Number of received message in the buffer: " + incoming.size());
						}
					}
					fromClient.close();
				} catch (IOException | IllegalStateException e) {
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

//	public void sendMessage(String message) {
//		outgoing.add(message);
//	}
//
//	public String receiveMessage() {
//		if (incoming.size() > 0) {
//			String message = incoming.get(0);
//			incoming.remove(0);
//			return message;
//		} else
//			return null;
//	}

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
					tcpServer.outgoing.add(message);
				}
			} catch (IOException | IllegalStateException e) {
			}
			String message;
			try {
				message = tcpServer.incoming.take();
			} catch (InterruptedException e) {
				message = null;
				e.printStackTrace();
			}
			if (message != null) {
				System.out.println("Main received: " + message);
			}

		}

	}

}
