package com.rappelr.bloodmoon.effect;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.world.BloodmoonWorld;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BossBarEffect {
	
	private final BloodmoonWorld world;
	
	private BossBar bossbar;
	
	public void setup() {
		if(bossbar != null)
			clear();
		
		if(world.isDarkenSky()) {
			bossbar = Bukkit.createBossBar("Bloodmoon",
                    BarColor.RED,
                    BarStyle.SEGMENTED_12,
                    BarFlag.CREATE_FOG,
                    BarFlag.DARKEN_SKY
            );
		} else {
			bossbar = Bukkit.createBossBar("Bloodmoon",
                    BarColor.RED,
                    BarStyle.SEGMENTED_12
            );
		}
		
		reset();
	}
	
	public void reset() {
		if(bossbar != null)
			bossbar.setProgress(world.isPermanent() ? 1d : 0d);
	}
	
	public void update() {
		if(world.isPermanent() || bossbar == null)
			return;
		
		bossbar.setProgress(world.getClock().process());
	}
	
	public void show() {
		if(bossbar != null)
			world.getWorld().getPlayers().forEach(bossbar::addPlayer);
	}
	
	public void clear() {
		if(bossbar != null)
			bossbar.removeAll();
	}
	
	public void add(final Player player) {
		if(bossbar != null)
			bossbar.addPlayer(player);
	}
	
	public void remove(final Player player) {
		if(bossbar != null && bossbar.getPlayers().contains(player))
			bossbar.removePlayer(player);
	}

}
