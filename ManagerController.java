import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.*;

public class ManagerController {

	DatagramSocket socket;
	private String group = "230.0.0.1";
	private int port = 6666;
	InetAddress address;

    @FXML
    private TextField text;
	
	@FXML
	void sendMessage(ActionEvent event) {
		
		byte[] data = text.getText().getBytes();
		
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		text.clear();
	}

	@FXML
	void initialize() {
		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(group);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
