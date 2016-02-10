package com.programmerdan.minecraft.devotion.monitors;

public enum PlayerInteractionType {
	PlayerInteractEvent(0),
	PlayerBedEnterEvent(1),
	PlayerBedLeaveEvent(2),
	PlayerBucketFillEvent(3),
	PlayerBucketEmptyEvent(4),
	PlayerDropItemEvent(5),
	PlayerEditBookEvent(6),
	PlayerEggThrowEvent(7),
	PlayerExpChangeEvent(8),
	PlayerFishEvent(9),
	PlayerGameModeChangeEvent(10),
	PlayerInteractEntityEvent(11),
	PlayerInventoryEvent(12),
	PlayerItemBreakEvent(13),
	PlayerItemConsumeEvent(14),
	PlayerItemHeldEvent(15),
	PlayerLevelChangeEvent(16),
	PlayerPickupItemEvent(17),
	PlayerResourcePackStatusEvent(18),
	PlayerShearEntityEvent(19),
	PlayerStatisticIncrementEvent(20),
	PlayerDeathEvent(21);
	
	public static final int MAX_IDX = 21; // UPDATE THIS if you alter the above
	public static final int SIZE = 22; // UPDATE THIS if you alter the above.
	
	private int idx;
	
	PlayerInteractionType(int idx) {
		this.idx = idx;
	}
	
	public int getIdx() {
		return idx;
	}
}