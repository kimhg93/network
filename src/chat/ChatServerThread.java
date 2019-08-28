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
	private List<User> usersList;
	private BufferedReader br;
	private PrintWriter pw;
	User user = new User();
	
	public ChatServerThread(Socket socket, List<User> usersList) {
		this.socket = socket;
		this.usersList = usersList;
	}

	@Override
	public void run() {		
		try {			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			while (true) {
				String data = br.readLine();
				System.out.println(data);
				if(nickname!=null) {
					System.out.println(nickname+"> "+data);
				}
				if(data == null) {					
					break;
				}				
				
				String[] tokens = data.split("/");
				if(tokens[0].equals("join")) {
					doJoin(tokens[1], pw);
				} else if(tokens[0].equals("msg")) {
					doMessage(tokens[1]);					
				} else if(tokens[0].equals("quit")) {
					doQuit(user);
				} else if(tokens[0].equals("dm")) {					
					doDirectMsg(tokens[1], tokens[2]);
				}
				else {
					ChatServer.log("알수 없는 요청: "+tokens[0]);
				}				
			}
		} catch (SocketException e) {
			System.out.println("누군가 나갔다");
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
		pw.println("join/ok");
		broadcast(data);
		user.setName(nickname);
		user.setWriter(writer);		
		addWriter(writer, user);		
		
	}
	
	private void addWriter(Writer writer, User user) {
		synchronized(usersList) {
			usersList.add(user);
		}
	}
	
	private void broadcast(String sendmsg) {
		synchronized(usersList) {
			for(User user:usersList) {
				pw = (PrintWriter)user.getWriter();
				///System.out.println("broad>>"+sendmsg);
				pw.println(sendmsg);
			}
		}
	}
	
	private void doDirectMsg(String userName, String sendmsg) {
		for(User user:usersList) {
			if(user.getName().equals(userName)) {
				pw = (PrintWriter)user.getWriter();				
				pw.println(nickname+":"+sendmsg);
			}				
			if(user.getName().equals(nickname)) {
				pw = (PrintWriter)user.getWriter();				
				pw.println(nickname+":"+sendmsg);
			}	
		}
	}
	
	private void doMessage(String sendmsg){
		//System.out.println(nickname+">>"+sendmsg);
		broadcast(nickname+": "+sendmsg);
	}
	
	private void doQuit(User user) {
		//System.out.println("doquit");				
		String data = nickname + "님이 퇴장했음";		
		broadcast(data);
		removeWriter(user);
		
	}
	
	private void removeWriter(User user) {
		synchronized(usersList) {
			usersList.remove(user);
		}
	}

}