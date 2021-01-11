package com.rappelr.bloodmoon.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.rappelr.bloodmoon.Bloodmoon;

import lombok.Getter;

public abstract class Hook implements Listener {
	
	private final String plugin;
	
	@Getter
	private boolean present;
	
	public Hook(String plugin) {
		this.plugin = plugin;
		
		attempt();
		
		if(present)
			Bukkit.getPluginManager().registerEvents(this, Bloodmoon.getInstance());
	}
	
	public boolean attempt() {
		present = Bloodmoon.getInstance().getServer().getPluginManager().getPlugin(plugin) != null;
		return present;
	}

}
