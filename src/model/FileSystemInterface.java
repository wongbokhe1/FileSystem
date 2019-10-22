package model;

public interface FileSystemInterface {
	public static final byte CLOSED = 0;
	public static final byte READ = 1;
	public static final byte WRITE = 2;
	public static final byte ROOT = 2;
	
	public static byte[] buffer1 = new byte[64];
	public static byte[] buffer2 = new byte[64];
	
	public DirItem createFile(String name, String type, byte attribute, String path, int index) throws Exception;
	public DirItem createDir(String name, byte attribute, String path, int index) throws Exception;
	public DirItem createFile(String name, String type, byte attribute, DirItem paretent, int index) throws Exception;
	public DirItem createDir(String name, byte attribute, DirItem paretent, int index) throws Exception;
	public void deleteDir(DirItem item) throws Exception;
	public byte[] open(DirItem item, byte mode) throws Exception;
	public void close(DirItem item, byte[] buffer) throws Exception;
	public byte[] read(DirItem item, byte mode) throws Exception;
	public void write(DirItem item, byte[] buffer) throws Exception;
	public void delete(DirItem item) throws Exception;
	public void modify(DirItem itme, byte attribute) throws Exception;
 	public DirItem[] getFileTree() throws Exception;
	public DirItem[] getFileTree(DirItem item) throws Exception;
	public DirItem[] getFileTree(String path) throws Exception;
}
