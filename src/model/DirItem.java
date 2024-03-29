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
		if("".equals(path)) {
			this.path = this.getName().trim();
		}
		else {
			if((this.getAttribute()&DirItem.DIR)>0) {
				this.path = path + this.getName().trim() + "/";
			}else {
				this.path = path + this.getName().trim();
			}
			
		}
		this.currentBlock = currentBlock;
		this.index = index;
	}
	
	public DirItem() {
		
	}
	
	
	public void setValues(byte[] values) {
		this.values = values;
	}

	@Override
	public byte getAttribute() {
		return this.values[5];
	}

	@Override
	public String getName(){
		byte[] name = Arrays.copyOfRange(this.values, 0, 3);
		return new String(name);
//		if ((values[5] & (DirItem.FILE | DirItem.SYS_FILE | DirItem.DIR)) > 0) {
//			byte[] name = Arrays.copyOfRange(this.values, 0, 3);
//			return new String(name);
//		}
//		throw new Exception("目录项错误");
	}

	@Override
	public String getType(){
		return new String(Arrays.copyOfRange(this.values, 3, 5));

		
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
		if("".equals(name)) {
			throw new Exception("文件名不能为空");
		}
		
		byte[] bName = name.getBytes();
		if(bName.length > 3) {
			throw new Exception("文件名过长, 最大允许长度为3");
		}
		for (int i = 0; i<3; i++) {
			this.values[i] = 0; 
		}
		for(int i = 0; i<bName.length; i++) {
			this.values[i] = bName[i];
		}
		
	}


	@Override
	public void setAttribute(byte attribute) throws Exception {
		if(attribute == 0) {
			throw new Exception("属性非法！");
		}
		this.values[5] = attribute;
	}


	@Override
	public void setType(String type)  throws Exception{
		byte[] bName = type.getBytes();
		if(bName.length > 2) {
			throw new Exception("类型名过长, 最大允许长度为2");
		}
		for(int i = 0; i<2; i++) {
			this.values[i+3] = bName[i];
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
			if((this.getAttribute() & DirItem.DIR) > 0) {
				return this.getName().trim();
			} else {
				return this.getName().trim() + "." + this.getType().trim();
			}
			
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
	
	public boolean isRegularFile() {
		if ((this.values[5] & DirItemInterface.FILE) > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isSystemFile() {
		if ((this.values[5] & DirItemInterface.SYS_FILE) > 0) {
			return true;
		}else {
			return false;
		}
	}

	public boolean isReadOnlyFile() {
		if ((this.values[5] & DirItemInterface.READONLY) > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isDir() {
		if ((this.values[5] & DirItemInterface.DIR) > 0) {
			return true;
		}else {
			return false;
		}
		
	}
}
