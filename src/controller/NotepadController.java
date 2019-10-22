package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NotepadController extends RootController {
	
	private Stage notePadStage;
	
    @FXML
    private MenuItem saveButton;

    @FXML
    private MenuItem saveAndExitButton;

    @FXML
    private MenuItem exitButtton;

    @FXML
    private TextArea textArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stage getStage() {
		return this.notePadStage;
	}

	@Override
	public void setStage(Stage stage) {
		this.notePadStage = stage;
	}

}