package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.DirItem;
import model.FAT;
import model.FileSystem;

public class Utility {
	public static void genTreeView(TreeView<DirItem> treeView, FileSystem fileSys) throws Exception {
		byte[] val = new byte[] {'/', 0,0,0,0,8,2,0};
		TreeItem<DirItem> treeRoot = new TreeItem<DirItem>(new DirItem(val, "", (byte)2, (byte)0));
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
	
	public static TreeItem<DirItem> getTreeItem(String s, TreeView<DirItem> treeView) throws Exception {
		List<String> paths = Utility.parsePath(s);
		TreeItem<DirItem> treeItem = treeView.getRoot();
		TreeItem<DirItem> current = treeItem;
		Iterator<String> sIter = paths.iterator();
		while(sIter.hasNext()) {
			String name = sIter.next();
			Iterator<TreeItem<DirItem>> itemIter = current.getChildren().iterator();
			boolean flag = true;
			while (itemIter.hasNext()) {
				TreeItem<DirItem> item = itemIter.next();
				if(item.getValue().getName().trim().equals(name)) {
					current = item;
					flag = false;
				}
			}
			if(flag) {
				throw new Exception("找不到目录");
			}
		}
		return current;
		
	}
	
	/**
	 * 用于解析路径, 将每一个文件夹名按序存放在数组中返回
	 * 例: "/aaa/bbb/" 或 "//aaa//bbb" 都返回: ["aaa", "bbb"]  
	 * @param path
	 * @return 路径名数组
	 */
	public static List<String> parsePath(String path) {
		String[] paths = path.split("/");
		List<String> pathList = new ArrayList<String>();
		for (String string : paths) {
			pathList.add(string);
		}
		Stream<String> stream = pathList.stream();
		pathList = stream.filter(element->(element.length() != 0)).collect(Collectors.toList());
		return pathList;

	}


}
