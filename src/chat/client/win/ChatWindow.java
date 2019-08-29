package chat.client.win;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class ChatWindow extends Thread {
	
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private Socket socket;
	private PrintWriter pw;
	String name = null;
	Scanner sc = null;
	
	public ChatWindow(String name, Socket socket) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.name = name;
		this.socket = socket;		
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void run() {
		while(true) {			
			try {
				System.out.println("스레드");
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				String data = br.readLine();
				updateTestArea(data);
				System.out.println("?"+data);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}	
	}
	
	public void show() {
		// Button
		System.out.println();
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				char keyCode = event.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}			
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pw.println("quit/");
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.pack();	
		this.run();
	}
	
	private void updateTestArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}
	
	private void sendMessage() {
		System.out.println(socket.getRemoteSocketAddress());
		String message = textField.getText();
		if(message==null||message=="") {
			message+=" ";
		}
		String[] tokens = message.split("/");
		if(tokens[0].equals("dm")) {
			pw.println("dm/"+tokens[1]+"/"+tokens[2]+" ");
		} else {
			pw.println("msg/"+message+" ");
		}
		textField.setText("");
		textField.requestFocus();
	
	}
}
