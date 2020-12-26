package com.rappelr.bloodmoon.effect;

import java.util.List;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarEffect {
	
	private final BossBar bossbar;
	
	{
		bossbar = null;//Bukkit.createBossBar(title, color, style, flags)
	}
	
	public void show(final List<Player> players) {
		players.forEach(bossbar::addPlayer);
	}
	
	public void clear() {
		bossbar.removeAll();
	}
	
	public void update(final double percentile) {
		bossbar.setProgress(percentile);
	}
	
	public void add(final Player player) {
		bossbar.addPlayer(player);
	}
	
	public void remove(final Player player) {
		if(bossbar.getPlayers().contains(player))
			bossbar.removePlayer(player);
	}

}
