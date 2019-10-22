package model;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import controller.FileSystemController;
import controller.RootController;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class NotepadTab extends Tab {
	private DirItem dirItem;
	private BorderPane borderPane;
	private MenuBar menuBar;
	private TextArea textArea;

	public NotepadTab(DirItem dirItem) {
		this.dirItem = dirItem;
		this.borderPane = new BorderPane();
		this.initMenuBar();
		this.initTextArea();
		this.borderPane.setPadding(new Insets(3, 3, 3, 3));
		super.setContent(this.borderPane);
	}
	
	private void initTextArea() {
		byte[] text;
		try {
			text = ((FileSystemController)RootController.controllers.get("contrller.FileSystemController")).getFileSystem().read(this.dirItem, FileSystem.WRITE);
			String textString = new String(text);
			this.textArea.setText(textString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.borderPane.setCenter(this.textArea);
	}
	
	private void initMenuBar() {
		this.menuBar = new MenuBar();
		Menu fileMenu = new Menu("文件");
		MenuItem save = new MenuItem("保存");
		MenuItem saveAndQuit = new MenuItem("保存并退出");
	    MenuItem quit = new MenuItem("退出");
	    
	    save.setOnAction(event ->{
	    	//TODO 保存
	    });

	    saveAndQuit.setOnAction(event ->{
	    	//TODO 保存并退出
	    });
	    
	    quit.setOnAction(event ->{
	    	//TODO 退出
	    });
	    
	    fileMenu.getItems().addAll(save,saveAndQuit,quit);
	    this.menuBar.getMenus().add(fileMenu);
	    this.borderPane.setTop(this.menuBar);
	}
}
