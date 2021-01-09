package com.rappelr.bloodmoon.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;

import net.md_5.bungee.api.ChatColor;

public class Toolkit {

	public static Player toPlayer(final HumanEntity e) {
		return Bukkit.getPlayer(e.getName());
	}
	
	public static void scheduleSync(final Runnable b) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bloodmoon.getInstance(), b, 1l);
	}
	
	public static String stripExtension(String filename) {
		final int index = filename.lastIndexOf(".");
		return index == -1 ? filename : filename.substring(0, index);
	}
	
	public static String formatString(String string) {
		return string.toLowerCase().replace(" ", "_");
	}
	
	public static String formName(ItemStack item) {
		if(item.hasItemMeta())
			if(item.getItemMeta().hasDisplayName())
				return formatString(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
			else if(item.getItemMeta().hasLocalizedName())
				return formatString(ChatColor.stripColor(item.getItemMeta().getLocalizedName()));
		return formatString(item.getType().name());
	}
	
	public static float parseOr(String string, float alternative) {
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			return alternative;
		}
	}
}
