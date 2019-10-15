package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.print.Collation;

public class FileSystem implements FileSystemInterface{

	private FAT fat;
	private Disk disk;
	private List<DirItem> openedFile = new ArrayList<DirItem>(5);
	
	public FileSystem() {
		this.fat = new FAT();
		this.disk = new Disk();
		
	}
	
	@Override
	public DirItem createFile(String name, String type, byte attribute, String path) throws Exception{
		if ((attribute | DirItem.READONLY) == 0) {
			throw new Exception("不能建立只读文件");
		}
		
		this.checkPath(path);
		
		this.checkName(name, path);
		
		
		DirItem file = new DirItem();
		// TODO disk allocation
		file.setName(name);
		file.setType(type);
		file.setAttribute(attribute);
		file.setSize((byte) 0);
		file.setPath(path);
		byte blockNum = fat.getEmptyLocation();
		fat.setTable(blockNum, FAT.USED);

		return file;
	}

	@Override
	public DirItem createDir(String name, byte attribute, String path) throws Exception{
		this.checkPath(path);
		
		this.checkName(name, path);
		
		DirItem dir = new DirItem();
		// TODO disk allocation
		dir.setName(name);
		dir.setAttribute(attribute);
		dir.setPath(path);
		byte blockNum = fat.getEmptyLocation();
		fat.setTable(blockNum, FAT.USED);
		return dir;
	}
	
	@Override
	public DirItem createFile(String name, String type, byte attribute, DirItem paretent) throws Exception{
		if ((attribute | DirItem.READONLY) == 0) {
			throw new Exception("不能建立只读文件");
		}
		
		this.checkPath(paretent);
		
		// CHECK name's uniqueness
		this.checkName(name, paretent);
		
		
		DirItem file = new DirItem();
		// TODO disk allocation 
		file.setName(name);
		file.setType(type);
		file.setAttribute(attribute);
		file.setSize((byte) 0);
		file.setPath(paretent.getPath()+"/"+name);
		byte blockNum = fat.getEmptyLocation();
		fat.setTable(blockNum, FAT.USED);

		return file;
	}

	@Override
	public DirItem createDir(String name, byte attribute, DirItem paretent) throws Exception{

		this.checkPath(paretent);

		// CHECK name's uniqueness
		this.checkName(name, paretent);
		
		DirItem dir = new DirItem();
		// TODO disk allocation
		dir.setName(name);
		dir.setAttribute(attribute);
		dir.setPath(paretent.getPath()+"/"+name);
		byte blockNum = fat.getEmptyLocation();
		fat.setTable(blockNum, FAT.USED);
		return dir;
	}
	
	
	/**
	 * delete empty directory
	 */
	@Override
	public void deleteDir(DirItem item) throws Exception {
		
		this.checkPath(item);
		
		// check root directory
		if(item.getStartBlock() == FileSystemInterface.ROOT) {
			throw new Exception("该目录为根目录，不能删除");
		}
		
		// check empty directory
		DirItem[] fileTree = this.getFileTree(item);
		for (DirItem i: fileTree) {
			if(!i.isEmpty()) {
				throw new Exception("该目录不为空目录，不能删除");
			}
		}
		
		// delete single directory
		byte startBlock = item.getStartBlock();
		fat.setTable(startBlock, FAT.EMPTY);
	}

	@Override
	public byte[] open(DirItem item, byte mode) throws Exception{
		
		this.checkPath(item);
		
		//不能以写方式打开只读文件
		if(mode == FileSystem.WRITE && item.getMode() == DirItem.READONLY) {
			throw new Exception("不能以写方式打开只读文件");
		}
		
		for(int i = 0; i<5; i++) {
			if(openedFile.get(i) == null) {
				item.setMode(mode);
				openedFile.set(i, item); 
				byte[] data = new byte[item.getSize()];
				byte blockNum = item.getStartBlock();
				int idx = 0;
				byte[] block = disk.getBlock(blockNum);
				for (byte b : block) {
					data[idx] = b;
					idx++;
				}
				while(FAT.USED != fat.getLocation(blockNum)) {
					block = disk.getBlock(blockNum);
					for (byte b : block) {
						data[idx] = b;
						idx++;
					}
					blockNum = fat.getLocation(blockNum);
				}
				return data;
			}
		}
		throw new Exception("仅容许打开5个文件, 但尝试打开更多文件");
		
		
	}


	@Override
	public void write(DirItem item, byte[] buffer) throws Exception {
		//检查是否打开，否则打开
		if(!isOpen(item)) {
			this.open(item, FileSystem.WRITE);
		}
		
		//判断磁盘剩余空间是否能满足
		if(buffer.length > fat.getVacantCount()*Disk.blockSize) {
			throw new Exception("磁盘空间不足");
		}
		
		//获取当前的末尾盘块号
		byte blockNum = item.getStartBlock();
		while(fat.getLocation(blockNum) != FAT.USED) {
			blockNum = fat.getLocation(blockNum);
		}
		
		byte lastBlockNum = blockNum;
		byte emptyBlockNum;
		byte[] dividedBlock;
		
		int amount = (buffer.length / Disk.blockSize) + 1;
		
		for(int i = 0; i < amount; i++) {
			if(i == amount - 1) {
				dividedBlock = Arrays.copyOfRange(buffer, i*Disk.blockSize, buffer.length-1);
			}else {
				dividedBlock = Arrays.copyOfRange(buffer, i*Disk.blockSize, (i+1)*Disk.blockSize-1);
			}
			//获取空盘块号
			emptyBlockNum = fat.getEmptyLocation();
			//改写FAT
			fat.setTable(lastBlockNum, emptyBlockNum);
			//写入磁盘
			lastBlockNum = emptyBlockNum;
			disk.setBlock(lastBlockNum, dividedBlock);
		}
		//标志文件结束
		fat.setTable(lastBlockNum, FAT.USED);
		
		//修改目录项的文件长度
		item.setSize((byte)(item.getSize() +amount));
		
	}

