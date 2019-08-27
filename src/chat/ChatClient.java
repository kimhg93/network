package chat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
	private static final int SERVER_PORT = 9993;
	private static final String SERVER_IP = "192.168.56.1";

	public static void main(String[] args) {
		Scanner sc = null;
		Socket socket = null;
		try {
			sc = new Scanner(System.in);
			socket = new Socket();
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			log("Connected");

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			System.out.print("닉네임 입력 >>");
			String nickname = sc.nextLine();
			pw.println("join/" + nickname);	
			System.out.println("join 실행");
			 
			new ChatClientThread(socket).start();
			while (true) {

				// 5. 키보드 입력 받기
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
			System.out.println("소켓에러");
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
