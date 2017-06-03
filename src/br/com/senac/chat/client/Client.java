package br.com.senac.chat.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private JTextField textMessage;
	private JButton buttonSEND;
	private JButton buttonEXIT;
	private JLabel labelHistory;
	private JLabel labelMessage;
	private JPanel panelContent;
	private Socket socket;
	private OutputStream outputStream;
	private Writer writer;
	private BufferedWriter bufferReader;
	private JTextField textFieldIp;
	private JTextField textFildPort;
	private JTextField textFieldName;

	public Client() throws IOException {
		JLabel lblMessage = new JLabel("Verify!");
		textFieldIp = new JTextField("127.0.0.1");
		textFildPort = new JTextField("12345");
		textFieldName = new JTextField("Client");
		Object[] texts = { lblMessage, textFieldIp, textFildPort, textFieldName };
		JOptionPane.showMessageDialog(null, texts);
		panelContent = new JPanel();
		textArea = new JTextArea(10, 20);
		textArea.setEditable(false);
		textArea.setBackground(new Color(240, 240, 240));
		textMessage = new JTextField(20);
		labelHistory = new JLabel("History");
		labelMessage = new JLabel("Message");
		buttonSEND = new JButton("Send");
		buttonSEND.setToolTipText("Send Message");
		buttonEXIT = new JButton("exit");
		buttonEXIT.setToolTipText("quit form chat");
		buttonSEND.addActionListener(this);
		buttonEXIT.addActionListener(this);
		buttonSEND.addKeyListener(this);
		textMessage.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		panelContent.add(labelHistory);
		panelContent.add(scroll);
		panelContent.add(labelMessage);
		panelContent.add(textMessage);
		panelContent.add(buttonEXIT);
		panelContent.add(buttonSEND);
		panelContent.setBackground(Color.LIGHT_GRAY);
		textArea.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		textMessage.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		setTitle(textFieldName.getText());
		setContentPane(panelContent);
		setLocationRelativeTo(null);
		setResizable(true);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void conect() throws IOException {

		socket = new Socket(textFieldIp.getText(), Integer.parseInt(textFildPort.getText()));
		outputStream = socket.getOutputStream();
		writer = new OutputStreamWriter(outputStream);
		bufferReader = new BufferedWriter(writer);
		bufferReader.write(textFieldName.getText() + "\r\n");
		bufferReader.flush();
	}

	public void sendMessage(String msg) throws IOException {

		if (msg.equalsIgnoreCase("exit")) {
			bufferReader.write("exiting \r\n");
			textArea.append("exit sucessful \r\n");
		} else {
			bufferReader.write(msg + "\r\n");
			textArea.append(textFieldName.getText() + " diz -> " + textMessage.getText() + "\r\n");
		}
		bufferReader.flush();
		textMessage.setText("");
	}

	public void listener() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"exit".equalsIgnoreCase(msg))

			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equalsIgnoreCase("exit"))
					textArea.append("Server is down! \r\n");
				else
					textArea.append(msg + "\r\n");
			}
	}

	public void quit() throws IOException {

		sendMessage("exit");
		bufferReader.close();
		writer.close();
		outputStream.close();
		socket.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals(buttonSEND.getActionCommand()))
				sendMessage(textMessage.getText());
			else if (e.getActionCommand().equals(buttonEXIT.getActionCommand()))
				quit();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				sendMessage(textMessage.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) throws IOException {

		Client app = new Client();
		app.conect();
		app.listener();
	}
}
