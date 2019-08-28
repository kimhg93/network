package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int PORT = 9991;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<User> usersList = new ArrayList<User>();
		try {
			serverSocket = new ServerSocket();
			//String hostAddress = InetAddress.getLocalHost().getHostAddress();
			String hostAddress = "127.0.0.1";
			System.out.println(hostAddress);
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));

			while (true) {
				Socket socket = serverSocket.accept();
				new ChatServerThread(socket, usersList).start();
			}			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String str) {
		System.out.println("[chat sever] " + str);
	}
}
