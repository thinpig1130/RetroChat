package retrochat.com.vo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import javafx.application.Platform;
import retrochat.com.client.FXLisner;
import retrochat.com.thread.SendThread;
import retrochat.com.thread.ConnectionThread;

//Singleton Pattern과  Builder Pattern idea를 이용하여 구현
public class Client {

	private static Client instance;
	private String nickName;
	private SocketChannel socketChannel;
	private FXLisner screenConnecter;
	private int roomNum;
	
	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum; 
	}
	
	public int getRoomNum() {
		return this.roomNum ; 
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName; 
	} 
	
	public String getNickName() {
		return this.nickName;
	}
    private Client(){}
    
    public static synchronized Client getInstance() {
    	if(instance == null){
            instance = new Client();
            instance.createsocketChannel();
        }
    	return instance;
    }
    
    public static synchronized Client getInstance(FXLisner connecter){
        if(instance == null){
            instance = new Client();
        }
        
        instance.setScreenConnecter(connecter);
        
        if(instance.socketChannel == null || !instance.socketChannel.isOpen()) { 
        	instance.createsocketChannel();
        }
        
        return instance;
    }
    
    public void setScreenConnecter(FXLisner connecter) {
    	this.screenConnecter = connecter;
    }
    

    public void nickName(String nickName) {
    	this.nickName = nickName;
    };
    
    //소켓 생성
    private void createsocketChannel() {
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			socketChannel.connect(new InetSocketAddress("192.168.0.199", 5001));
			if(socketChannel.isOpen())
				getScreenConnecter().messageController("0000Retro Chat Server 접속완료.");
			receiveData();
		} catch (IOException e) {
			getScreenConnecter().messageController("0000서버가 응답하지 않습니다.");
		}
	}
    
    //소켓종료
    public void closeSocket() {
		if(socketChannel!=null && socketChannel.isOpen()) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    public static boolean isInstance(){
        if(instance == null){
            return false;
        }
        return true;
    }
    

    
    public SocketChannel getSocketChannel() {
    	return socketChannel;
    }
    
    public FXLisner getScreenConnecter() {
    	return screenConnecter;
    }

    //서버에 data를 전송.
	public void sendData(String data) {
		Thread sendThread = new SendThread(data);
		sendThread.start();
	}

	//서버에서 전송 받은 메시지 처리
	public void receiveData() {
		ConnectionThread reseiver = new ConnectionThread();
		reseiver.start();
	}
	
}
