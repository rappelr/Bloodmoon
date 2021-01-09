package com.rappelr.bloodmoon.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;
import lombok.Getter;

public class ItemsAdderHook implements Listener {
	
	@Getter
	private boolean present;
	
	{
		present = Bloodmoon.getInstance().getServer().getPluginManager().getPlugin("ItemsAdder") != null;
		if(!present)
			Bukkit.getLogger().info("[Bloodmoon] ItemsAdder not detected, dependant features disabled");
		else {
			Bukkit.getPluginManager().registerEvents(this, Bloodmoon.getInstance());
			Bukkit.getLogger().info("[Bloodmoon] Waiting for ItemsAdder to finish loading...");
		}
	}

	public boolean isCustom(ItemStack item) {
		try {
			return ItemsAdder.isCustomItem(item);
		} catch(NoClassDefFoundError e) {
			return false;
		}
	}

	public String getName(ItemStack item) {
		try {
			return ItemsAdder.getCustomItemName(item);
		} catch(NoClassDefFoundError e) {
			return null;
		}
	}

	public ItemStack getItemStack(String name) {
		try {
			return ItemsAdder.getCustomItem(name);
		} catch(NoClassDefFoundError e) {
			return null;
		}
	}
	
	@EventHandler
	public void onLoad(ItemsAdderFirstLoadEvent e) {
		Bukkit.getLogger().info("[Bloodmoon] ItemsAdder load complete, reloading");
		Bloodmoon.getInstance().reload(true);
	}
}
