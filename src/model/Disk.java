package model;

import java.util.List;

public class Disk {
	 private byte[][] storageBlocks = new byte[128][];
	
	public Disk() {
		for (int i = 0; i < 128; i++) {
            storageBlocks[i] = new byte[64];
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
    
    
	
}
