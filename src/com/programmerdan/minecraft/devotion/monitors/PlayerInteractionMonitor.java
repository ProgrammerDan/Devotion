package com.programmerdan.minecraft.devotion.monitors;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.config.PlayerInteractionMonitorConfig;
import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.flyweight.FlyweightFactory;

/**
 * Player Interaction Monitor -- tracks interactions with the MC world.
 * 
 * Current:
 * <ul>
 *     <li>PlayerInteractEvent</li>
 *     <li>PlayerBedEnterEvent</li>
 *     <li>PlayerBedLeaveEvent</li>
 *     <li>PlayerBucketEvent</li>
 * </ul>
 * 
 * TODO:
 * <ul>
 *     <li>PlayerDropItemEvent</li>
 *     <li>PlayerEditBookEvent</li>
 *     <li>PlayerEggThrowEvent</li>
 *     <li>PlayerExpChangeEvent</li>
 *     <li>PlayerFishEvent</li>
 *     <li>PlayerGameModeChangeEvent</li>
 *     <li>PlayerInteractEntityEvent</li>
 *     <li>PlayerInventoryEvent</li>
 *     <li>PlayerItemBreakEvent</li>
 *     <li>PlayerItemConsumeEvent</li>
 *     <li>PlayerItemHeldEvent</li>
 *     <li>PlayerLevelChangeEvent</li>
 *     <li>PlayerPickupItemEvent</li>
 *     <li>PlayerResourcePackStatusEvent</li>
 *     <li>PlayerShearEntityEvent</li>
 *     <li>PlayerStatisticIncrementEvent</li>
 * 	   <li>PlayerDeathEvent</li>
 * </ul>
 * 
 * TODO: extract inventory events into PlayerInventoryMonitor
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @author Aleksey Terzi
 */
public class PlayerInteractionMonitor extends Monitor implements Listener {

	private PlayerInteractionMonitorConfig config;
	
	/**
	 * Records the 
	 */
	private ConcurrentHashMap<UUID, long[]> lastCapture;
	
	private boolean checkInsert(UUID player, PlayerInteractionType pit) {
		if (!checkDelay) {
			return true;
		} else {
			long[] captureTimes = lastCapture.get(player);
			if (captureTimes == null) {
				captureTimes = new long[PlayerInteractionType.SIZE];
				lastCapture.put(player, captureTimes);
			}
			long now = System.currentTimeMillis();
			boolean res = (now - captureTimes[pit.getIdx()]) > config.delayBetweenSamples;
			captureTimes[pit.getIdx()] = now;
			return res;
		}
	}
	
	private boolean checkDelay = true; 
	
	protected PlayerInteractionMonitorConfig getConfig() {
		return config;
	}
	
	private PlayerInteractionMonitor(PlayerInteractionMonitorConfig config) {
		this.config = config;
	}
	
	public static PlayerInteractionMonitor generate(ConfigurationSection config) {
		if (config == null) return null;
		PlayerInteractionMonitorConfig pimc = new PlayerInteractionMonitorConfig();
		pimc.delayBetweenSamples = config.getLong("sampling_delay", 10l);
		PlayerInteractionMonitor pim = new PlayerInteractionMonitor(pimc);
		pim.setDebug(config.getBoolean("debug", Devotion.instance().isDebug()));
		return pim;
	}
	
	@Override
	public void onEnable() {
		if (super.isEnabled()) {
			return;
		}
		
		if (config.delayBetweenSamples > 0l) {
			checkDelay = true;
			lastCapture = new ConcurrentHashMap<UUID, long[]>(5000, .75f, 5); // large pre-claimed space, default load factor, est. 5 concurrent threads
		}else {
			checkDelay = false;
		}
		
		Devotion.instance().getServer().getPluginManager().registerEvents(this, Devotion.instance());

		super.setEnabled(true);
	}

	@Override
	public void onDisable() {
		if (!super.isEnabled()) {
			return;
		}

		super.setEnabled(false);
		
		if (lastCapture != null) lastCapture.clear();
	}

	@Override
	void doSample() {
		// Currently unused for this monitor.
	}

	/**
	 * Quickly create a flyweight and pass it along to the active handlers.
	 * @param event
	 */
	private void insert(PlayerEvent event) {
		Flyweight flyweight = FlyweightFactory.create(event);
		
		Devotion.instance().insert(flyweight);
	}
	
	/**
	 * Follow this pattern. Each new monitor uses checkInsert which sees if it's time to update.
	 * If no delay is set, it retuns fast with "true", else checks the last time a record was made ...
	 * see {@link #checkInsert(UUID, PlayerInteractionType)}
	 *  
	 * @param event the Interaction Event.
	 */
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (checkInsert(event.getPlayer().getUniqueId(), PlayerInteractionType.PlayerInteractEvent)) {
			insert(event);
		} // else skip.
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		if (checkInsert(event.getPlayer().getUniqueId(), PlayerInteractionType.PlayerBedEnterEvent)) {
			insert(event);
		} // else skip.
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
		if (checkInsert(event.getPlayer().getUniqueId(), PlayerInteractionType.PlayerBedLeaveEvent)) {
			insert(event);
		} // else skip.
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if (checkInsert(event.getPlayer().getUniqueId(), PlayerInteractionType.PlayerBucketFillEvent)) {
			insert(event);
		} // else skip.
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerBucketFill(PlayerBucketEmptyEvent event) {
		if (checkInsert(event.getPlayer().getUniqueId(), PlayerInteractionType.PlayerBucketEmptyEvent)) {
			insert(event);
		} // else skip.
	}
}