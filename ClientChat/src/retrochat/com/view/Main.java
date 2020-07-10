package retrochat.com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Scene scene;
			BorderPane startScreen = (BorderPane) FXMLLoader.load(getClass().getResource("ClientScreen.fxml"));
			scene = new Scene(startScreen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Welcome to Retro Chat");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
