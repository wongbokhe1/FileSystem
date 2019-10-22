package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import model.DirItem;
import model.FileSystem;
import model.NotepadTab;

public class NotepadController extends RootController {
	
	private Stage notePadStage;
	
	private static Map<String, NotepadTab> openNotepadTabs = new HashMap<String, NotepadTab>();
	
    @FXML
    private TabPane tabPane;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void openFile(DirItem dirItem) {
		NotepadTab notepadTab = new NotepadTab(dirItem);
		openNotepadTabs.put(dirItem.getPath(), notepadTab);
		tabPane.getTabs().add(notepadTab);
		try {
			((FileSystemController)RootController.controllers.get("contrller.FileSystemController")).getFileSystem().open(dirItem, FileSystem.WRITE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
