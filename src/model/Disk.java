package model;

import java.util.List;

public class Disk {
	public static final int totalBlock = 128;
	public static final int blockSize = 64;
	
	private byte[][] storageBlocks = new byte[this.totalBlock][];
	
	public Disk() {
		for (int i = 0; i < this.totalBlock; i++) {
            storageBlocks[i] = new byte[this.blockSize];
            
        }
	}
	
	public Disk(byte[] byteArray) {
		this.storageBlocks = new byte[this.totalBlock][this.blockSize];
		for(int i = 0; i<byteArray.length; i++) {
			this.storageBlocks[i/64][i%64] = byteArray[i];
		}
	}
	
	// TODO load disk from file
	
	/**
     * 存储一个完整的块
     *
     * @param index 位置
     * @param block 一个完整的块
     */
    public void setBlock(int index, byte[] singleBlock) {
        storageBlocks[index] = singleBlock;
    }
	
	/**
     * 返回整个磁盘数组
     *
     * @return the blocks
     */
    public byte[][] getDisk() {
        return storageBlocks;
    }
    
    /**
     * 返回指定的盘块
     *
     * @return the block
     */
    public byte[] getBlock(int index) {
        return storageBlocks[index];
    }
    
    public byte[] getDiskArray() {
    	byte[] array = new byte [this.totalBlock * this.blockSize];
    	int i = 0;
    	for (byte[] bs : this.storageBlocks) {
			for (byte b : bs) {
				array[i++] = b;
			}
		}
		return array;
    	
    }
    
    
	
}
