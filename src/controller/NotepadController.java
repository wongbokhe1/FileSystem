package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
		// 检查是否已打开
		if (!((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().isOpen(dirItem)) {
			try {
				((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().open(dirItem, FileSystem.WRITE);
				NotepadTab notepadTab = new NotepadTab(dirItem);
				openNotepadTabs.put(dirItem.getPath(), notepadTab);
				tabPane.getTabs().add(notepadTab);
				this.tabPane.getSelectionModel().select(notepadTab);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			this.tabPane.getSelectionModel().select(openNotepadTabs.get(dirItem.getPath()));
		}
	}
	
	
	
	@Override
	public Stage getStage() {
		return this.notePadStage;
	}

	@Override
	public void setStage(Stage stage) {
		this.notePadStage = stage;
		this.notePadStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				// 窗口关闭，清空Tabs 清空已打开表hashmap & openedlist
				for(Map.Entry<String, NotepadTab> entry : openNotepadTabs.entrySet()) {
					NotepadTab tab = entry.getValue();
					tab.save();
					tab.quit();
					tabPane.getTabs().remove(tab);
				}
				openNotepadTabs.clear();
			}
		});
	}

}
