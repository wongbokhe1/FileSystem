package model;

import java.net.MalformedURLException;
import application.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileLabel extends Label {
	private DirItem dirItem;
	private Image image;
	private ImageView imageView;

	public FileLabel(DirItem dirItem) throws MalformedURLException {
		this.dirItem = dirItem;
		if (dirItem.getAttribute() == DirItem.FILE) {
			// 判断文件/目录
			this.image = new Image(Main.class.getResourceAsStream("/view/file_icon.png"), 85, 85, true, true);
		} else {
			this.image = new Image(Main.class.getResourceAsStream("/view/folder_icon.png"), 85, 85, true, true);
		}
		this.imageView = new ImageView(this.image);
		super.setGraphic(this.imageView);
		try {
			super.setText(this.dirItem.toString());
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
	
	public DirItem getDirItem() {
		return dirItem;
	}

	public void setDirItem(DirItem dirItem) {
		this.dirItem = dirItem;
	}

	@Override
	public String toString() {
		return dirItem.toString();
	}


}
