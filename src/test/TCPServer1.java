package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer1 {
	private static final int PORT = 5555;
	public static void main(String[] args) {
		// 1. 서버소켓 생성
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
			
			//2. Binding: Socket에 SocketAddress(IPAddress + Port)를 바인딩 한다.
			InetAddress inetAddress = InetAddress.getLocalHost();			
			InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress,PORT);
			serverSocket.bind(inetSocketAddress);
			System.out.println("[TCPServer] binding "+inetAddress.getHostAddress()+":"+PORT);
			
			//3. accept: 클라이언트로 부터 연결요청(connect)을 기다린다.
			Socket socket = serverSocket.accept(); //Blocking
			InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
			int remoteHostPort = inetRemoteSocketAddress.getPort();
			System.out.println("[TCPServer] connected form client "+remoteHostAddress+":"+remoteHostPort);
			
			try {
			//4. IOStream 받아오기
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				
				while(true) {
					//5. 데이터 읽기
					byte[] buffer = new byte[256];
					int readByteCount = is.read(buffer); //Blocking					
					if(readByteCount==-1) {
						// 정상종료 : remote socket이 close() 메소드를 호출해서 정상적으로 소켓을 닫은경우
						System.out.println("[TCPServer] closed by client");
						break;
					}					
					String data = new String(buffer, 0, readByteCount, "utf-8");
					System.out.println("[TCPServer] received > "+data);
					
					//6. 데이터 쓰기
					os.write(data.getBytes("utf-8"));
				}				
			} catch(SocketException e) {
				System.out.println("[TCPServer] abnormal closed by client");
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				// 7. socket 자원정리
				if(socket!=null&&socket.isClosed()==false) {
					socket.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//8. server socket 자원정리
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
