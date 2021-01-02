package com.rappelr.bloodmoon.world.cache;

import org.bukkit.Bukkit;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.Configuration;
import com.rappelr.bloodmoon.world.BloodmoonWorld;

import lombok.val;

public class WorldCache {
	
	private final Configuration config;
	
	{
		config = new Configuration(".cache.yml", Bloodmoon.getInstance(), true);
	}
	
	public void load() {
		config.load();
	}

	public Entry get(BloodmoonWorld world) {
		if(!config.contains(world.getWorld().getName())) {
			Bukkit.getLogger().info("Cache for \"" + world.getWorld().getName() + "\" was not found");
			val entry = Entry.of(world);
			set(world.getWorld().getName(), entry.getLastBloodmoon(), entry.getLastNight());
			return entry;
		}
		Bukkit.getLogger().info("Found cache for \"" + world.getWorld().getName() + "\"");
		return Entry.of(config.getSource().getString(world.getWorld().getName()));
	}

	public void set(String name, int lastBloodMoon, long lastNight) {
		config.set(name, new Entry(lastNight < 0, lastBloodMoon, lastNight).toString());
		config.save();
	}

}