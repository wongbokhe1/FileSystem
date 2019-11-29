package model;


public interface DirItemInterface {
	public static final byte DIR = 8;
	public static final byte FILE = 4;
	public static final byte SYS_FILE = 2;
	public static final byte READONLY = 1;
	
	
	
	public byte getAttribute();
	public String getName();
	public String getType();
	public byte getSize();
	public byte getStartBlock();
	public void setName(String name) throws Exception;
	public void setAttribute(byte attribute) throws Exception;
	public void setType(String type) throws Exception;
	public void setSize(byte size);
	public void setStartBlock(byte startBlock);

}
