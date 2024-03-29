package application;

import java.io.IOException;

import controller.FileSystemController;
import controller.EditorController;
import controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application{
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			//准备好所有窗口
			createStage("/view/FileSystem.fxml", "controller.FileSystemController", true);
			createStage("/view/Editor.fxml", "controller.EditorController", false);
			createStage("/view/Notepad.fxml", "controller.NotepadController", false);
			((EditorController) RootController.controllers.get("controller.EditorController")).getStage().setResizable(false);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createStage(String fxmlPath, String controllerName, boolean isShow) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource(fxmlPath));
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("InternalExplorer");
			stage.getIcons().add(new Image("/view/filesystem_icon.png"));
			RootController.controllers.get(controllerName).setStage(stage);
			if(isShow) {
				((FileSystemController)RootController.controllers.get(controllerName)).initCloseEventHandler();
				stage.show();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
