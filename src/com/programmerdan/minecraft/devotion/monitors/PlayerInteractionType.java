package com.programmerdan.minecraft.devotion.monitors;

public enum PlayerInteractionType {
	PlayerInteractEvent(0),
	PlayerBedEnterEvent(1),
	PlayerBedLeaveEvent(2),
	PlayerBucketEvent(3),
	PlayerDropItemEvent(4),
	PlayerEditBookEvent(5),
	PlayerEggThrowEvent(6),
	PlayerExpChangeEvent(7),
	PlayerFishEvent(8),
	PlayerGameModeChangeEvent(9),
	PlayerInteractEntityEvent(10),
	PlayerInventoryEvent(11),
	PlayerItemBreakEvent(12),
	PlayerItemConsumeEvent(13),
	PlayerItemHeldEvent(14),
	PlayerLevelChangeEvent(15),
	PlayerPickupItemEvent(16),
	PlayerResourcePackStatusEvent(17),
	PlayerShearEntityEvent(18),
	PlayerStatisticIncrementEvent(19);
	
	public static final int MAX_IDX = 19; // UPDATE THIS if you alter the above
	public static final int SIZE = 20; // UPDATE THIS if you alter the above.
	
	private int idx;
	
	PlayerInteractionType(int idx) {
		this.idx = idx;
	}
	
	public int getIdx() {
		return idx;
	}
}
