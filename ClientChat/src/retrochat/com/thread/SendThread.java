package retrochat.com.thread;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import retrochat.com.vo.Client;

public class SendThread extends Thread{
	String data;
	
	public SendThread(String data){
		this.data = data;
	}
	
//	public void setData(String data) {
//		this.data = data;
//	}
	
	@Override
	public void run() {
		Client client = Client.getInstance();
		try {			
			Charset charset = Charset.forName("UTF-8");
			ByteBuffer byteBuffer = charset.encode(data);
			client.getSocketChannel().write(byteBuffer);
//				socketChannel.write(byteBuffer);
//				Platform.runLater(()->displayStatus("[보내기 완료]"));
		} catch(Exception e) {
//				Platform.runLater(()->displayStatus("[서버 통신 안됨]"));
			client.closeSocket();
		}				
	}
}
