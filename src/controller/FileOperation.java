package controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import model.DirItem;
import model.FileSystem;
import utils.Utility;

public class FileOperation implements Initializable{
	
	private FileSystem fileSystem;
	
	
    @FXML
    private AnchorPane parentPane;

    @FXML
    private TreeView<DirItem> treeView;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Rectangle rect;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		this.fileSystem = new FileSystem();
		// TODO Auto-generated method stub
		try {
			Utility.genTreeView(this.treeView, this.fileSystem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
    @FXML
    public void createDir(ActionEvent event) {
    	DirItem[] itemList;
    	DirItem parentDir;
		try {
			parentDir = this.treeView.getSelectionModel().getSelectedItem().getValue();
			itemList = fileSystem.getFileTree(parentDir);
			if (Utility.countValidItem(itemList, this.fileSystem) >= 8) {
				// TODO 提示
				System.out.println("已达目录项上限");
			}
			for (DirItem dirItem : itemList) {
				if(dirItem.getAttribute() == 0) {
					fileSystem.createDir("tes", DirItem.DIR, parentDir);
					break;
				}
			}
			this.refreshTreeView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	
    	
    }
    
    public void refreshTreeView() {
    	try {
			Utility.genTreeView(this.treeView, this.fileSystem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
