package model;

public class MyFile {
	 private char[] name = {0, 0, 0}; 	//文件名或目录名:3个字节
	 private char[] type = {0, 0}; 		//文件类型:2个字节)
	 private byte attribute = 0; 		//文件属性(用来识别是文件还是目录): 1个字节
	 private byte startNum;   			//起始块盘号(在FAT表中起始位置):1个字节
	 private byte size;   				//文件的大小(占用的盘块数):1个字节
	 
	public MyFile() {

	}

	public MyFile(char[] name, char[] type, byte attribute, byte startNum, byte size) {
		super();
		this.name = name;
		this.type = type;
		this.attribute = attribute;
		this.startNum = startNum;
		this.size = size;
	}

	public char[] getName() {
		return name;
	}

	public void setName(char[] name) {
		this.name = name;
	}

	public char[] getType() {
		return type;
	}

	public void setType(char[] type) {
		this.type = type;
	}

	public byte getAttribute() {
		return attribute;
	}

	public void setAttribute(byte attribute) {
		this.attribute = attribute;
	}

	public byte getStartNum() {
		return startNum;
	}

	public void setStartNum(byte startNum) {
		this.startNum = startNum;
	}

	public byte getSize() {
		return size;
	}

	public void setSize(byte size) {
		this.size = size;
	}


}
