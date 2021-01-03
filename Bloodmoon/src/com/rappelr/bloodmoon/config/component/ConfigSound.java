package com.rappelr.bloodmoon.config.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigSound {
	
	private final String sound;
	
	private final float volume, pitch;
	
	public static ConfigSound of(String string) {
		if(string == null || string.isEmpty())
			return null;
		
		val split = string.split(";");
		
		if(split.length == 1)
			return new ConfigSound(split[0], 1f, 1f);
		
		if(split.length == 2)
			return new ConfigSound(split[0], Float.parseFloat(split[1]), 1f);
		
		if(split.length >= 3)
			return new ConfigSound(split[0], Float.parseFloat(split[1]), Float.parseFloat(split[2]));
		
		return null;
	}
	
	public static ConfigSound[] of(List<String> strings) {
		if(strings == null)
			return null;
		
		val result = new ArrayList<ConfigSound>();
		strings.forEach(s -> result.add(ConfigSound.of(s)));
		result.removeIf(Objects::isNull);
		
		return result.toArray(new ConfigSound[0]);
	}
	
	public void playAll() {
		Bukkit.getOnlinePlayers().forEach(this::play);
	}
	
	public void playAll(World world) {
		world.getPlayers().forEach(this::play);
	}
	
	public void play(Player player) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	public void play(Location location) {
		location.getWorld().playSound(location, sound, volume, pitch);
	}

}
