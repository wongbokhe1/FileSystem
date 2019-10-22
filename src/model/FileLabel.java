package model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import application.Main;
import controller.EditorController;
import controller.RootController;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class FileLabel extends Label {
	private DirItem dirItem;
	private Image image;
	private ImageView imageView;

	public FileLabel(DirItem dirItem) throws MalformedURLException {
		this.dirItem = dirItem;
		if (dirItem.getAttribute() == DirItem.FILE) {
			// TODO 判断文件/目录
			this.image = new Image(Main.class.getResourceAsStream("/view/file.png"), 85, 85, true, true);
		} else {
			this.image = new Image(Main.class.getResourceAsStream("/view/folder.png"), 85, 85, true, true);
		}
		this.imageView = new ImageView(this.image);
		super.setGraphic(this.imageView);
		try {
			super.setText(this.dirItem.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setGraphicTextGap(20);
		super.setContentDisplay(ContentDisplay.TOP);
		super.setPrefSize(100, 100);
		super.setPadding(new Insets(2, 2, 2, 2));
		super.setAlignment(Pos.CENTER);
		super.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);

	}

	@Override
	public String toString() {
		return dirItem.toString();
	}

}
