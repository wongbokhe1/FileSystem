package utils;

import java.util.Arrays;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.DirItem;
import model.FAT;
import model.FileSystem;

public class Utility {
	public static void genTreeView(TreeView<DirItem> treeView, FileSystem fileSys) throws Exception {
		TreeItem<DirItem> treeRoot = new TreeItem<DirItem>(new DirItem("/\0\0\0\0\2\2\0".getBytes(), "/", (byte)2, (byte)0));
		treeView.setRoot(treeRoot);
		DirItem[] rootItems = fileSys.getFileTree();
		for (DirItem dirItem : rootItems) {
			if(dirItem.getStartBlock() != 0 && fileSys.getFat().getLocation(dirItem.getStartBlock()) != FAT.EMPTY && (dirItem.getAttribute() & DirItem.DIR) > 0) {
				treeRoot.getChildren().add(new TreeItem<DirItem>(dirItem));
			}
		}
		
		if(treeRoot.getChildren().isEmpty()) {
			return;
		}
		
		for (TreeItem<DirItem> dirItem : treeRoot.getChildren()) {
			genTreeItem(dirItem, fileSys);
		}
		
		
	}
	
	public static void genTreeItem(TreeItem<DirItem> root, FileSystem fileSys) throws Exception {
		DirItem[] items = fileSys.getFileTree(root.getValue());
		for(DirItem item: items) {
			if(item.getStartBlock() != 0 && fileSys.getFat().getLocation(item.getStartBlock()) != FAT.EMPTY && (item.getAttribute() & DirItem.DIR) > 0) {
				root.getChildren().add(new TreeItem<DirItem>(item));
			}
		}
		
		if(root.getChildren().isEmpty()) {
			return;
		}
		
		for (TreeItem<DirItem> dirItem : root.getChildren()) {
			genTreeItem(dirItem, fileSys);
		}
		
	}
	
	public static int countValidItem(DirItem[] items, FileSystem fileSys) {
		int count = 0;
		for (DirItem dirItem : items) {
			if(Utility.validItem(dirItem, fileSys)) {
				count += 1;
			}
		}
		return count;
	}
	
	public static boolean validItem(DirItem item, FileSystem fileSys) {
		return item.getStartBlock() != 0 && fileSys.getFat().getLocation(item.getStartBlock()) != FAT.EMPTY;
	}
	
	public static byte[][] reshape(byte[] raw) {
		byte[][] rnt = new byte[8][8];
		for(int i = 0; i<8; i++) {
			rnt[i] = Arrays.copyOfRange(raw, i*8, i*8+8); 
		}
		return rnt;
	}
	
	public static byte[] flatten(byte[][] raw) {
		byte[] rnt = new byte[64];
		int i = 0;
		for (byte[] row : raw) {
			for (byte b : row) {
				rnt[i] = b;
				i++;
			}
		}
		return rnt;
	}

}
