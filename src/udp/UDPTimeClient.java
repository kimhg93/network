package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class UDPTimeClient {
	private static final String SERVER_IP = "192.168.1.31";
	public static void main(String[] args) {
		Scanner sc = null;
		DatagramSocket socket = null;
		try {
			//1. 키보드 연결
			sc = new Scanner(System.in);
			
			//2. socket 생성
			socket = new DatagramSocket();
			
			//3. 사용자 입력을 받음
			while(true) {
				System.out.print(">> ");
				String message = sc.nextLine();
				
				if("quit".equals(message)) {
					break;
				}
				//4. 메시지 전송
				byte[] sendData = new byte[0];
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, new InetSocketAddress(SERVER_IP,UDPTimeServer.PORT));
				socket.send(sendPacket);
				
				//5. 메시지 수신
				DatagramPacket receivePacket = new DatagramPacket(new byte[UDPTimeServer.BUFFER_SIZE], UDPTimeServer.BUFFER_SIZE);
				socket.receive(receivePacket);
				
				byte[] data = receivePacket.getData();
				int length = receivePacket.getLength();
				message = new String(data, 0, length, "UTF-8");
				System.out.println(message);			
				
			}
			
		} catch(IOException e ) {
			e.printStackTrace();
		} finally {
			if(sc!=null) {
				sc.close();
			}
			if(socket!=null&&socket.isClosed()==false) {
				socket.close();
			}
		}
	}

}
