package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ChatServerThread extends Thread {
	private String nickname;
	private Socket socket;
	private List<Writer> listWriters;
	private BufferedReader br;
	private PrintWriter pw;
	
	
	public ChatServerThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {		
		try {			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
					
			while (true) {
				String data = br.readLine();
				if(nickname!=null) {
					System.out.println(nickname+"> "+data);
				}
				if(data == null) {
					doQuit(pw);
					break;
				}				
				
				String[] tokens = data.split("/");
				if(tokens[0].equals("join")) {
					doJoin(tokens[1], pw);
				} else if(tokens[0].equals("msg")) {
					doMessage(tokens[1]);					
				} else if(tokens[0].equals("quit")) {
					doQuit(pw);
				} else {
					ChatServer.log("알수 없는 요청: "+tokens[0]);
				}				
			}
		} catch (SocketException e) {
			System.out.println("소켓이 ?"+e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private void doJoin(String nickname, Writer writer) {
		this.nickname = nickname;		
		String data = nickname +"님이 참여하였습니다.";
		System.out.println(nickname + "join");
		broadcast(data);
		addWriter(writer);		
	}
	
	private void addWriter(Writer writer) {
		synchronized(listWriters) {
			listWriters.add(writer);
		}
	}
	
	private void broadcast(String sendmsg) {
		synchronized(listWriters) {
			for(Writer writer:listWriters) {
				pw = (PrintWriter)writer;
				//System.out.println("broad>>"+sendmsg);
				pw.println(sendmsg);
			}
		}
	}
	
	private void doMessage(String sendmsg){
		//System.out.println(nickname+">>"+sendmsg);
		broadcast(nickname+": "+sendmsg);
	}
	
	private void doQuit(Writer writer) {
		//System.out.println("doquit");				
		String data = nickname + "님이 퇴장했음";		
		broadcast(data);
		removeWriter(writer);
		
	}
	
	private void removeWriter(Writer writer) {
		synchronized(listWriters) {
			listWriters.remove(writer);
		}
	}

}
