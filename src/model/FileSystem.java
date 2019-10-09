package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSystem implements FileSystemInterface{

	private FAT fat;
	private Disk disk;
	private List<DirItem> openedFile = new ArrayList<DirItem>(3);
	
	public FileSystem() {
		this.fat = new FAT();
		this.disk = new Disk();
		
	}
	
	@Override
	public DirItem createFile(String name, String type, byte attribute, String path) throws Exception{
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
	public byte[] open(DirItem item, byte mode) throws Exception{
		for(int i = 0; i<3; i++) {
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
				while(blockNum != fat.getLocation(blockNum)) {
					block = disk.getBlock(blockNum);
					for (byte b : block) {
						data[idx] = b;
						idx++;
					}
				}
				return data;
			}
		}
		throw new Exception("仅容许打开3个文件, 但尝试打开更多文件");
		
		
	}


	@Override
	public void write(DirItem item, byte[] buffer) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DirItem[] getFileTree() throws Exception {
		byte[] data = disk.getBlock(FileSystem.ROOT);
		DirItem[] dirItems = new DirItem[8];
		for(int i = 0; i<8; i++) {
			dirItems[i] = new DirItem(Arrays.copyOfRange(data, i, i+7), "/");
		}
		return dirItems;
		
	}

	@Override
	public DirItem[] getFileTree(DirItem item) throws Exception {
		byte[] data = disk.getBlock(item.getStartBlock());
		DirItem[] dirItems = new DirItem[8];
		for(int i = 0; i<8; i++) {
			dirItems[i] = new DirItem(Arrays.copyOfRange(data, i, i+7), item.getPath());
		}
		return dirItems;
	}
	
	@Override
	public DirItem[] getFileTree(String path) throws Exception {
		return null;
		
		
	}

	@Override
	public void close(DirItem item, byte[] buffer) throws Exception {
		if(buffer == null) {
			int idx = this.openedFile.indexOf(item);
			this.openedFile.get(idx).setMode(FileSystem.CLOSED);
			this.openedFile.set(idx, null);
		} else {
			this.write(item, buffer);
		}
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

		
}
