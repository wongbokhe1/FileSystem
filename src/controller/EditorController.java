package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.Closeable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.DirItem;
import utils.Utility;

public class EditorController extends RootController {

	private boolean canSaveName = true;

	private DirItem dirItem;

	private Stage editStage;

	@FXML
	private TextField fileNameTextField;

	@FXML
	private TextField fileTypeTextField;
	
    @FXML
    private Text typeText;

    @FXML
    private Text attrText;

	@FXML
	private CheckBox regularFileCheckBox;

	@FXML
	private CheckBox systemFileCheckBox;

	@FXML
	private CheckBox readOnlyFileCheckBox;

	@FXML
	private Button cancelButton;

	@FXML
	private Button confirmButton;

	private Pattern pattern = Pattern.compile("/");

	private Matcher matcher;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addTextDetector();
	}

	public void showStage(DirItem dirItem, boolean isFile) throws Exception {
		this.dirItem = dirItem;
		this.setFileNameTextField(dirItem.getName());
		// 文件
		if (isFile) {
			this.typeText.setVisible(true);
			this.attrText.setVisible(true);
			this.fileTypeTextField.setVisible(true);
			this.readOnlyFileCheckBox.setVisible(true);
			this.regularFileCheckBox.setVisible(true);
			this.systemFileCheckBox.setVisible(true);
			this.setFileTypeTextField(dirItem.getType());
			this.setRegularFileCheckBox(dirItem.isRegularFile());
			this.setSystemFileCheckBox(dirItem.isSystemFile());
			this.setReadOnlyFileCheckBox(dirItem.isReadOnlyFile());
		} else {
			// 文件夹
			this.typeText.setVisible(false);
			this.attrText.setVisible(false);
			this.fileTypeTextField.setVisible(false);
			this.readOnlyFileCheckBox.setVisible(false);
			this.regularFileCheckBox.setVisible(false);
			this.systemFileCheckBox.setVisible(false);
		}

		if (!this.editStage.isShowing()) {
			this.editStage.show();
		} else {
			this.editStage.hide();
			this.editStage.show();
		}

	}

	@FXML
	public void close() {
		((EditorController) RootController.controllers.get("controller.EditorController")).getStage().hide();
		//TODO refresh
	}

	@FXML
	public void confirm() {
		// 修改目录项
		if (canSaveName) {
			try {
				this.dirItem.setName(getFileNameTextField().getText());
			} catch (Exception e) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("Error");
				errorAlert.setContentText(e.getMessage());
				errorAlert.setHeaderText(null);
				errorAlert.showAndWait();
				this.fileNameTextField.setText(dirItem.getName());
				return;
			}
			try {
				this.dirItem.setType(getFileTypeTextField().getText());
			} catch (Exception e) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("Error");
				errorAlert.setContentText(e.getMessage());
				errorAlert.setHeaderText(null);
				errorAlert.showAndWait();
				this.fileTypeTextField.setText(dirItem.getType());;
				return;
			}
			byte attribute = 0;
			if(readOnlyFileCheckBox.isSelected()) {
				attribute += 1;
			}
			if(systemFileCheckBox.isSelected()) {
				attribute += 2;
			}
			if(regularFileCheckBox.isSelected()) {
				attribute += 4;
			}
			try {
				this.dirItem.setAttribute(attribute);
			} catch (Exception e) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setTitle("Error");
				errorAlert.setContentText(e.getMessage());
				errorAlert.setHeaderText(null);
				errorAlert.showAndWait();
				// 复原 check box
				this.setRegularFileCheckBox(dirItem.isRegularFile());
				this.setSystemFileCheckBox(dirItem.isSystemFile());
				this.setReadOnlyFileCheckBox(dirItem.isReadOnlyFile());
				return;
			}
			
			//修改目录项
			byte parentBlock = this.dirItem.getCurrentBlock();
			byte inblockIndex = this.dirItem.getIndex();
			byte[] block = ((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().getDisk().getBlock(parentBlock);
			byte[][] matrix = Utility.reshape(block);
			matrix[inblockIndex] = this.dirItem.getValues();
			block = Utility.flatten(matrix);
			((FileSystemController)RootController.controllers.get("controller.FileSystemController")).getFileSystem().getDisk().setBlock(parentBlock, block);
			
			
		} else {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Error");
			errorAlert.setContentText("名称不得有\"/\"！");
			errorAlert.setHeaderText(null);
			errorAlert.showAndWait();
			this.fileNameTextField.setText(dirItem.getName());
			return;
		}
		((EditorController) RootController.controllers.get("controller.EditorController")).getStage().hide();
		// refresh
		try {
			((FileSystemController) RootController.controllers.get("controller.FileSystemController")).refreshTreeView(dirItem.getPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("error");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
			e.printStackTrace();
		}
	}

	private void addTextDetector() {
		this.fileNameTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				EditorController.this.matcher = EditorController.this.pattern.matcher(newValue);
				if (EditorController.this.matcher.find()) {
					EditorController.this.canSaveName = false;
				} else {
					EditorController.this.canSaveName = true;
				}
			}
		});
	}

	@Override
	public Stage getStage() {
		return this.editStage;
	}

	@Override
	public void setStage(Stage stage) {
		this.editStage = stage;
	}

	public TextField getFileNameTextField() {
		return fileNameTextField;
	}

	public void setFileNameTextField(String name) {
		this.fileNameTextField.setText(name);
	}

	public TextField getFileTypeTextField() {
		return fileTypeTextField;
	}

	public void setFileTypeTextField(String type) {
		this.fileTypeTextField.setText(type);
	}

	public CheckBox getRegularFileCheckBox() {
		return regularFileCheckBox;
	}

	public void setRegularFileCheckBox(boolean isRegularFile) {
		this.regularFileCheckBox.setSelected(isRegularFile);
	}

	public CheckBox getSystemFileCheckBox() {
		return systemFileCheckBox;
	}

	public void setSystemFileCheckBox(boolean isSystemFile) {
		this.systemFileCheckBox.setSelected(isSystemFile);
	}

	public CheckBox getReadOnlyFileCheckBox() {
		return readOnlyFileCheckBox;
	}

	public void setReadOnlyFileCheckBox(boolean isReadOnlyFile) {
		this.readOnlyFileCheckBox.setSelected(isReadOnlyFile);
	}

}
