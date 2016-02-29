package com.programmerdan.minecraft.devotion.dao.info;

import org.bukkit.block.Block;

public class BlockInfo {
	public String blockType;
	public Integer x;
	public Integer y;
	public Integer z;
	
	public BlockInfo() {
	}
	
	public BlockInfo(Block block) {
		if(block != null) {
			this.blockType = block.getType().name();
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
		}
	}
}
