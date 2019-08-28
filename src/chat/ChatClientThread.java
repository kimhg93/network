package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientThread extends Thread {
	private Socket socket;	
	public ChatClientThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				//System.out.println("클라이언트 스레드 호출!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				String data = br.readLine();				
				if(data.equals("join/ok")) {
					System.out.println("채팅에 접속 됨");
				} else {
					System.out.println(data);
				}
			}
		} catch(SocketException e) {
			System.out.println("소켓이 닫힘"+e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
