package controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.DirItem;
import model.FileLabel;
import model.FileSystem;
import utils.Utility;


public class FileSystemController extends RootController{

    @FXML
    private TreeView<DirItem> treeView;
    
    private Stage stage;

    @FXML
    private FlowPane flowPane;
    
	private FileSystem fileSystem;
	
    @FXML
    private Rectangle rect;
    
    @FXML
    private Button createFileButton;

    @FXML
    private Button createDirButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		FileSystemController.this.createDirButton.setDisable(true);
		FileSystemController.this.createFileButton.setDisable(true);
		
		this.fileSystem = new FileSystem();
		// TODO Auto-generated method stub
		try {
			Utility.genTreeView(this.treeView, this.fileSystem);
			this.initListener();
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
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("error");
				errorAlert.setContentText("超过目录项上限");
				errorAlert.setHeaderText(null);
			}
			

			
			for(int i = 0; i<itemList.length; i++) {
				if(itemList[i].getAttribute() == 0) {
					TextInputDialog dialog = new TextInputDialog("新建文件夹");
					dialog.setTitle("新建文件夹");
					dialog.setHeaderText(String.format("在%s目录新建文件夹", parentDir.getPath()));
					dialog.setContentText("文件夹名: ");
					Optional<String> res = dialog.showAndWait();
					if(res.isPresent()) {
						fileSystem.createDir(res.get(), DirItem.DIR, parentDir, i);
					}
					break;
				}
			}
			this.refreshTreeView();
		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("error");
			errorAlert.setHeaderText(null);
			if("文件名过长, 最大允许长度为3".equals(e.getMessage())) {
				errorAlert.setContentText("文件名过长, 最大允许长度为3");
			} else {
				e.printStackTrace();
			}
			errorAlert.showAndWait();
			
		}
    }
    
    @FXML
    void createFile(ActionEvent event) {
    	DirItem[] itemList;
    	DirItem parentDir;
		try {
			parentDir = this.treeView.getSelectionModel().getSelectedItem().getValue();
			itemList = fileSystem.getFileTree(parentDir);
			if (Utility.countValidItem(itemList, this.fileSystem) >= 8) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("error");
				errorAlert.setContentText("超过目录项上限");
				errorAlert.setHeaderText(null);
			}
			

			
			for(int i = 0; i<itemList.length; i++) {
				if(itemList[i].getAttribute() == 0) {
					TextInputDialog dialog = new TextInputDialog("新建文件");
					dialog.setTitle("新建文件");
					dialog.setHeaderText(String.format("在%s目录新建文件夹", parentDir.getPath()));
					dialog.setContentText("文件夹名: ");
					Optional<String> res = dialog.showAndWait();
					if(res.isPresent()) {
						fileSystem.createFile(res.get(), "tx", DirItem.FILE, parentDir, i);
					}
					break;
				}
			}
			this.refreshTreeView();
		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("error");
			errorAlert.setHeaderText(null);
			if("文件名过长, 最大允许长度为3".equals(e.getMessage())) {
				errorAlert.setContentText("文件名过长, 最大允许长度为3");
			} else {
				e.printStackTrace();
			}
			errorAlert.showAndWait();
			
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
    
    private void initListener() {
    	treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<DirItem>>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem<DirItem>> observable, TreeItem<DirItem> oldValue,
					TreeItem<DirItem> newValue) {
				if(newValue!=null) {
					FileSystemController.this.createDirButton.setDisable(false);
					FileSystemController.this.createFileButton.setDisable(false);
				}
				
				try {
					FileSystemController.this.flowPane.getChildren().clear();
					DirItem[] items = FileSystemController.this.fileSystem.getFileTree(newValue.getValue());
					for (DirItem dirItem : items) {
						if(Utility.validItem(dirItem, FileSystemController.this.fileSystem)) {
							FileLabel fileLabel = new FileLabel(dirItem);
							fileLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

								@Override
								public void handle(MouseEvent event) {
									if (event.getClickCount() >= 2 && event.getButton() == MouseButton.PRIMARY) {
										// 双击打开事件
										
										FileLabel f = (FileLabel)event.getSource();
										System.out.println("file opened: " + f);
										// 打开编辑窗口
										if(!RootController.controllers.get("controller.NotepadController").getStage().isShowing()) {
											((NotepadController)RootController.controllers.get("controller.NotepadController")).getStage().show();
										}
										// 打开对应文件
										((NotepadController)RootController.controllers.get("controller.NotepadController")).openFile(((FileLabel)event.getSource()).getDirItem());
									}
								}
							});
							FileSystemController.this.flowPane.getChildren().add(fileLabel);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

    		
    	});
    }

	public Button getCreateFileButton() {
		return createFileButton;
	}

	public void setCreateFileButton(Button createFileButton) {
		this.createFileButton = createFileButton;
	}

	public Button getCreateDirButton() {
		return createDirButton;
	}

	public void setCreateDirButton(Button createDirButton) {
		this.createDirButton = createDirButton;
	}

	@Override
	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}


    
    

}
