package chat.client.win;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static final int SERVER_PORT = 9991;
	private static final String SERVER_IP = "127.0.0.1";

	public static void main(String[] args) {
		String name = null;
		Scanner sc = new Scanner(System.in);
		Socket socket = null;		
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			System.out.println("Connected");
			
			while (true) {

				System.out.println("대화명을 입력하세요.");
				System.out.print(">>> ");
				name = sc.nextLine();

				if (name.isEmpty() == false) {
					pw.println("join/"+name);
					break;
				}

				System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
			}
			 if(br.readLine().equals("join/ok")) {	
				 log("Connected");
				 new ChatWindow(name, socket).show();			 
			 }
			
			
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
