package retrochat.com.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import retrochat.com.vo.Client;
import retrochat.com.vo.Room;

public class ClientIndexControl extends FXLisner  implements Initializable{
	
	Client client;

	@FXML
	public HBox hboxMain;	
	@FXML
    private Label statusLabel;
    @FXML
    private TextArea txtaRoomInfo;
    @FXML
    private TextField txtRoomName;
    @FXML
    private TextField txtNickName;
    @FXML
    private Button btnMakeRoom;
    @FXML
    private Button btnStartInfo;
    @FXML
    private ListView<Room> listRoom;
    @FXML
    private TextArea txtChatRoomInfo;
    
    @FXML
    private List<Room> roomList = new Vector<Room>(); 

    @FXML
    void actInRoom(MouseEvent event) {
    	int selectedRoomIndex = listRoom.getSelectionModel().getSelectedIndex();
    	if(selectedRoomIndex != -1) {
    	Room selectedRoom = listRoom.getItems().get(selectedRoomIndex);
	    	if(event.getClickCount() > 1) {
	    		requestEnteringTheRoom(selectedRoom.getId());
	    	}else{
	    		String info = "----------------- [ NO." + selectedRoom.getId() + " ] -----------------\n" +
	    				"\t\t\t[ 방명 ] " + selectedRoom.getName() + " \n" + 
	    				"\t\t\t[ 참여 인원 ] " + selectedRoom.getCnt() + "명 \n" +
	    				"------------------------------------------\n" +
	    				selectedRoom.getInfo() + "\n" +
	    				"------------------------------------------";
	    		txtChatRoomInfo.setText(info);
	    	}
    	}
    }
    
    // 방만들기 폼에 '들어가기' 버튼을 눌렀을때.
    @FXML
    void requestMakeRoom(ActionEvent event) {

    	String roomName = txtRoomName.getText();
    	String roomInfo = txtaRoomInfo.getText();
    	String nickName = txtNickName.getText();
    	
    	String data = "3200" + nickName + "/" + roomName + "/" + roomInfo; 
    	client.sendData(data);
 
    }
    
    void requestEnteringTheRoom(int roomNo) {
    	if(txtNickName.getText().trim().equals("")) {
    		JOptionPane.showMessageDialog(null, "대화명을 입력해 주세요.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
    	}else{  	
	    		String data = "3300" + roomNo + "$+|*" + txtNickName.getText().trim();
	    		client.sendData(data);
    	}
    }
    
	void requestListInfo() {
		client.sendData("3100");
	}


    @FXML
    void actUpdateInfo(ActionEvent event) {
    	if(!statusLabel.getText().equals("Retro Chat Server 접속완료.")) {
	    	Platform.runLater(()->messageController("채팅 서버 접속 중..."));
	    	startClient();
    	}else {
    		client.sendData("메시지 전송 Test");
    	}
    }
    
	void startClient() {
		client = Client.getInstance(this);
		Stage stage = (Stage) statusLabel.getScene().getWindow();
		stage.setOnCloseRequest(event->stopClient());
		btnStartInfo.setText("UPDATE INFO");
		
		if(statusLabel.getText().equals("Retro Chat Server 접속완료.")){
			requestListInfo();
		}
	}
	
	void stopClient() {
		client.closeSocket();
		System.out.println("stopClient");
	}

	ClientIndexControl getController() {
		return this;
	}
	
	void receive() {
		client.receiveData();
	}
	
	@Override
	public void messageController(String data) {
		System.out.println("ClientIndex에 들어온 정보 : " + data);
		//요청번호와 데이터 분리
		String dataCommand = data.substring(0,4);
		String nextData = data.substring(4);
		
		/*
		 * '0000' 상태메시지 변경 (내부통신)
		 * '3001' 채팅방정보요청/수신
		 * '3002' 채팅방개설
		 * '3003' 채팅방입장
		 */
		
		if(dataCommand.equals("0000")){
			statusLabel.setText(nextData);
		}else if(dataCommand.equals("2001")){
			//채팅방 입장 명령
		}else if(dataCommand.equals("3110")) {
			setRoomList(nextData);
		}else if(dataCommand.equals("3310")){
			loadChatScreen(nextData);
		}else if(dataCommand.equals("3320")){
			//채팅방 입장 거절 메시지 처리
		}else {
//			System.out.println(data);
		}
		
	}
	
	void setRoomList(String data) {
		listRoom.getItems().removeAll(listRoom.getItems());
		if(data.equals("")||data == null) {
			txtChatRoomInfo.setText("개설된 방이 없습니다.");
		}else {
			String[] arr = data.split("[$][+][|][*]");
			System.out.println(arr[0]);
						
			for(String room : arr) {
				String[] str = room.split("/",4);
				Room addRoom = new Room(Integer.parseInt(str[0]), Integer.parseInt(str[1]), str[2], str[3]);
				listRoom.getItems().add(addRoom);
			};		
		}	
	}

	void loadChatScreen(String data) {
		//채팅방 로드하고 controller 바꾸기.
    	try {
    		Stage stage = (Stage) btnMakeRoom.getScene().getWindow();
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/WindowForChat.fxml"));
    		HBox chatWindow = (HBox) loader.load();
			Scene scene = new Scene(chatWindow);
			stage.setScene(scene);
			
			ChatRoomControl control = loader.getController();
			client.setScreenConnecter(control);
			
			control.setInfo(data);
			
			stage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    	//로드한 채팅방에 정보를 셋팅해 준다.
		System.out.println(data);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//statusLabel.setText("ㅇㅇㅇㅇㅇㅇㅇ");
		//startClient();
		listRoom.setItems(FXCollections.observableArrayList());
		listRoom.getItems().addAll(roomList);
		statusLabel.setText("Retro Chat에 오신 것을 환영합니다.");
		btnStartInfo.setText("START");
		
		
	}


}
