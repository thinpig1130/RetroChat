package retrochat.com.vo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import javafx.application.Platform;
import retrochat.com.server.ReceiveThread;
import retrochat.com.server.ServerControl;

public class Client {

	@Override
	public String toString() {
		return "[ " + portNum + " ] ' " + (nickName==null ? "닉네임미정 '" : nickName+" ' 님") ;
	}

	private String portNum;
	private String nickName = null;
	private SocketChannel socketChannel;
	
	
	public String getPortNum() {
		return portNum;
	}
	
	public void setPortNum(String portNum) {
		this.portNum = portNum;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
	
	public Client(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		receiveMsg();
	}
	
	void receiveMsg(){
		Runnable runnable = new ReceiveThread(this);
		ServerControl.executorService.submit(runnable);
	}
	
	public void sendMsg(String data) {
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Charset charset = Charset.forName("UTF-8");
					ByteBuffer byteBuffer = charset.encode(data);
					socketChannel.write(byteBuffer);
				} catch(Exception e) {
					try {
						String message = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
						Platform.runLater(()->ServerControl.serverCon.displayText(message));
						ServerControl.connections.remove(Client.this);
						socketChannel.close();
					} catch (IOException e2) {}
				}
			}
		};
		ServerControl.executorService.submit(runnable);
	}
	
	public SocketChannel getSoc() {
		return socketChannel;
	}
	
}
