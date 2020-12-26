package com.rappelr.bloodmoon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.rappelr.bloodmoon.command.CommandExecutor;
import com.rappelr.bloodmoon.config.ConfigCore;
import com.rappelr.bloodmoon.listener.BloodmoonListener;
import com.rappelr.bloodmoon.world.WorldManager;

import lombok.Getter;

public class Bloodmoon extends JavaPlugin {
	
	@Getter private static Bloodmoon instance;
	
	@Getter private WorldManager worldManager;
	
	@Getter private ConfigCore configuration;
	
	{
		instance = this;
	}

	@Override
    public void onEnable() {
		
		configuration = new ConfigCore("config.yml", this, true);
		
		worldManager = new WorldManager();
		
		Bukkit.getPluginManager().registerEvents(new BloodmoonListener(), this);
		
		getCommand("bloodmoon").setExecutor(new CommandExecutor());
		
	}
	
	public void reload() {
		worldManager.reload();
	}
	
}