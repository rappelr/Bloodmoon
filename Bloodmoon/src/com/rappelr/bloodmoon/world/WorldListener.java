package com.rappelr.bloodmoon.world;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

public interface WorldListener {
	
	public void onPlayerDeath(PlayerDeathEvent event, boolean active);
	
	public void onMobDeath(EntityDeathEvent event, boolean active);
	
	public void onPlayerDamage(EntityDamageByEntityEvent event, boolean active);
	
	public void onMobDamage(EntityDamageByEntityEvent event, boolean active);
	
	public void onMobSpawn(EntitySpawnEvent event, boolean active);
	
	public void onPlayerSleep(PlayerBedEnterEvent event, boolean active);

}
