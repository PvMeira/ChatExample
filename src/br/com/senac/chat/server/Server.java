package br.com.senac.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {
	private static ArrayList<BufferedWriter> clients;
	private static ServerSocket server;
	private String name;
	private Socket connection;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;

	public Server(Socket socket) {
		this.connection = socket;
		try {
			inputStream = connection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (IOException e) {
			System.err.println("Error when trying to create a conection");
		}
	}

	public void run() {

		try {

			String message;
			OutputStream outputStream = this.connection.getOutputStream();
			Writer writer = new OutputStreamWriter(outputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			clients.add(bufferedWriter);
			name = message = bufferedReader.readLine();

			while (!"exit".equalsIgnoreCase(message) && message != null) {
				message = bufferedReader.readLine();
				sendToAll(bufferedWriter, message);
				System.out.println(message);
			}

		} catch (Exception e) {
			System.err.println("Error when trying to send the message to all users");
		}
	}

	public void sendToAll(BufferedWriter bufferWriter, String message) throws IOException {

		BufferedWriter bufferedWriter;

		for (BufferedWriter bw : clients) {
			bufferedWriter = (BufferedWriter) bw;
			if (!(bufferWriter == bufferedWriter)) {
				bw.write(name + " -> " + message + "\r\n");
				System.out.println("Mensagem : "+ message);
//				bw.flush();
			}
		}
	}

	public static void main(String[] args) {
		try {
			JLabel lblMessage = new JLabel("Server port:");

			JTextField txtPorta = new JTextField("12345");

			Object[] texts = { lblMessage, txtPorta };

			JOptionPane.showMessageDialog(null, texts);

			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			System.out.println("New Server socket created : " + server.getLocalSocketAddress());
			System.out.println(server.getLocalPort());
			System.out.println(server.getInetAddress());
			System.out.println(server.getReceiveBufferSize());
			clients = new ArrayList<BufferedWriter>();

			JOptionPane.showMessageDialog(null, "Server is active on the port : " + txtPorta.getText());

			while (true) {
				System.out.println("Waiting Conection ...");
				Socket conection = server.accept();
				System.out.println("Client connected...");
				Thread thread = new Server(conection);
				thread.start();
			}

		} catch (Exception e) {
			System.err.println("Error when trying to run the server");
		}
	}
}
