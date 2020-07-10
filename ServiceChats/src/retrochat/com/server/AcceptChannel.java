package retrochat.com.server;

import java.nio.channels.SocketChannel;
import javafx.application.Platform;
import retrochat.com.vo.Client;

public class AcceptChannel implements Runnable {

	@Override
	public void run() {
		while(true) {
			try {
				SocketChannel socketChannel = ServerControl.serverSocketChannel.accept();
				Client client = new Client(socketChannel);
				
				//클라이언트를 식별할 port번호 저장
				String portNum = socketChannel.getRemoteAddress().toString();
				portNum = portNum.substring(portNum.length()-5);
				client.setPortNum(portNum);

				ServerControl.connections.add(client);
								
				// socketChannel.getRemoteAddress() ->   /192.168.0.199:63279
				// Thread.currentThread().getName() ->   pool-2-thread-1
				//ServerControl.connections.size() -> 현재 접속한 클라이언트 수
				
				//client의 접속 message.
				Platform.runLater(()->ServerControl.serverCon.displayText(
						"[ IN  :  " + client.getPortNum() + " ] 현재 접속자 : " + ServerControl.connections.size() + "명 "));

			} catch (Exception e) {
				if( ServerControl.serverSocketChannel.isOpen()) { ServerControl.serverCon.stopServer(); }
				break;
			}
		}
	}
}