	@Override
	public byte[] read(DirItem item, byte mode) throws Exception {
		//检查是否以写方式打开，是则不允许读
		if(mode == FileSystem.WRITE) {
			throw new Exception("不能以写方式打开");
		}
		
		//检查是否打开，否则打开
		if(!isOpen(item)) {
			this.open(item, mode);
		}
		
		byte[] data = new byte[item.getSize()];
		byte blockNum = item.getStartBlock();
		int idx = 0;
		byte[] block = disk.getBlock(blockNum);
		for (byte b : block) {
			data[idx] = b;
			idx++;
		}
		while(FAT.USED != fat.getLocation(blockNum)) {
			block = disk.getBlock(blockNum);
			for (byte b : block) {
				data[idx] = b;
				idx++;
			}
			blockNum = fat.getLocation(blockNum);
		}
		return data;
	}

	@Override
	public void delete(DirItem item) throws Exception {
		
		this.checkPath(item);
		
		if(this.isOpen(item)) {
			throw new Exception("文件已打开");
		}
		
		//修改FAT，归还磁盘空间
		byte currentBlock = item.getStartBlock();
		byte nextBlock = fat.getLocation(currentBlock);
		while(fat.getLocation(currentBlock) != FAT.USED) {
			fat.setTable(currentBlock, FAT.EMPTY);
			currentBlock = nextBlock;
			nextBlock = fat.getLocation(currentBlock);
		}
		
	}

	@Override
	public void modify(DirItem item, byte attribute) throws Exception {
		
		this.checkPath(item);
		
		if(this.isOpen(item)) {
			throw new Exception("文件已打开");
		}
		
		item.setAttribute(attribute);
		
	}

	@Override
	public DirItem[] getFileTree() throws Exception {
		byte[] data = disk.getBlock(FileSystem.ROOT);
		DirItem[] dirItems = new DirItem[8];
		for(int i = 0; i<8; i++) {
			dirItems[i] = new DirItem(Arrays.copyOfRange(data, i*8, i*8+7), "/");
		}
		return dirItems;
		
	}

	@Override
	public DirItem[] getFileTree(DirItem item) throws Exception {
		byte[] data = disk.getBlock(item.getStartBlock());
		DirItem[] dirItems = new DirItem[8];
		for(int i = 0; i<8; i++) {
			dirItems[i] = new DirItem(Arrays.copyOfRange(data, i*8, i*8+7), item.getPath());
		}
		return dirItems;
	}
	
	@Override
	public DirItem[] getFileTree(String path) throws Exception {
		List<String> dirList = FileSystem.parsePath(path);
		DirItem[] current = this.getFileTree();
		Iterator<String> iter = dirList.iterator();
		while(iter.hasNext()) {
			String name = iter.next();
			for (DirItem dirItem : current) {
				if (name.equals(dirItem.getName())) {
					current = getFileTree(dirItem);
					break;
				}
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

	@Override
	public void close(DirItem item, byte[] buffer) throws Exception {
		if(!isOpen(item)) {
			return;
		}
		
//		if(buffer == null) {
//			int idx = this.openedFile.indexOf(item);
//			this.openedFile.get(idx).setMode(FileSystem.CLOSED);//
//			this.openedFile.set(idx, null);
//		} else {
//			this.write(item, buffer);
//		}
		
		if(buffer != null) {
			this.write(item, buffer);
		}
		int idx = this.openedFile.indexOf(item);
		this.openedFile.get(idx).setMode(FileSystem.CLOSED);//
		this.openedFile.set(idx, null);
	}

	public FAT getFat() {
		return fat;
	}

	public void setFat(FAT fat) {
		this.fat = fat;
	}

	public Disk getDisk() {
		return disk;
	}

	public void setDisk(Disk disk) {
		this.disk = disk;
	}

	public List<DirItem> getOpenedFile() {
		return openedFile;
	}

	public void setOpenedFile(List<DirItem> openedFile) {
		this.openedFile = openedFile;
	}
	
	/**
	 * 检查某目录/路径是否存在
	 */
	private boolean checkPath(DirItem item) throws Exception {
		//TODO 
//		String path = item.getPath();
//		if() {
//			throw new Exception("目录不存在");
//		}
		return true;
	}
	
	/**
	 * 检查某目录/路径是否存在
	 */
	private boolean checkPath(String path) throws Exception {
		//TODO 
//		if() {
//			throw new Exception("目录不存在");
//		}
		return true;
	}
	
	/**
	 * 检查该目录是否有重名目录/文件
	 */
	private void checkName(String name, String path) throws Exception {
		//TODO
//		if()) {
//			throw new Exception("已有该目录/文件");
//		}
		return;
	}
	
	/**
	 * 检查该目录是否有重名目录/文件
	 */
	private void checkName(String name, DirItem item) throws Exception {
		DirItem[] fileTree = this.getFileTree(item);
		for(DirItem d: fileTree) {
			if(name.equalsIgnoreCase(d.getName())) {
				throw new Exception("已有该目录/文件");
			}
		}
		return;
	}
	
	/**
	 * 检查文件是否已打开
	 */
	private boolean isOpen(DirItem item) {
		int idx = openedFile.indexOf(item);
		if(idx != -1) {
			return true;
		}else {
			return false;
		}
		
	}
}
