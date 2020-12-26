package com.rappelr.bloodmoon.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.effect.WorldBorderEffect;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public class WorldManager {
	
	@Getter(AccessLevel.PACKAGE)
	private final WorldBorderEffect worldBorderEffect;
	
	@Getter
	private final List<BloodmoonWorld> worlds;

	@Getter(AccessLevel.PACKAGE)
	private long startTime, endTime;
	
	{
		loadTimes();
		
		worldBorderEffect = new WorldBorderEffect();
		worlds = new ArrayList<BloodmoonWorld>();
		
		Bukkit.getWorlds().forEach(this::register);
	}
	
	public void reload() {
		loadTimes();
		worlds.forEach(w -> w.loadConfig());
	}
	
	public BloodmoonWorld by(@NonNull World world) {
		val bloodmoonWorld = get(world);
		
		if(bloodmoonWorld == null)
			return register(world);
		
		return bloodmoonWorld;
	}
	
	public BloodmoonWorld by(@NonNull String name) {
		val world = Bukkit.getWorld(name);
		
		if(world == null)
			return null;
		
		return by(world);
	}
	
	public BloodmoonWorld by(@NonNull Player player) {
		return by(player.getWorld());
	}
	
	private BloodmoonWorld register(World world) {
		val bloodmoonWorld = new BloodmoonWorld(world);
		worlds.add(bloodmoonWorld);
		return bloodmoonWorld;
	}
	
	private BloodmoonWorld get(World world) {
		for(BloodmoonWorld bloodmoonWorld : worlds)
			if(bloodmoonWorld.getWorld().equals(world))
				return bloodmoonWorld;
		return null;
	}
	
	private void loadTimes() {
		startTime = Bloodmoon.getInstance().getConfiguration().getSource().getLong("world-time.during", 13300l);
		endTime = Bloodmoon.getInstance().getConfiguration().getSource().getLong("world-time.final", 23000l);
	}

}
