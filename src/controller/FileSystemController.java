package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.DirItem;
import model.FileLabel;
import model.FileSystem;

public class FileSystemController  implements Initializable{
	
	private Stage stage;

	private FileSystem fileSystem;
	
    @FXML
    private FlowPane flowPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		byte[] values = {'a',0,0,0,0,DirItem.FILE,0,0};
		try {
			FileLabel f1 = new FileLabel(new DirItem(values,"123"));
			flowPane.getChildren().add(f1);
			FileLabel f2 = new FileLabel(new DirItem(values,"123"));
			flowPane.getChildren().add(f2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public FlowPane getFlowPane() {
		return flowPane;
	}

	public void setFlowPane(FlowPane flowPane) {
		this.flowPane = flowPane;
	}

	public Stage getStage() {
		return this.stage;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
