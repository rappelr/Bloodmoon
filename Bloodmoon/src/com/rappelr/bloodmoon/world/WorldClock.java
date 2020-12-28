package com.rappelr.bloodmoon.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.utils.Toolkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
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
				() -> clocks.forEach(c -> c.tick()), 1l, SUPER_INTERVAL);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bloodmoon.getInstance(), 
				() -> clocks.forEach(c -> c.saveToCache()), 1l, STORE_INTERVAL);
	}
	
	void load() {
		val cache = world.getCache();
		lastBloodmoon = cache.getLastBloodmoon();
		lastNight = cache.getLastNight();
		isBloodmoon = cache.isBloodmoon();

		Bukkit.getLogger().info("Loaded cache, lastBloodmoon: " + lastBloodmoon + " lastNight: " + lastNight);

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
	
	void register() {
		WorldClock.clocks.add(this);
	}

	void unregister() {
		if(WorldClock.clocks.contains(this))
			WorldClock.clocks.remove(this);
	}
	
	private void tick() {
		checkNight();
		
		if(isBloodmoon)
			listener.tick();
	}
	
	private void checkNight() {
		if(world.isNight() && lastNight > 0) {
			if(lastNight > MINIMUM_CHECK_INTERVAL)
				onNight();
			if(!isBloodmoon)
				lastNight = 0l;
		}
		
		lastNight += SUPER_INTERVAL;

		if(isBloodmoon && lastNight >= 0)
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
	
	void saveToCache() {
		world.setCache(lastBloodmoon, lastNight);
		Bukkit.getLogger().info("Saved cache, lastBloodmoon: " + lastBloodmoon + " lastNight: " + lastNight);
	}
	
	public int daysLeft() {
		return lastBloodmoon >= world.getInterval() ? 0 : world.getInterval() - lastBloodmoon;
	}
	
	public static void clear() {
		clocks.clear();
	}

	public double process() {
		if(!isBloodmoon)
			return 0d;
		
		return (0d - lastNight) / world.getDuration();
	}

}

interface WorldClockListener {
	
	public void onDayBefore();
	
	public void onStart();
	
	public void onContinue();
	
	public void onEnd();
	
	public void tick();
	
}
