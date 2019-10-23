package model;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import controller.FileSystemController;
import controller.NotepadController;
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
		try {
			setText(dirItem.getName());
			setClosable(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initTextArea() {
		this.textArea = new TextArea();
		try {
			byte[] text = ((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().read(this.dirItem, FileSystem.WRITE);
			String textString = new String(text);
			if(textString != null && textString.length() != 0) {
				this.textArea.setText(textString);
			} else {
				this.textArea.setText(" ");
			}
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
	    	this.save();
	    });

	    saveAndQuit.setOnAction(event ->{
	    	this.save();
	    	this.quit();
	    });
	    
	    quit.setOnAction(event ->{
	    	this.quit();
	    });
	    
	    fileMenu.getItems().addAll(save,saveAndQuit,quit);
	    this.menuBar.getMenus().add(fileMenu);
	    this.borderPane.setTop(this.menuBar);
	}
	
	public void save() {
		//  保存文件 -> 写入
		byte[] text = this.textArea.getText().getBytes();
		try {
			((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().write(this.dirItem, text);
			((FileSystemController)RootController.controllers.get("controller.FileSystemController")).upDateDiskUsingTable();
			((FileSystemController)RootController.controllers.get("controller.FileSystemController")).upDateFATTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void quit() {
		try {
			((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().close(this.dirItem, null);
			((NotepadController) RootController.controllers.get("controller.NotepadController")).removeTab(this);
			if(((NotepadController) RootController.controllers.get("controller.NotepadController")).getTabPane().getTabs().size() == 0) {
				((NotepadController) RootController.controllers.get("controller.NotepadController")).getStage().hide();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
