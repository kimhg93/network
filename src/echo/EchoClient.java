package echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	private static String SERVER_IP = "192.168.1.31";
	private static final int SERVER_PORT = 8000;
	public static void main(String[] args) {
		Socket socket = null;
		Scanner sc = new Scanner(System.in);
		try {
			//1. 소켓 생성
			socket = new Socket();
			
			//2. 서버 연결
			InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);		
			socket.connect(inetSocketAddress);			
			System.out.println("[TCPClient] connected");
			
			//3. IOStream 받아오기
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//4. 쓰기
			
			
			//5. 읽기
			while(true) {								
				System.out.print("[TCPClient] toServer > ");
				String data = sc.next();
				os.write(data.getBytes("utf-8"));				
				
				byte[] buffer = new byte[256];
				int readByteCount = is.read(buffer); //Blocking			
				
				if((readByteCount==-1) || data.equals("exit")) {
					// 정상종료 : remote socket이 close() 메소드를 호출해서 정상적으로 소켓을 닫은경우
					System.out.println("[TCPClient] closed by client");
					break;
				}	
				
				data = new String(buffer, 0, readByteCount, "utf-8");
				System.out.println("[TCPClient] fromServer > "+data);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sc.close();
				if(socket!=null&socket.isClosed()==false) {
					socket.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
