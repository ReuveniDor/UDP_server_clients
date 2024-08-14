

import java.io.IOException;
import java.net.MulticastSocket;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.net.*;

public class ClientController {

	private String defaultGroup = "230.0.0.1";
	private int port = 6666;
	private MulticastSocket socket;
	private boolean connected = false;
	private InetAddress group;
	private String textToShow;
	private int numOfPacketsRecieved = 0;

	@FXML
	private TextArea textRecieved;

	@FXML
	private TextArea portProvided;

	@FXML
	void clear(ActionEvent event) {
		textRecieved.clear();
	}

	@FXML
	void connectToProvided(ActionEvent event) {
		connect(portProvided.getText());
	}

	@FXML
	void defaultConnection(ActionEvent event) {
		connect(defaultGroup);
	}

	@FXML
	synchronized void dissconnect(ActionEvent event) {
		if(connected) {
			try {
				socket.leaveGroup(group);
				socket.close();
				connected = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void connect(String ip) {
		if(!connected) {
			try {
				socket = new MulticastSocket(port);
				group = InetAddress.getByName(ip);
				socket.joinGroup(group);
				connected = true;
				waitForPacket();
			} catch (IOException e) {
				portProvided.setText("Please provide a valid name");
			}
		}
	}

	private synchronized void recievedPacket(DatagramPacket packetRecieved) {
		textToShow = "" + new String(packetRecieved.getData()).substring(0, packetRecieved.getLength());
		textToShow += "\n" + LocalDateTime.now();

		Platform.runLater(new Runnable() {
			public void run() {
				textRecieved.setText(textToShow);
			}
		});

	}

	private void waitForPacket() {
		RecievingThread thread = new RecievingThread(""+numOfPacketsRecieved);
		thread.start();
	}

	public class RecievingThread extends Thread{

		public RecievingThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			try {
				byte[] buf = new byte[256];
				DatagramPacket packetRecieved = new DatagramPacket(buf, buf.length);
				socket.receive(packetRecieved);
				recievedPacket(packetRecieved);
				waitForPacket();
			} catch (IOException e) {
				//Socket closed - the thread will die and the connection is lost
			}
		}

	}
}
