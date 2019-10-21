package model;

public class FAT {
	
	public static final byte USED = -1;
	public static final byte EMPTY = 0;
	
	private int vacantCount = 126;			//空闲盘块数
	private byte[][] table = new byte[2][];	//定义FAT表
    
	
	/*
	 * FAT 初始化
	 */
    public FAT() {
    	table[0] = new byte[64];
        table[1] = new byte[64];
        table[0][0] = FAT.USED;		//255表示盘块已被占用
        table[0][1] = FAT.USED;		//系统占用第0块和第1块
        table[0][2] = FAT.USED;
        for (int i = 3; i < table[0].length; i++) {
            table[0][i] = FAT.EMPTY;	//0表示盘块空闲
        }
        for (int i = 0; i < table[1].length; i++) {
            table[1][i] = FAT.EMPTY;
        }
        this.vacantCount = 126;
	}
    
    // TODO load existent fat
    
    /**
     * 设置某块的值
     *
     * @param index 哪一块
     * @param value 设定的值
     */
    public void setTable(int index, byte value) {
    	// TODO 逻辑有误
        if (index < 64) {
            table[0][index] = value;
            this.vacantCount--;
        } else if(index < 128){
            table[1][index] = value;
            this.vacantCount--;
        }
        else {
        	System.out.println("Index out of range!");
        }
    }
    
    public byte[][] getTable() {
		return table;
	}
    public byte getLocation(byte index) {
    	return this.table[index / 64][index % 64];
    }

    public int getVacantCount() {
		return vacantCount;
	}

	public void setVacantCount(int vacantCount) {
		this.vacantCount = vacantCount;
	}

	/**
     * 获取一个空的FAT位置
     *
     * @return
     * @throws Exception
     */
    public byte getEmptyLocation()throws Exception{
    	//因为系统占用了第0块和第1块，因此从第2块开始查询
        for (byte i = 2; i < table[0].length; i++) {
            if (table[0][i] == FAT.EMPTY) {
                return i;
            }
        }
        for (byte i = 0; i < table[1].length; i++) {
            if (table[1][i] == FAT.EMPTY) {
                return (byte) (i + 64);
            }
        }
        throw new Exception("error:FAT is full!");
    }
    
    
 
	
    
}
