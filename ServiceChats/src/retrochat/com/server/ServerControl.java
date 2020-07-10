package retrochat.com.server;

import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import retrochat.com.vo.ChatRoom;
import retrochat.com.vo.Client;

public class ServerControl implements Initializable{
	public static ExecutorService executorService;
	public static ServerSocketChannel serverSocketChannel;
	public static ServerControl serverCon;
	public static List<Client> connections = new Vector<Client>(); //Vector 동기화가 지원되는 자료구조.
	public static Map<Integer, ChatRoom> rooms = new HashMap<>();
//	public static List<ChatRoom> rooms = new Vector<ChatRoom>();
	public static int nextRoomNum = 1;
	
    @FXML
    private ListView<Object> listInfo;
	@FXML
    private TextArea textaServerWindow;
	@FXML
    private Button btnServerCon;
    @FXML
    private Button btnRoomList;
    @FXML
    private Button btnUserList;

    @FXML
    void actViewRoomList(ActionEvent event) {
    	listInfo.getItems().removeAll(listInfo.getItems());
    	if(btnServerCon.getText().equals("STOP")) {
    		if(rooms.size() == 0) {
    			listInfo.getItems().add("개설된 방 목록이 없습니다.");
    		}else {
   				rooms.forEach((key, room) -> listInfo.getItems().add(room));
    		}    		
    	} else {
    		reInitialize();
    	}
    }

    @FXML
    void actViewUserList(ActionEvent event) {

    	listInfo.getItems().removeAll(listInfo.getItems());
    	if(btnServerCon.getText().equals("STOP")) {  	
	    	if(connections.size() == 0) {
	    		listInfo.getItems().add("접속한 사람이 없습니다.");
	    	}else {
	    		listInfo.getItems().addAll(connections);
	    	}
    	}else {
    		reInitialize();
    	}
    }

    //SARTE (STOP) 버튼을 눌렀을 때
    @FXML
    void ActServerCon(ActionEvent event) {
    	if(btnServerCon.getText().equals("START")) {
    		startServer();
    		displayText("[서버 구동 중...]");
    		btnServerCon.setText("STOP");
    	}else {
    		stopServer();
    		displayText("[서버 실행 종료]");
    		btnServerCon.setText("START");
    	}
    }	
    
    //모니터링 창에 MSG를 출력하는 메소드
	public void displayText(String text) {
		textaServerWindow.appendText(text + "\n");
	}

	//서버 실행
	void startServer() {
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors() //CUP core 개수 반환
		    );
			
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);		
			serverSocketChannel.bind(new InetSocketAddress(5001));
		} catch(Exception e) {
			if(serverSocketChannel.isOpen()) { stopServer(); }
			return;
		}
		
		Runnable runnable = new AcceptChannel();
		executorService.submit(runnable);
		
		//화면 종료시 서버도 같이 멈추도록 설정.
		Stage stage = (Stage) btnServerCon.getScene().getWindow();
		stage.setOnCloseRequest(event->stopServer());
	}
	
	//서버 종료
	void stopServer() {
		try {
			Iterator<Client> iterator = connections.iterator();
			while(iterator.hasNext()) {
				Client client = iterator.next();
				client.getSoc().close();
				iterator.remove();
			}
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()) { 
				serverSocketChannel.close(); 
			}
			if(executorService!=null && !executorService.isShutdown()) { 
				executorService.shutdown(); 
			}		
		} catch (Exception e) {}
	}
	
	void reInitialize() {
		listInfo.getItems().add("서버 미실행 / 정보 없음");
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listInfo.setItems(FXCollections.observableArrayList());
		reInitialize();
		serverCon = this;
		textaServerWindow.setEditable(false);
	}
}
