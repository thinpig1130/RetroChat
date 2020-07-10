package retrochat.com.vo;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
	
	@Override
	public String toString() {
		return "[ " + id + "번 ] " + name + " ( 접속 "+ getCountClient() +"명 )";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	public  Map<String, Client> getIncomingClient() {
		return incomingClient;
	}

	private int id;
	private String name;
	private String info;
//	private List<Client> incomingClient = new Vector<Client>();
	public Map<String, Client> incomingClient = new HashMap<>();
	
	public ChatRoom(int id, String name, String info, Client client) {
		this.id = id;
		this.name = name;
		this.info = info;
		incomingClient.put(client.getNickName(), client);
	}
	
	public void sendMsg(String msg, Client client) {
		// 자신을 제외한 방 접속자에게 msg 전송 
	}
	
	public int getCountClient() {
		return incomingClient.size();
	}
	
	//클라이언트에게 보낼 방의 모든 정보를 보냄
	public String getRoomData() {
		//방번호/인원/방이름/정보/대화명들...
		//대구분자 $+l*
		String data = id + "$+|*" + incomingClient.size() + "$+|*" + 
						name + "$+|*" +	info + "$+|*" ;
		for(String key : incomingClient.keySet()) {
			data += key + "/"; 
		}
		
		return data;
	}
	
	public void enterRoom(Client client) {
		for(String key : incomingClient.keySet()) {
			incomingClient.get(key).sendMsg("3311"+client.getNickName());
		}
		
		incomingClient.put(client.getNickName(), client);
	}

	public void goOut(String nickName) {
		incomingClient.remove(nickName);
		for(String key : incomingClient.keySet()) {
			incomingClient.get(key).sendMsg("3312"+ nickName);
		}
	}

}
