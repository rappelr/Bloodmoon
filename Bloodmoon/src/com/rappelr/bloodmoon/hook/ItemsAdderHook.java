package com.rappelr.bloodmoon.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;

public class ItemsAdderHook extends Hook {
	
	public ItemsAdderHook() {
		super("ItemsAdder");
		
		if(isPresent())
			Bukkit.getLogger().info("[Bloodmoon] Waiting for ItemsAdder to finish loading...");
		else
			Bukkit.getLogger().info("[Bloodmoon] ItemsAdder not detected, dependant features disabled");
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
