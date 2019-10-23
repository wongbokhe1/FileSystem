package controller;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.DirItem;
import model.FileLabel;
import model.FileSystem;
import utils.Utility;

public class FileSystemController extends RootController {


	@FXML
	private TreeView<DirItem> treeView;

	private Stage stage;


	@FXML
	private FlowPane flowPane;

	private FileSystem fileSystem;

	private ContextMenu flowpaneMenu;

	private NotepadController notepadController;


	private String currentPath;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.fileSystem = new FileSystem();
		// TODO Auto-generated method stub
		try {
			Utility.genTreeView(this.treeView, this.fileSystem);
			this.initListener();
			this.initHandler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createDir() {
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

			for (int i = 0; i < itemList.length; i++) {
				if (itemList[i].getAttribute() == 0) {
					TextInputDialog dialog = new TextInputDialog("新建文件夹");
					dialog.setTitle("新建文件夹");
					dialog.setHeaderText(String.format("新建文件夹"));
					dialog.setContentText("文件夹名: ");
					Optional<String> res = dialog.showAndWait();
					if (res.isPresent()) {
						fileSystem.createDir(res.get(), DirItem.DIR, parentDir, i);
					}
					break;
				}
			}
			this.refreshTreeView(parentDir.getPath());
		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("error");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();

		}
	}

	public void createFile() {
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

			for (int i = 0; i < itemList.length; i++) {
				if (itemList[i].getAttribute() == 0) {
					TextInputDialog dialog = new TextInputDialog("新建文件");
					dialog.setTitle("新建文件");
					dialog.setHeaderText(String.format("在%s目录新建文件夹", parentDir.getPath()));
					dialog.setContentText("文件夹名: ");
					Optional<String> res = dialog.showAndWait();
					if (res.isPresent()) {
						fileSystem.createFile(res.get(), "tx", DirItem.FILE, parentDir, i);
					}
					break;
				}
			}
			this.refreshTreeView(parentDir.getPath());
		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("error");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText(e.getMessage());
			e.printStackTrace();
			errorAlert.showAndWait();

		}
	}

	public void refreshTreeView(String targetPath) {
		try {
			Utility.genTreeView(this.treeView, this.fileSystem);
			if(targetPath != null) {
				TreeItem<DirItem> targetTreeItem = Utility.getTreeItem(targetPath, this.treeView);
				this.treeView.getSelectionModel().select(targetTreeItem);
			}
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
				FileSystemController.this.flowPane.getChildren().clear();
				if (newValue == null) {
					return;
				}

				FileSystemController.this.currentPath = newValue.getValue().getPath();
				System.out.println(FileSystemController.this.currentPath);

				try {
					DirItem[] items = FileSystemController.this.fileSystem.getFileTree(newValue.getValue());
					for (DirItem dirItem : items) {
						if (Utility.validItem(dirItem, FileSystemController.this.fileSystem)) {
							FileLabel fileLabel = new FileLabel(dirItem);
							ContextMenu menu = new ContextMenu();
							MenuItem modifyItem = new MenuItem("修改属性/重命名");
							MenuItem deleteItem = new MenuItem("删除");
							modifyItem.setOnAction(new RightClickHandler(fileLabel) {

								@Override
								public void handle(ActionEvent event) {
									super.handle(event);
									// TODO modify attribute/name
								}

							});
							deleteItem.setOnAction(new RightClickHandler(fileLabel) {

								@Override
								public void handle(ActionEvent event) {
									super.handle(event);
									try {
										FileSystemController.this.fileSystem.delete(fileLabel.getDirItem());
										FileSystemController.this.refreshTreeView(FileSystemController.this.treeView.getSelectionModel().getSelectedItem().getValue().getPath());
									} catch (Exception e) {
										Alert errorAlert = new Alert(AlertType.ERROR);
										errorAlert.setTitle("error");
										errorAlert.setHeaderText(null);
										errorAlert.setContentText(e.getMessage());
										e.printStackTrace();
										errorAlert.showAndWait();
									}
								}

							});
							menu.getItems().addAll(new MenuItem[] { modifyItem, deleteItem });
							fileLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent event) {
									if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
										// 双击打开事件
										FileLabel f = (FileLabel) event.getSource();
										System.out.println("file opened: " + f);

										// 打开文件
										if ((f.getDirItem().getAttribute() & DirItem.FILE) > 0) {
											//编辑窗口置顶
//											RootController.controllers.get("controller.NotepadController").getStage().setAlwaysOnTop(true);
											// 打开编辑窗口
											if (!RootController.controllers.get("controller.NotepadController").getStage().isShowing()) {
												((NotepadController) RootController.controllers.get("controller.NotepadController")).getStage().show();
											}
											// 打开对应文件
											((NotepadController) RootController.controllers.get("controller.NotepadController")).openFile(((FileLabel) event.getSource()).getDirItem());
										}
										
										// 打开目录
										if ((f.getDirItem().getAttribute() & DirItem.DIR) > 0) {
											String path = f.getDirItem().getPath();
											try {
												TreeItem<DirItem> tmp = Utility.getTreeItem(path, FileSystemController.this.treeView);
												FileSystemController.this.treeView.getSelectionModel().select(tmp);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									} else if(event.getButton() == MouseButton.SECONDARY) {
										menu.show(FileSystemController.this.flowPane, event.getScreenX(), event.getScreenY());
										event.consume();
									} else if (event.getButton() == MouseButton.PRIMARY && menu.isShowing()) {
										menu.hide();
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

	public void initHandler() {
		addFlowPaneHandler();
	}

	public void addFlowPaneHandler() {
		ContextMenu menu = new ContextMenu();
		MenuItem createFile = new MenuItem("新建文件");
		MenuItem createDir = new MenuItem("新建文件夹");
		createFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileSystemController.this.createFile();
			}
		});
		createDir.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileSystemController.this.createDir();
			}
		});
		menu.getItems().addAll(new MenuItem[] { createFile, createDir });

		this.flowpaneMenu = menu;

		this.flowPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY
						&& FileSystemController.this.treeView.getSelectionModel().getSelectedItem() != null) {
					FileSystemController.this.flowpaneMenu.show(FileSystemController.this.flowPane, event.getScreenX(),
							event.getScreenY());
				} else if (event.getButton() == MouseButton.PRIMARY
						|| FileSystemController.this.flowpaneMenu.isShowing()) {
					FileSystemController.this.flowpaneMenu.hide();
				}
			}
		});

	}

	@Override
	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
		
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				Platform.exit(); //退出程序
			}
		});
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

}

class RightClickHandler implements EventHandler<ActionEvent> {

	private FileLabel fileLable;

	public RightClickHandler(FileLabel fileLable) {
		super();
		this.fileLable = fileLable;
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub

	}

}
