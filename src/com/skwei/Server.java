package com.skwei;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * @author Wei
 *
 */
public class Server {
	private boolean serverOn = true;
	private HashMap<String, ClientThread> clientMaps = new HashMap<>();
	private ServerSocket ss = null;
	
	public static final int DEFAULT_PORT = 11288;
	
	private Server(){
		
	}
	
	public void startServer(){
		new ServerThread().start();
		new UserInputThread().start();
	}
	
	private class ServerThread extends Thread {
		@Override
		public void run() {
			try{
				ss = new ServerSocket(DEFAULT_PORT);
				System.out.println("Server started at " + InetAddress.getLocalHost().getHostAddress() + "...");
				while (serverOn) {
					System.out.println("Waiting users...");
					Socket client = ss.accept();
					new ClientThread(client).start();
					
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				serverOn = false;
				if(ss != null){
					try {
						ss.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
	
	private class UserInputThread extends Thread {
		@Override
		public void run() {
			while(serverOn){
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);
				String line;
				try {
					String[] commands;
					while ((line = br.readLine()) != null) {
						commands = line.split(" ");
						if (commands != null) {
							if(commands.length == 1){
								if (commands[0].equalsIgnoreCase("exit")){
									serverOn = false;
									if(ss != null){
										ss.close();
									}
									System.exit(0);
								}
							} 
						}
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ClientThread extends Thread {
		private Socket client;
		private boolean clientOn = true;
		private ObjectOutputStream oos = null;
		private String clientName;
		
		private ClientThread(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(client.getInputStream());
				oos = new ObjectOutputStream(client.getOutputStream());
				
				while(clientOn){
					Object obj = ois.readObject();
					if(obj instanceof RegMsgObject){
						RegMsgObject o = (RegMsgObject) obj;
						String name = o.getMessage();
						//log in
						if(o.getMsgType() == RegMsgObject.MSGTYPE_REGISTER){
							RegMsgObject respond = new RegMsgObject();
							synchronized (clientMaps) {
								// if the userName name already exists
								if (clientMaps.containsKey(name)) {
									respond.setMsgType(RegMsgObject.MSGTYPE_USERNAME_OCCUPIED);
									clientOn = false;
									oos.writeObject(respond);
									System.out.println("Sorry. Nickname [" + name + "] has already been used.");
								} else {
									respond.setMsgType(RegMsgObject.MSGTYPE_REGISTER_SUCCEED);
									oos.writeObject(respond);
									
									clientMaps.put(name, ClientThread.this);
									
									System.out.println("User [" + name + "] logged in.");
									
									SysMsgObject sysMsg = new SysMsgObject();
									sysMsg.setMessage("[system] User \"" + name + "\" joins chat.");
									broadcast(sysMsg);
									
									broadcastUserListChange();
									clientName = name;
								}
							}
							
						} 
						//log out
						else if(o.getMsgType() == RegMsgObject.MSGTYPE_EXIT){
							userExits();
						}
					} else if(obj instanceof DrawingMsgObject) {
						DrawingMsgObject msg = (DrawingMsgObject) obj;
						msg.setFromWhom(clientName);
						msg.setTimeStamp(System.currentTimeMillis());
						String privateTo = msg.getToWhom();
						if(privateTo == null || privateTo.equals("")){
							broadcast(msg);
						} else {
							sendPrivateMsg(msg);
							send(msg);//send to the sender itself since the msg is also needed to be listed in sender's chat content
						}
					} else if(obj instanceof MsgObject){
						MsgObject msg = (MsgObject) obj;
						msg.setFromWhom(clientName);
						msg.setTimeStamp(System.currentTimeMillis());
						
						String privateTo = msg.getToWhom();
						if(privateTo == null || privateTo.equals("")){
							broadcast(msg);
						} else {
							sendPrivateMsg(msg);
							send(msg);//send to the sender itself since the msg is also needed to be listed in sender's chat content
						}
					}
						
				}
				
			} catch(SocketException e){
				userExits();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				clientOn = false;
				if(client != null){
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		public void send(DataObject msg) throws IOException {
			oos.writeObject(msg);
		}
		
		private void userExits(){
			clientOn = false;
			String name = clientName;
			try {
				synchronized (clientMaps) {
					clientMaps.remove(name);
					broadcastUserListChange();
				}
				SysMsgObject sysMsg = new SysMsgObject();
				sysMsg.setMessage("[system] User \"" + name + "\" exits.");
				broadcast(sysMsg);
				System.out.println("User [" + name + "] logged out.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void broadcastUserListChange() throws IOException{
		ListMsgObject list = new ListMsgObject();
		ArrayList<String> userList = new ArrayList<>(clientMaps.keySet());
		Collections.sort(userList);
		list.setUserList(userList);
		
		broadcast(list);
	}
	
	private void broadcast(DataObject msg) throws IOException{
		for(ClientThread ct : clientMaps.values()){
			ct.send(msg);
		};
	}
	
	private void sendPrivateMsg(MsgObject msg) throws IOException{
		ClientThread clientPrivateTo = clientMaps.get(msg.getToWhom());
		clientPrivateTo.send(msg);
	}
	
	public static void initServer() {
		Server server = new Server();
		server.startServer();
	}

	public static void main(String[] args){
		initServer();
	}
	
}
