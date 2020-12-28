package com.rappelr.bloodmoon.effect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.Bloodmoon;

import lombok.val;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;

public class WorldBorderEffect {
	
	private static final long PHASE_SPEED = 6l, GENERAL_SPEED = 200l;
	private static final int STAGES = 10, FINAL_FACTOR = 20;
	private static final double FACTOR = Math.log(FINAL_FACTOR) / Math.log(STAGES);
	 
	private final class Phase {
		
		private final Player player;
		private final int scale, stable;
		private int stage;
		
		private Phase(final Player player) {
			this.player = player;
			scale = (int) Math.floor(player.getWorld().getWorldBorder().getSize()) / 2;
			stable = scale * FINAL_FACTOR;
			stage = -STAGES;
		}
		
		private boolean isStable() {
			return stage == 0;
		}
		
		private void drain() {
			stage++;
		}
		
		private boolean isDrained() {
			return stage > STAGES + 1;
		}
		
		private int phase() {
			int result = 0;
			
			if(stage < 0)
				result = (int) (scale * Math.pow(FACTOR, STAGES + stage));
			else if (stage > 0 && stage <= STAGES)
				result = (int) (scale * Math.pow(FACTOR, STAGES - stage));
			
			stage++;

			return result;
		}
		
	}
	
	private final List<Phase> list;
	
	{
		list = new ArrayList<WorldBorderEffect.Phase>();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bloodmoon.getInstance(),
				() -> list.removeIf(this::updatePhase),
				1l, PHASE_SPEED);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bloodmoon.getInstance(),
				() -> list.forEach(this::updateGeneral),
				GENERAL_SPEED, GENERAL_SPEED);
	}
	
	public void add(Player player) {
		if(list.stream().anyMatch(p -> p.player.equals(player)))
			return;
		
		pAdd(player);
	}
	
	public void addAll(Player... players) {
		for(Player p : players)
			pAdd(p);
	}
	
	public void removeAll(Player... players) {
		for(Player p : players)
			pRemove(p);
	}
	
	public void update(Player player) {
		val p = get(player);
		
		if(p != null)
			updateGeneral(p);
	}
	
	private void pAdd(Player player) {
		val p = get(player);
		
		if(p != null)
			p.stage = 0;
		else
			list.add(new Phase(player));
	}
	
	private void pRemove(Player player) {
		val p = get(player);
		
		if(p != null)
			p.drain();
	}
	
	private Phase get(Player player) {
		for(Phase p : list)
			if(p.player.equals(player))
				return p;
		return null;
	}
	
	private boolean updatePhase(Phase phase) {
		if(phase.isStable())
			return false;
		
		updateWarningBlocks(phase.player, phase.phase());
		
		return phase.isDrained();
	}
	
	private void updateGeneral(Phase phase) {
		if(phase.isStable())
			updateWarningBlocks(phase.player, phase.stable);
	}

	@SuppressWarnings("deprecation")
	private void updateWarningBlocks(final Player player, final int warningBlocks) {
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		WorldBorder playerWorldBorder = nmsPlayer.world.getWorldBorder();
		PacketPlayOutWorldBorder worldBorder = new PacketPlayOutWorldBorder(playerWorldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS);

		try {
			Field field = worldBorder.getClass().getDeclaredField("i");
			field.setAccessible(true);
			field.setInt(worldBorder, warningBlocks);
			field.setAccessible(!field.isAccessible());
		} catch (Exception e) {
			e.printStackTrace();
		}

		nmsPlayer.playerConnection.sendPacket(worldBorder);
	}
}
