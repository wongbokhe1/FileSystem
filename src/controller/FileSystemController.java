package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.DirItem;
import model.Disk;
import model.FAT;
import model.FileLabel;
import model.FileSystem;
import utils.Utility;

public class FileSystemController extends RootController {

	@FXML
	private TreeView<DirItem> treeView;

	private Stage stage;

	private boolean flag = true;

	@FXML
	private FlowPane flowPane;

	private FileSystem fileSystem;

	private ContextMenu flowpaneMenu;
	private ContextMenu itemMenu;

	private NotepadController notepadController;

	private String currentPath;

	private PieChart.Data usedDisk;

	private PieChart.Data noUsedDisk;

	private double used = 0;
	private double noUsed = 1;

	private List<String> history;
	private ListIterator<String> currentDirIterator;

	@FXML
	private PieChart diskUsingPieChart;

	private ObservableList<PieChart.Data> pieChartData;
	
    @FXML
    private TextField pathText;

	@FXML
	private GridPane diskUsingTable;

	private List<StackPane> diskUsingTableBlocks = new ArrayList<StackPane>();
	

	@FXML
	private GridPane FATTable;
	private List<StackPane> FATTableBlocks = new ArrayList<StackPane>();
	private List<Text> FATTableTextBlocks = new ArrayList<Text>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.history = new ArrayList<String>();
		this.currentDirIterator = this.history.listIterator();
		loadExistDisk();
		this.initDiskUsingTable();
		this.initDiskUsingPieChart();
		this.initFATTable();
		try {
			Utility.genTreeView(this.treeView, this.fileSystem);
			this.treeView.getSelectionModel().select(this.treeView.getRoot());
			this.initListener();
			this.initHandler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.upDateDiskUsingTable();
		this.upDateFATTable();
		try {
			refreshTreeView("/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadExistDisk() {
		String savePath = "./disk.dat";
		try {
			FileInputStream fileInputStream = new FileInputStream(savePath);
			byte[] diskArray = new byte[Disk.blockSize * Disk.totalBlock];
			fileInputStream.read(diskArray);
			fileInputStream.close();
			this.fileSystem = new FileSystem(diskArray);

		} catch (FileNotFoundException e) {
			this.fileSystem = new FileSystem();
		} catch (IOException e) {
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
				errorAlert.showAndWait();
				return;
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
				errorAlert.showAndWait();
				return;
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
			errorAlert.showAndWait();

		}
	}

	public void refreshTreeView(String targetPath) throws Exception {
		int idx = this.treeView.getSelectionModel().getSelectedIndex();
		TreeItem<DirItem> tmpItem = this.treeView.getRoot();
		Utility.genTreeView(this.treeView, this.fileSystem);
		this.upDateDiskUsingTable();
		this.upDateFATTable();
		if (targetPath != null) {
			try {
				TreeItem<DirItem> targetTreeItem = Utility.getTreeItem(targetPath, this.treeView);
				this.treeView.getSelectionModel().select(targetTreeItem);
			} catch (Exception e) {
				this.treeView.setRoot(tmpItem);
				this.treeView.getSelectionModel().select(idx);
				throw e;
			}
			
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

				if (FileSystemController.this.flag) {
					if (FileSystemController.this.currentDirIterator.hasNext()) {
						ListIterator<String> iter = FileSystemController.this.history
								.listIterator(FileSystemController.this.currentDirIterator.nextIndex());
						iter.next();
						if (FileSystemController.this.currentDirIterator.hasNext()) {
							while (iter.hasNext()) {
								iter.next();
								iter.remove();
							}
						}
					}

					FileSystemController.this.history.add(newValue.getValue().getPath());
					FileSystemController.this.currentDirIterator = FileSystemController.this.history
							.listIterator(FileSystemController.this.history.size() - 1);
				}
				FileSystemController.this.flag = true;

				FileSystemController.this.currentPath = newValue.getValue().getPath();
				FileSystemController.this.pathText.setText(FileSystemController.this.currentPath);
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
									// modify attribute/name
									// 设置显示属性
									boolean isFile = true;
									if (dirItem.isDir()) {
										isFile = false;
									}
									try {
										((EditorController) RootController.controllers
												.get("controller.EditorController")).showStage(dirItem, isFile);

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							});
							deleteItem.setOnAction(new RightClickHandler(fileLabel) {

								@Override
								public void handle(ActionEvent event) {
									super.handle(event);
									try {
										FileSystemController.this.fileSystem.delete(fileLabel.getDirItem());
										FileSystemController.this.refreshTreeView(FileSystemController.this.treeView
												.getSelectionModel().getSelectedItem().getValue().getPath());
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
											// 编辑窗口置顶
//											RootController.controllers.get("controller.NotepadController").getStage().setAlwaysOnTop(true);
											// 打开编辑窗口
											if (!RootController.controllers.get("controller.NotepadController")
													.getStage().isShowing()) {
												((NotepadController) RootController.controllers
														.get("controller.NotepadController")).getStage().show();
											}
											// 打开对应文件
											((NotepadController) RootController.controllers
													.get("controller.NotepadController"))
															.openFile(((FileLabel) event.getSource()).getDirItem());
										}

										// 打开目录
										if ((f.getDirItem().getAttribute() & DirItem.DIR) > 0) {
											String path = f.getDirItem().getPath();
											try {
												TreeItem<DirItem> tmp = Utility.getTreeItem(path,
														FileSystemController.this.treeView);
												FileSystemController.this.treeView.getSelectionModel().select(tmp);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									} else if (event.getButton() == MouseButton.SECONDARY) {
										menu.show(FileSystemController.this.flowPane, event.getScreenX(),
												event.getScreenY());
										FileSystemController.this.itemMenu = menu;
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
					if(FileSystemController.this.itemMenu != null) {
						FileSystemController.this.itemMenu.hide();
					}
				}
			}
		});

	}

	private void initDiskUsingTable() {
		for (int i = 0; i < Disk.totalBlock; i++) {
			Text diskBlock = new Text(String.valueOf(i));
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #46e145");
			this.diskUsingTableBlocks.add(stackPane);
			this.diskUsingTable.add(stackPane, i % 8, i / 8);
		}
	}

	public void upDateDiskUsingTable() {
		this.used = 0;
		this.noUsed = 0;
		for (int i = 0; i < Disk.totalBlock; i++) {
			if (this.fileSystem.getFat().getLocation((byte) i) != FAT.EMPTY) {
				this.diskUsingTableBlocks.get(i).setStyle("-fx-background-color: #FF3333");
				this.used++;
			} else {
				this.diskUsingTableBlocks.get(i).setStyle("-fx-background-color: #DDDDDD");
				this.noUsed--;
			}
		}
		upDateDiskUsingPieChart();
	}

	private void initDiskUsingPieChart() {
		this.usedDisk = new PieChart.Data("已使用", this.used);
		this.noUsedDisk = new PieChart.Data("未使用", this.noUsed);
		this.pieChartData = FXCollections.observableArrayList(this.usedDisk, this.noUsedDisk);
		this.diskUsingPieChart.setData(this.pieChartData);
	}

	public void upDateDiskUsingPieChart() {
		this.usedDisk.setPieValue(this.used);
		this.noUsedDisk.setPieValue(this.noUsed);
	}

	private void initFATTable() {
		for (byte i = 0; i < 3; i++) {
			Text diskBlock = new Text("-1");
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #DDDDDD");
			this.FATTableTextBlocks.add(diskBlock);
			this.FATTableBlocks.add(stackPane);
			this.FATTable.add(stackPane, i / 64, i % 64);
		}
		for (int i = 3; i < Disk.totalBlock; i++) {
//			Text diskBlock = new Text(String.valueOf(this.fileSystem.getFat().getLocation(i)));
			Text diskBlock = new Text("0");
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(diskBlock);
			stackPane.setStyle("-fx-background-color: #DDDDDD");
			this.FATTableTextBlocks.add(diskBlock);
			this.FATTableBlocks.add(stackPane);
			this.FATTable.add(stackPane, i / 64, i % 64);
		}
	}

	public void upDateFATTable() {
		for (int i = 0; i < Disk.totalBlock; i++) {
			if (this.fileSystem.getFat().getLocation((byte) i) != FAT.EMPTY) {
				this.FATTableBlocks.get(i).setStyle("-fx-background-color: #FF3333");
				String string = String.valueOf(this.fileSystem.getFat().getLocation((byte) i));
				this.FATTableTextBlocks.get(i).setText(string);
			} else {
				this.FATTableBlocks.get(i).setStyle("-fx-background-color: #DDDDDD");
				String string = String.valueOf(this.fileSystem.getFat().getLocation((byte) i));
				this.FATTableTextBlocks.get(i).setText(string);
			}
		}
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

	public void initCloseEventHandler() {
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				String savePath = "./disk.dat";
				try {
					byte[][] fatBlocks = FileSystemController.this.fileSystem.getFat().getTable();
					FileSystemController.this.fileSystem.getDisk().setBlock(0, fatBlocks[0]);
					FileSystemController.this.fileSystem.getDisk().setBlock(1, fatBlocks[1]);
					FileOutputStream fileOutputStream = new FileOutputStream(savePath);
					byte[] diskArray = FileSystemController.this.fileSystem.getDisk().getDiskArray();
					fileOutputStream.write(diskArray);
					fileOutputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Platform.exit(); // 退出程序
			}
		});
	}

	@FXML
	void backButton(ActionEvent event) {
		if (this.currentDirIterator.hasPrevious()) {
			String path = this.currentDirIterator.previous();
			this.flag = false;
			try {
				this.refreshTreeView(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@FXML
	void nextButton(ActionEvent event) {
		if (this.currentDirIterator.hasNext()) {
			ListIterator<String> iter = this.history.listIterator(this.currentDirIterator.nextIndex());
			iter.next();
			if (iter.hasNext()) {
				String path = iter.next();
				this.flag = false;
				try {
					this.refreshTreeView(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.currentDirIterator = iter;
			}
		}
	}
	
    @FXML
    void trackButton(ActionEvent event) {
    	String[] pathList = this.currentPath.split("/");
    	String path = "/";
    	for(int i = 0; i<pathList.length-1; i++) {
    		path = path + pathList[i] + "/";
    	}
    	try {
			refreshTreeView(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void textEnter(KeyEvent event) {
    	if(event.getCode() == KeyCode.ENTER) {
    		try {
				refreshTreeView(this.pathText.getText());
			} catch (Exception e) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("error");
				errorAlert.setHeaderText(null);
				errorAlert.setContentText(e.getMessage());
				errorAlert.showAndWait();
			}
    	}
    	
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
