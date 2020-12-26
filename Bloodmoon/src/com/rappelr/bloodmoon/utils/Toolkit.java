package com.rappelr.bloodmoon.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import com.rappelr.bloodmoon.Bloodmoon;

public class Toolkit {

	public static Player toPlayer(final HumanEntity e) {
		return Bukkit.getPlayer(e.getName());
	}
	
	public static void scheduleSync(final Runnable b) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bloodmoon.getInstance(), b, 1l);
	}
}
