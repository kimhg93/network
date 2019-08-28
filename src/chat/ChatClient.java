package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
	private static final int SERVER_PORT = 9994;
	private static final String SERVER_IP = "127.0.0.1";

	public static void main(String[] args) {
		Scanner sc = null;
		Socket socket = null;
		try {
			sc = new Scanner(System.in);
			socket = new Socket();
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			
			System.out.print("닉네임 입력 >>");
			String nickname = sc.nextLine();
			 pw.println("join/" + nickname);
			 if(br.readLine().equals("join/ok")) {	
				 new ChatClientThread(socket).start(); 
				 log("Connected");
			 }
			 
			while (true) {
				System.out.print(">");
				String input = sc.nextLine();

				if (input.equals("quit")) {
					pw.println("quit/");
					break;
				} else {
					pw.println("msg/" + input);
				}				
				pw.flush();
			}
		} catch (SocketException e) {
			System.out.println("소켓에러 "+e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (sc != null) {
					sc.close();
				}
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String str) {
		System.out.println(str);
	}
}
