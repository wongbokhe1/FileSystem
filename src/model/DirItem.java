package model;

import java.util.Arrays;


public class DirItem implements DirItemInterface {

	private byte[] values = {0,0,0,0,0,0,0,0};
	private String path;
	private byte mode = 0;
	private byte currentBlock;
	private byte index;
	
	
	public DirItem(byte[] values, String path, byte currentBlock, byte index) throws Exception {
		this.values = values;
		path += "/" + this.getName();
		this.currentBlock = currentBlock;
		this.index = index;
	}
	
	public DirItem() {
		
	}
	
	
	@Override
	public byte getAttribute() {
		return this.values[5];
	}

	@Override
	public String getName() throws Exception{
		byte[] name = Arrays.copyOfRange(this.values, 0, 3);
		return new String(name);
//		if ((values[5] & (DirItem.FILE | DirItem.SYS_FILE | DirItem.DIR)) > 0) {
//			byte[] name = Arrays.copyOfRange(this.values, 0, 3);
//			return new String(name);
//		}
//		throw new Exception("目录项错误");
	}

	@Override
	public String getType() throws Exception{
		if ((values[5] & (DirItem.FILE | DirItem.SYS_FILE)) == 0) {
			return new String(Arrays.copyOfRange(this.values, 3, 5));
		}
		throw new Exception("对非文件取类型");
		
	}

	@Override
	public byte getSize(){
		return this.values[7];
	}

	@Override
	public byte getStartBlock(){
		return this.values[6];
	}


	@Override
	public void setName(String name) throws Exception{
		byte[] bName = name.getBytes();
		if(bName.length > 3) {
			throw new Exception("文件名过长, 最大允许长度为3");
		}
		for(int i = 0; i<3; i++) {
			this.values[i] = bName[i];
		}
		
	}


	@Override
	public void setAttribute(byte attribute) {
		this.values[5] = attribute;
	}


	@Override
	public void setType(String type)  throws Exception{
		byte[] bName = type.getBytes();
		if(bName.length > 2) {
			throw new Exception("类型名过长, 最大允许长度为2");
		}
		for(int i = 0; i<2; i++) {
			this.values[i+3] = bName[i+3];
		}
		
	}


	@Override
	public void setSize(byte size) {
		this.values[7] = size;
		
	}


	@Override
	public void setStartBlock(byte startBlock) {
		this.values[6] = startBlock;
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirItem other = (DirItem) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public byte[] getValues() {
		return values;
	}


	public boolean isEmpty() {
		for(byte b:values) {
			if(b != 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		try {
			return this.getName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public byte getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(byte currentBlock) {
		this.currentBlock = currentBlock;
	}

	public byte getIndex() {
		return index;
	}

	public void setIndex(byte index) {
		this.index = index;
	}
	

	

}
