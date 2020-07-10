package retrochat.com.thread;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.stage.Stage;
import retrochat.com.vo.Client;

public class ConnectionThread extends Thread{

	@Override
	public void run() {
		Client client = Client.getInstance();
	
		while(true) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
				int readByteCount = client.getSocketChannel().read(byteBuffer); //서버가 비정상적으로 종료했을 경우 IOException 발생됨.
				
				//서버가 정상적으로 Socket의 close()를 호출했을 경우
				if(readByteCount == -1) {
					Platform.runLater(()->client.getScreenConnecter().messageController("0000 서버가 응답하지 않습니다."));
					client.closeSocket();
					break;
				}
									
				//받은 데이터를 문자열로 변환
				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
				
				//받은 메시지에 따라 응답을 처리해주는  메소드 호출
				//msgControl(data);
//				System.out.println(data + "client");
				Platform.runLater(()->client.getScreenConnecter().messageController(data));
			
			} catch (Exception e) {
				client.closeSocket();
	//			e.printStackTrace();
	//			System.out.println("ClientIndex Exception 0096");
				break;
			}
		}
	}

}
