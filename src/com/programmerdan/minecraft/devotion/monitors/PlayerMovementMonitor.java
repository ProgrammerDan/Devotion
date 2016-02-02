package com.programmerdan.minecraft.devotion.monitors;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.programmerdan.minecraft.devotion.DataHandler;
import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.Monitor;
import com.programmerdan.minecraft.devotion.config.PlayerMovementMonitorConfig;
import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.flyweight.PlayerFactory;

public class PlayerMovementMonitor extends Monitor implements Listener {

	private ConcurrentLinkedQueue<UUID> playersToMonitor;
	private ConcurrentHashMap<UUID,Boolean> playersToRemove;
	private PlayerMovementMonitorThread asynch;
	
	private boolean onlyAsynch;
	private boolean onlyEvent;
	
	private ConcurrentHashMap<UUID, Long> lastMovementSample;
	
	private PlayerMovementMonitorConfig config;
	
	public PlayerMovementMonitor(PlayerMovementMonitorConfig config) {
		this.config = config;
	}

	@Override
	public void onEnable() {
		if (super.isEnabled()) {
			return;
		}
		
		playersToMonitor = new ConcurrentLinkedQueue<UUID>();
		playersToRemove = new ConcurrentHashMap<UUID, Boolean>(5000, .75f, 5); // large pre-claimed space, default load factor, est. 5 concurrent threads
		
		onlyAsynch = !SamplingMethod.onevent.equals(config.technique); // Onevent is only non-asynch sampling technique.
		if (!onlyAsynch) {
			lastMovementSample = new ConcurrentHashMap<UUID, Long>(5000, .75f, 5);
			onlyEvent = true;
		}
		
		Devotion.instance().getServer().getPluginManager().registerEvents(this, Devotion.instance());

		super.setEnabled(true);
	}

	@Override
	public void onDisable() {
		if (!super.isEnabled()) {
			return;
		}

		// TODO: any teardown. No need to unregister listener.

		super.setEnabled(false);
		
		if (playersToMonitor != null) playersToMonitor.clear();
		if (playersToRemove != null) playersToRemove.clear();
		if (lastMovementSample != null) lastMovementSample.clear();
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerLogin(PlayerLoginEvent event) {
		insert(event);
		checkAdd(event.getPlayer().getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerJoin(PlayerJoinEvent event) {
		insert(event);
		checkAdd(event.getPlayer().getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerQuit(PlayerQuitEvent event) {
		insert(event);
		setRemove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (onlyAsynch) return;
		
		if (onlyEvent) {
			UUID p = event.getPlayer().getUniqueId();
			if (config.timeoutBetweenSampling <= 0) { // bypass throttling by setting timeout to 0.
				insert(event);
			} else {
				Long lastSample = lastMovementSample.get(p);
				long timePassed = lastSample != null ? System.currentTimeMillis() - lastSample: config.timeoutBetweenSampling;
				
				if (timePassed < config.timeoutBetweenSampling) return;
				
				insert(event);
				lastMovementSample.put(p, System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		insert(event);
	}
	
	/**
	 * Called by MonitorThread, triggers a periodic sampling process
	 * 
	 * This might not be safe to call the Bukkit function. Trying it, we can recode if unsafe.
	 */
	protected void doSample() {
		int samples = 0;
		UUID start = playersToMonitor.poll();
		UUID now = start;
		while (now != null && samples <= config.sampleSize) {
			Player p = Bukkit.getPlayer(now);
			if (p != null) {
				insert(new PlayerMoveEvent(p, new Location(null, 0, 0, 0), new Location(null, 0, 0, 0)));
			}
			samples++;
			checkRemove(now); // put current samplee back on the list.
			now = playersToMonitor.poll();
			if (start.equals(now)) break; // we've sampled everyone
		}
		checkRemove(start); // put first person back on the list.
	}
	
	private void insert(PlayerEvent event) {
		Flyweight flyweight = PlayerFactory.create(event);
		
		for(DataHandler handler : Devotion.instance().getHandlers()) {
			handler.insert(flyweight);
		}
	}
	
	/**
	 * Approx O(1) check to see if we should be skipping this player.
	 * 
	 * @param player
	 */
	private void checkRemove(UUID player) {
		Boolean remStat = playersToRemove.get(player);
		if (remStat != null) {
			playersToRemove.put(player, Boolean.FALSE);
		} else {
			playersToMonitor.add(player);
		}
		
	}
	
	/**
	 * Use on player login. 
	 * If in remove list, and true, removes from remove list. (removal scheduled but hasn't happened)
	 * If in remove list, and false, removes from remove list and adds to check list. (removal happened)
	 * If not in remove list, adds to check list.
	 * @param player
	 */
	private void checkAdd(UUID player) {
		Boolean remStat = playersToRemove.remove(player);
		if (remStat == null || !remStat.booleanValue()) {
			playersToMonitor.add(player);
		}
	}
	
	/**
	 * Use on player quit/kick/disconnect
	 * Adds player to remove list, with flag TRUE -- scheduled but hasn't happened
	 * @param player
	 */
	private void setRemove(UUID player) {
		playersToRemove.put(player, Boolean.TRUE);
	}
}
