package com.rappelr.bloodmoon.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.utils.Toolkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class WorldClock {
	
	private static final long SUPER_INTERVAL = 20l, MINIMUM_CHECK_INTERVAL = 200l, STORE_INTERVAL = 1200l;
	
	private static List<WorldClock> clocks;

	private final BloodmoonWorld world;
	
	@Setter(AccessLevel.PACKAGE) private WorldClockListener listener;
	
	@Getter private int lastBloodmoon;
	
	@Getter private boolean isBloodmoon;
	
	private long lastNight;
	
	static {
		clocks = new ArrayList<WorldClock>();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bloodmoon.getInstance(), 
				() -> clocks.forEach(c -> c.checkNight()), 1l, SUPER_INTERVAL);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bloodmoon.getInstance(), 
				() -> clocks.forEach(c -> c.saveToCache()), 1l, STORE_INTERVAL);
	}
	
	WorldClock(BloodmoonWorld world) {
		this.world = world;
		
		val cache = world.getCache();
		lastBloodmoon = cache.getLastBloodmoon();
		lastNight = cache.getLastNight();
		isBloodmoon = cache.isBloodmoon();
		
		WorldClock.clocks.add(this);

		if(isBloodmoon) {
			if(lastNight < 0)
				Toolkit.scheduleSync(() -> {
					if(listener != null)
						listener.onContinue();
				});
			else {
				Bukkit.getLogger().info(ChatColor.RED + "Bloodmoon cache corrupted for world " + world.getWorld().getName() + ", repairing");
				Toolkit.scheduleSync(() -> onBloodmoonEnd());
			}
		}

	}

	void disband() {
		if(WorldClock.clocks.contains(this))
			WorldClock.clocks.remove(this);
	}
	
	private void checkNight() {
		if(world.isNight() && lastNight > 0) {
			if(lastNight > MINIMUM_CHECK_INTERVAL)
				onNight();
			if(!isBloodmoon)
				lastNight = 0l;
		}
		
		lastNight += SUPER_INTERVAL;
		val posNight = lastNight >= 0;

		if(isBloodmoon && posNight)
			onBloodmoonEnd();
	}
	
	private void onNight() {
		lastBloodmoon++;

		if(lastBloodmoon == world.getInterval() - 1)
			if(listener != null)
				listener.onDayBefore();
		
		if(lastBloodmoon >= world.getInterval())
			onBloodmoonStart();
	}
	
	void onBloodmoonStart() {
		lastBloodmoon = 0;
		isBloodmoon = true;
		lastNight = -world.getDuration();
		if(listener != null)
			listener.onStart();
		
		saveToCache();
	}

	void onBloodmoonEnd() {
		lastNight = 0;
		isBloodmoon = false;
		if(listener != null)
			listener.onEnd();
		
		saveToCache();
	}
	
	public int daysLeft() {
		return lastBloodmoon >= world.getInterval() ? 0 : world.getInterval() - lastBloodmoon;
	}
	
	public static void clear() {
		clocks.clear();
	}
	
	private void saveToCache() {
		world.setCache(lastBloodmoon, lastNight);
	}

}

interface WorldClockListener {
	
	public void onDayBefore();
	
	public void onStart();
	
	public void onContinue();
	
	public void onEnd();
	
}
