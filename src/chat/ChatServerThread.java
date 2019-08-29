package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ChatServerThread extends Thread {
	private String nickname;
	private Socket socket;
	private List<User> usersList;
	private BufferedReader br;
	private PrintWriter pw;
	private String roomname;
	private List<String> rooms;
	User user = new User();
	public ChatServerThread(Socket socket, List<User> usersList, List<String> rooms) {
		this.socket = socket;
		this.usersList = usersList;
		this.rooms = rooms;
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
				protocol(tokens);
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
	
	private void removeRoom() {
		
	}
	
	private void newRoom(String roomname) {
		//System.out.println("newRoom");
		user.setChatRoom(roomname);		
		addRoom(roomname);
		addWriter(user);		
	}
	
	private void goRoom(String roomname) {
		System.out.println("goRoom");
		user.setChatRoom(roomname);
		addWriter(user);		
		String data = "님이 참여하였습니다.";
		sendRoom(data);
		
	}
	
	private void sendRoom(String sendmsg) {
		System.out.println("현재참여 방 확인 >>>>"+this.roomname);
		for(User user:usersList) {
			if(user.getChatRoom().equals(roomname)) {
				pw = (PrintWriter)user.getWriter();				
				pw.println(nickname+":"+sendmsg);
			}	
		}
	}
	
	private void doJoin(String nickname, Writer writer) {		
		this.nickname = nickname;				
		String roomlist = "";
		System.out.println(nickname + "join");
		pw.println("join/ok");
		if(!rooms.isEmpty()) {
			for(String room : rooms) {
				roomlist = roomlist + "\'"+ room +"\'";
				System.out.println("현재 방"+room);
			} 
			System.out.println(roomlist);
			pw.println("현재 생성된 방"+roomlist+" 접속할 방을 선택하시오(방생성 -> nwroom/방이름, 방참여 -> goroom/방이름");
		} else {
			pw.println("현재 생성된 방이 없다. 접속할 방을 선택하시오(방생성 -> nwroom/방이름");			
		}
		user.setName(nickname);
		user.setWriter(writer);		
		
	}
	private void addRoom(String room) {
		synchronized(usersList) {
			rooms.add(roomname);
		}
	}	
	
	private void addWriter(User user) {
		synchronized(usersList) {			
			usersList.add(user);
		}
	}	
	
	private void doDirectMsg(String userName, String sendmsg) {
		for(User user:usersList) {
			if(user.getName().equals(userName)) {
				pw = (PrintWriter)user.getWriter();				
				pw.println(nickname+":"+sendmsg);
			}	
		}
	}
	
	private void doQuit(User user) {
		//System.out.println("doquit");		
		//String data = nickname + "님이 퇴장했음";
		String data = "님이 퇴장했음";		
		sendRoom(data);
		removeWriter(user);
		
	}
	
	private void removeWriter(User user) {
		synchronized(usersList) {
			usersList.remove(user);
		}
	}
	
	private void protocol(String tokens[]) {
		if(tokens[0].equals("join")) {
			doJoin(tokens[1], pw);
		} else if(tokens[0].equals("msg")) {
			sendRoom(tokens[1]);					
		} else if(tokens[0].equals("quit")) {
			doQuit(user);
		} else if(tokens[0].equals("dm")) {					
			doDirectMsg(tokens[1], tokens[2]);
		} else if(tokens[0].equals("nwroom")) {
			this.roomname = tokens[1];
			newRoom(tokens[1]);
		} else if(tokens[0].equals("goroom")) {
			this.roomname = tokens[1];
			goRoom(tokens[1]);
		} else {
			ChatServer.log("알수 없는 요청: "+tokens[0]);
		}	
	}
	
	
	
//	private void broadcast(String sendmsg) {
//		synchronized(usersList) {
//			for(User user:usersList) {
//				pw = (PrintWriter)user.getWriter();
//				///System.out.println("broad>>"+sendmsg);
//				pw.println(sendmsg);
//			}
//		}
//	}
	
	
//	private void doMessage(String sendmsg){
//		broadcast(nickname+": "+sendmsg);
//	}

}