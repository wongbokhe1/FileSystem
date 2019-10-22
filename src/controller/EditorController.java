package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditorController extends RootController {
	
	private Stage editStage;

    @FXML
    private TextField fileNameTextField;

    @FXML
    private TextField fileTypeTextField;

    @FXML
    private CheckBox regularFileCheckBox;

    @FXML
    private CheckBox systemFileCheckBox;

    @FXML
    private CheckBox readOnlyFileCheckBox;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stage getStage() {
		return this.editStage;
	}

	@Override
	public void setStage(Stage stage) {
		this.editStage = stage;
	}

}
