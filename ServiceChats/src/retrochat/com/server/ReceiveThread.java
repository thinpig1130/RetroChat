package retrochat.com.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Set;

import javafx.application.Platform;
import retrochat.com.vo.ChatRoom;
import retrochat.com.vo.Client;

public class ReceiveThread implements Runnable{
	Client client;
	
	public ReceiveThread(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while(true) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(100);
				
				int readByteCount = client.getSocketChannel().read(byteBuffer);
				
				//클라이언트가 접속을 종료 했을 경우.
				if(readByteCount == -1) { 
					ServerControl.connections.remove(client);
					Platform.runLater(()->ServerControl.serverCon.displayText(
							"[ OUT :  " + client.getPortNum() + " ] 현재 접속자 : " + ServerControl.connections.size() + "명 "));
					client.getSocketChannel().close();
				}
				
				//전송된 데이터를 받음.
				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
				
				//명령어 식별
				dataControl(data);
				
//				Platform.runLater(()->ServerControl.serverCon.displayText("[ MSG ] FROM"));
//				String message = "[요청 처리: " + client.getSocketChannel().getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
//				for(Client someclient : ServerControl.connections) {
//					someclient.sendMsg(data); 
//				}
				
			} catch(Exception e) {
				try {
					ServerControl.connections.remove(client);
					String message = "[클라이언트 통신 종료: " + client.getSocketChannel().getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
						Platform.runLater(()->ServerControl.serverCon.displayText(message));
						client.getSocketChannel().close();
					} catch (IOException e2) {}
					break;
				}
			}
		}
	
	// 전송된 데이터를 용도에 맞게 분리.
	void dataControl(String data){
		/*
		 * '3100' : 채팅방 정보 요청 
		 * '3200' : 채팅방 개설 요청
		 * '3300' : 채팅방 입장 요청-
		 */
		
		System.out.println("dataControl : " + data );
		String dataCommand = data.substring(0,4);
		String nextData = data.substring(4);
		if(dataCommand.equals("3100")) {
			sendRoomList();
		}else if(dataCommand.equals("3200")){
			createRoom(nextData);
		}else if(dataCommand.equals("3300")){
			enterTheRoom(nextData);
		}else if(dataCommand.equals("3400")){
			goOutTheRoom(nextData);
		}else {
			Platform.runLater(()->ServerControl.serverCon.displayText(
					"error : 비정상적인 접속 !!!!!!!!!!!!!!! \n"+ data ));
		}
	}
	
	void sendRoomList(){
		String data = "3110";
		
		Set<Integer> keys = ServerControl.rooms.keySet();

		for (Integer key : keys) {
			ChatRoom room = ServerControl.rooms.get(key);
			data += room.getId() + "/" + room.getCountClient() + "/"
					+ room.getName() + "/" + room.getInfo() + "$+|*";
		}

//		for(ChatRoom room : ServerControl.rooms) {
//			data += room.getId() + "/" + room.getCountClient() + "/"
//					+ room.getName() + "/" + room.getInfo() + "$+|*";
//		}
		
		client.sendMsg(data);
	}
	
	void createRoom(String data){

		String[] roomData = data.split("/", 3);
		client.setNickName(roomData[0]);
		ChatRoom room = new ChatRoom(ServerControl.nextRoomNum++, roomData[1], roomData[2], client);
		ServerControl.rooms.put(room.getId(), room);

		Platform.runLater(()->ServerControl.serverCon.displayText(
				"[ CREATE ROOM  : " + room.getId() + " ]  개설자 : " + client.getPortNum() +
				" / " + client.getNickName() ));
		
		//개설 후 방입장 메시지 보냄.
		sendEnterRoomMsg(room);
	}

	void enterTheRoom(String data) {

		String[] dataArr= data.split("[$][+][|][*]");
		//입력받은 닉네임 설정
		
		client.setNickName(dataArr[1]);
		
		int roomNum = Integer.parseInt(dataArr[0]);
		ChatRoom selectedRoom = ServerControl.rooms.get(roomNum);
		
		selectedRoom.enterRoom(client);
		
		if(selectedRoom == null) {
			String sendData ="3320" + roomNum +"번방에 입장 할 수 없습니다.(방이 존재하지 않습니다.)";
			client.sendMsg(sendData);
		}else {
			//채팅방 입장 수락 내용 보냄.
			sendEnterRoomMsg(selectedRoom);
			//채팅방에 있는 사람들에게 입장 메시지 보냄.
		}		
	}
	void goOutTheRoom(String data) {
		String[] dataArr= data.split("[$][+][|][*]");
		
		int roomNum = Integer.parseInt(dataArr[0]);
		ChatRoom selectedRoom = ServerControl.rooms.get(roomNum);
		
		selectedRoom.goOut(dataArr[1]);
		
		//방에 아무도 없으면 방도 지워줌
		if(selectedRoom.getCountClient()==0) ServerControl.rooms.remove(selectedRoom.getId());		
				
	}
	void sendEnterRoomMsg(ChatRoom room) {
		String sendData = "3310" + client.getNickName() + "$+|*";
		sendData += room.getRoomData();
		client.sendMsg(sendData);
	}
	
	void sendOutMsg(ChatRoom room) {
		String sendData = "3312" + client.getNickName() + "$+|*";
		sendData += room.getRoomData();
		client.sendMsg(sendData);
	}
	

}
