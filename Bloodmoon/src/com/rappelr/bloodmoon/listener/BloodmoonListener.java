package com.rappelr.bloodmoon.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.world.BloodmoonWorld;
import com.rappelr.bloodmoon.world.WorldManager;

public class BloodmoonListener implements Listener {
	
	private final WorldManager worlds = Bloodmoon.getInstance().getWorldManager();
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		BloodmoonWorld world = worlds.by(e.getEntity().getWorld());
		
		if(world.isEnabled())
			world.onPlayerDeath(e, world.getClock().isBloodmoon());
	}

	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		BloodmoonWorld world = worlds.by(e.getEntity().getWorld());
		
		if(world.isEnabled())
			world.onMobDeath(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		BloodmoonWorld world = worlds.by(e.getEntity().getWorld());
		
		if(world.isEnabled())
			if(e.getDamager() instanceof Player)
				world.onPlayerDamage(e, world.getClock().isBloodmoon());
			else if(e.getEntity() instanceof Player)
				world.onMobDamage(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onMobSpawn(EntitySpawnEvent e) {
		BloodmoonWorld world = worlds.by(e.getEntity().getWorld());
		
		if(world.isEnabled())
			world.onMobSpawn(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onPlayerSleep(PlayerBedEnterEvent e) {
		BloodmoonWorld world = worlds.by(e.getPlayer().getWorld());
		
		if(world.isEnabled())
			world.onPlayerSleep(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		BloodmoonWorld world = worlds.by(e.getPlayer().getWorld());
		
		if(world.isEnabled())
			world.onPlayerJoin(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		BloodmoonWorld world = worlds.by(e.getPlayer().getWorld());
		
		if(world.isEnabled())
			world.onPlayerLeave(e, world.getClock().isBloodmoon());
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		BloodmoonWorld world = worlds.by(e.getPlayer().getWorld());
		
		if(world.isEnabled())
			world.onPlayerRespawn(e, world.getClock().isBloodmoon());
	}


}
