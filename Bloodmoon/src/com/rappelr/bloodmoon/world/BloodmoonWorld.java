package com.rappelr.bloodmoon.world;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.ConfigCommand;
import com.rappelr.bloodmoon.config.ConfigSound;
import com.rappelr.bloodmoon.config.FluidSourceConfig;
import com.rappelr.bloodmoon.mob.MobManager;
import com.rappelr.bloodmoon.world.cache.WorldCache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor(force = true)
public class BloodmoonWorld implements WorldListener, WorldClockListener {

	private static final String CONFIG_DIRECTORY = "worlds/";

	private static final WorldCache cache;

	@Getter private final World world;

	private final FluidSourceConfig config;
	
	@Getter private final MobManager mobManager;

	@Getter private final WorldClock clock;

	@Getter private boolean enabled;

	@Getter(AccessLevel.PACKAGE)
	private long duration;

	@Getter private int interval;

	@SuppressWarnings("unused")
	private boolean permanent, despawnItems, despawnExperience, lightningOnDeath, noSleeping, noShieldEffect, darkenSky;

	private List<ConfigCommand> commandsOnStart, commandsOnEnd;

	@SuppressWarnings("unused")
	private ConfigSound soundOnStart, soundOnEnd, soundAmbient, soundOnHit, soundOnPlayerDeath;

	static {
		cache = new WorldCache();
	}

	public BloodmoonWorld(World world) {
		this.world = world;

		config = new FluidSourceConfig(CONFIG_DIRECTORY + world.getName() + ".yml", "default_world.yml", Bloodmoon.getInstance());

		mobManager = new MobManager();
		
		loadConfig();

		if(!enabled) {
			clock = null;
			return;
		}

		clock = new WorldClock(this);
		clock.setListener(this);
	}

	public void loadConfig() {
		config.load();

		enabled = config.getSource().getBoolean("enabled", false);

		if(!enabled)
			return;

		val c = config.getSource();

		// CLOCK
		interval = c.getInt("clock.interval");
		duration = c.getLong("clock.duration", 10000l);
		permanent = c.getBoolean("clock.permanent-bloodmoon", false);
		
		//BEHAVIOUR
		despawnItems = c.getBoolean("despawn-items-on-death", false);
		despawnExperience = c.getBoolean("despawn-experience-on-death", false);
		lightningOnDeath = c.getBoolean("lightning-on-player-death", false);
		noSleeping = c.getBoolean("prevent-sleeping", false);
		noShieldEffect = c.getBoolean("shields-prevent-hit-effect", false);
		darkenSky = c.getBoolean("darken-sky", false);
		
		//COMMANDS
		commandsOnStart = ConfigCommand.of(c.getStringList("commands.on-start"));
		commandsOnEnd = ConfigCommand.of(c.getStringList("commands.on-end"));
		
		//SOUNDS
		soundOnStart = ConfigSound.of(c.getString("sounds.on-start"));
		soundOnEnd = ConfigSound.of(c.getString("sounds.on-end"));
		soundAmbient = ConfigSound.of(c.getString("sounds.ambient"));
		soundOnHit = ConfigSound.of(c.getString("sounds.entity-hit"));
		soundOnPlayerDeath = ConfigSound.of(c.getString("sounds.player-death"));
		
		//MOBS
		mobManager.load(config.getSource().getConfigurationSection("mobs"));

		if(permanent) {
			clock.disband();
			if(!clock.isBloodmoon())
				onContinue();
		}

	}
	
	public void startBloodmoon() {
		clock.onBloodmoonStart();
	}
	
	public void endBloodmoon() {
		clock.onBloodmoonEnd();
	}

	public boolean isNight() {
		return world.getTime() > 13000 && world.getTime() < 23000;
	}

	com.rappelr.bloodmoon.world.cache.Entry getCache() {
		return BloodmoonWorld.cache.get(this);
	}

	void setCache(int lastBloodmoon, long lastNight) {
		BloodmoonWorld.cache.set(world.getName(), lastBloodmoon, lastNight);
	}

	@Override
	public void onDayBefore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		onContinue();
		Bukkit.broadcastMessage("BLOODMOON STARTS IN " + world.getName());

		if(soundOnStart != null)
			soundOnStart.playAll(getWorld());
		
		if(commandsOnStart != null)
			commandsOnStart.forEach(c -> c.run(getWorld()));
	}

	@Override
	public void onContinue() {
		Bukkit.broadcastMessage("BLOODMOON CONTINUING IN " + world.getName());
		world.setTime(Bloodmoon.getInstance().getWorldManager().getStartTime());
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		
		manager().getWorldBorderEffect().addAll(world.getPlayers().toArray(new Player[0]));
	}

	@Override
	public void onEnd() {
		Bukkit.broadcastMessage("BLOODMOON ENDS IN " + world.getName());
		
		world.setTime(Bloodmoon.getInstance().getWorldManager().getEndTime());

		if(soundOnEnd != null)
			soundOnEnd.playAll(getWorld());
		
		if(commandsOnEnd != null)
			commandsOnEnd.forEach(c -> c.run(getWorld()));
		
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

		manager().getWorldBorderEffect().removeAll(world.getPlayers().toArray(new Player[0]));
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event, boolean active) {
		if(!active)
			return;
		
		if(despawnItems)
			event.getDrops().clear();

		if(despawnExperience) {
			event.setNewTotalExp(0);
			event.setDroppedExp(0);
		}
		
		if(lightningOnDeath)
			event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
		
		if(soundOnPlayerDeath != null)
			soundOnPlayerDeath.playAll(event.getEntity().getWorld());
	}

	@Override
	public void onMobDeath(EntityDeathEvent event, boolean active) {
		if(!(event.getEntity() instanceof Monster) || !active)
			return;
		
		val mob = mobManager.of(event.getEntity());
		
		if(mob != null)
			event.setDroppedExp((int) mob.applyDeath((LivingEntity) event.getEntity(), event.getDroppedExp()));
	}

	@Override
	public void onPlayerDamage(EntityDamageByEntityEvent event, boolean active) {
		if(soundOnHit != null && active)
			soundOnHit.play(event.getEntity().getLocation());
	}

	@Override
	public void onMobDamage(EntityDamageByEntityEvent event, boolean active) {
		if(!(event.getDamager() instanceof Monster) || !active)
			return;
		
		val mob = mobManager.of(event.getDamager());
		
		if(mob != null)
			event.setDamage(mob.applyHit((LivingEntity) event.getEntity(), event.getDamage(), noShieldEffect));
		
		if(soundOnHit != null)
			soundOnHit.play(event.getEntity().getLocation());
	}

	@Override
	public void onMobSpawn(EntitySpawnEvent event, boolean active) {
		if(!(event.getEntity() instanceof Monster) || !active)
			return;
		
		val mob = mobManager.of(event.getEntity());
		
		if(mob != null)
			mob.applySpawn((LivingEntity) event.getEntity());
	}

	@Override
	public void onPlayerSleep(PlayerBedEnterEvent event, boolean active) {
		if(active && noSleeping)
			event.setCancelled(true);
	}
	
	private static WorldManager manager() {
		return Bloodmoon.getInstance().getWorldManager();
	}
}
