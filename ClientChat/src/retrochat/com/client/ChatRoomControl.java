package retrochat.com.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import retrochat.com.vo.Client;

public class ChatRoomControl extends FXLisner implements Initializable{
	
	Client client;
	
	@FXML
	public HBox hboxMain;
	@FXML
    private Button btnOutRoom;
    @FXML
    private Button btnSendMsg;

    @FXML
    private TextField txtSendMsg;
    @FXML
    private Label labRoomInfo;
    @FXML
    private Label labNickname;
    @FXML
    private Label labRoomStatus;
    @FXML
    private ListView<String> listMsg;
    @FXML
    private ListView<String> listNick;
    
    //채팅방 정보를 셋팅시킴
    public void setInfo(String data) {
    	/*
    	 * 0 : 대화명
    	 * 1 : 방번호
    	 * 2 : 접속자 인원
    	 * 3 : 방이름
    	 * 4 : 방정보
    	 * 5 : 접속한 사람들의 대화명
    	 */
    	String[] dataArr= data.split("[$][+][|][*]");
    	
    	client.setRoomNum(Integer.parseInt(dataArr[1]));
    	client.setNickName(dataArr[0]);
    	
    	labNickname.setText(dataArr[0]+"(나)");
    	labRoomStatus.setText("Welcome to the '" + dataArr[3] + "'  [" + dataArr[1] +"번방]");
    	labRoomInfo.setText("  " + dataArr[4]);
    	
    	String[] nickArr = dataArr[5].split("/");
    	for (String nick : nickArr) {
    		listNick.getItems().add(nick);
    	}
    	
    	listMsg.getItems().add( dataArr[0] + "'님 채팅방 접속.");
    }

    @FXML
    void actSendMsgEnter(ActionEvent event) {
    	actSendMsg(event);
    }

    @FXML
    void actSendMsg(ActionEvent event) {
    	//메시지 보내기 눌렀을때.
    }

    @FXML
    void actOutRoom(ActionEvent event) {

    	client.sendData("3400" + client.getRoomNum() + "$+|*" + client.getNickName());

    	Stage stage = (Stage) btnOutRoom.getScene().getWindow();
    	BorderPane index;
		try {
			index = (BorderPane) FXMLLoader.load(getClass().getResource("../view/ClientScreen.fxml"));
			Scene scene = new Scene(index);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			System.out.println("ClientScreen 가져오던 중 오류");
			e.printStackTrace();
		}
    }
    
    ChatRoomControl getController() {
		return this;
	}

	@Override
	public void messageController(String data) {
		System.out.println("Chat messageController:" + data);
		//요청번호와 데이터 분리
		String dataCommand = data.substring(0,4);
		String nextData = data.substring(4);
		if(dataCommand.equals("3311")) {
			someoneEnter(nextData);
		} else if(dataCommand.equals("3312")) {
			someoneOut(nextData);
		}
		
	}
	
	void someoneEnter(String data) {		
		listNick.getItems().add(data);
		listMsg.getItems().add("'"+data + "'님이 입장하셨습니다.");
	}
	
	void someoneOut(String data) {
		listNick.getItems().remove(data);
		listMsg.getItems().add("'"+data + "'님이 퇴실 하셨습니다.");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		client = Client.getInstance();
		
	}

}
