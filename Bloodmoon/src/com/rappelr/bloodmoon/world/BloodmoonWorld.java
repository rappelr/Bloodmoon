package com.rappelr.bloodmoon.world;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.component.ConfigCommand;
import com.rappelr.bloodmoon.config.component.ConfigSound;
import com.rappelr.bloodmoon.effect.AmbientEffect;
import com.rappelr.bloodmoon.effect.BossBarEffect;
import com.rappelr.bloodmoon.config.FluidSourceConfig;
import com.rappelr.bloodmoon.mob.MobManager;
import com.rappelr.bloodmoon.utils.Toolkit;
import com.rappelr.bloodmoon.world.cache.WorldCache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor(force = true)
public class BloodmoonWorld implements WorldListener, WorldClockListener {

	private static final String CONFIG_DIRECTORY = "worlds/";

	private static final WorldCache cache;

	private final FluidSourceConfig config;
	
	private final BossBarEffect bossbarEffect;
	
	private final AmbientEffect ambientEffect;

	@Getter private final World world;
	
	@Getter private final MobManager mobManager;

	@Getter private final WorldClock clock;

	@Getter private boolean enabled, permanent, darkenSky;

	@Getter(AccessLevel.PACKAGE)
	private long duration;

	@Getter private int interval, ambientFrequency;
	
	private int originalSpawnRate, spawnRate;

	private boolean despawnItems, despawnExperience, lightningOnDeath, noSleeping, noShieldEffect, firstBlood;

	private ConfigCommand[] commandsOnEnd, commandsOnStart;

	private ConfigSound soundOnStart, soundOnEnd, soundOnHit, soundOnPlayerDeath;
	
	@Getter
	private ConfigSound[] soundAmbient;

	static {
		cache = new WorldCache();
		cache.load();
	}

	public BloodmoonWorld(World world) {
		this.world = world;

		config = new FluidSourceConfig(CONFIG_DIRECTORY + world.getName() + ".yml", "default_world.yml", Bloodmoon.getInstance());

		mobManager = new MobManager();

		bossbarEffect = new BossBarEffect(this);
		
		ambientEffect = new AmbientEffect(this);
		
		clock = new WorldClock(this);
		
		originalSpawnRate = world.getMonsterSpawnLimit();
		
		loadConfig();

		if(!enabled)
			return;

		clock.setListener(this);
		
		firstBlood = false;
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
		despawnItems = c.getBoolean("behaviour.despawn-items-on-death", false);
		despawnExperience = c.getBoolean("behaviour.despawn-experience-on-death", false);
		lightningOnDeath = c.getBoolean("behaviour.lightning-on-player-death", false);
		noSleeping = c.getBoolean("behaviour.prevent-sleeping", false);
		noShieldEffect = c.getBoolean("behaviour.shields-prevent-hit-effect", false);
		darkenSky = c.getBoolean("behaviour.darken-sky", false);
		ambientFrequency = c.getInt("behaviour.ambient-frequency", 30);
		spawnRate = c.getInt("behaviour.mob-spawn-rate", 25);
		
		//COMMANDS
		commandsOnStart = ConfigCommand.of(c.getStringList("commands.on-start"));
		commandsOnEnd = ConfigCommand.of(c.getStringList("commands.on-end"));
		
		//SOUNDS
		soundOnStart = ConfigSound.of(c.getString("sounds.on-start"));
		soundOnEnd = ConfigSound.of(c.getString("sounds.on-end"));
		soundAmbient = ConfigSound.of(c.getStringList("sounds.ambient"));
		soundOnHit = ConfigSound.of(c.getString("sounds.entity-hit"));
		soundOnPlayerDeath = ConfigSound.of(c.getString("sounds.player-death"));
		
		//MOBS
		mobManager.load(config.getSource().getConfigurationSection("mobs"));

		if(permanent) {
			clock.unregister();
			if(!clock.isBloodmoon())
				onContinue();
		} else
			clock.register();
		
		clock.load();
		bossbarEffect.setup();
	}
	
	public static void reloadCache() {
		cache.load();
	}
	
	public void startBloodmoon() {
		clock.onBloodmoonStart();
	}
	
	public void endBloodmoon() {
		clock.onBloodmoonEnd();
	}
	
	public void disable() {
		bossbarEffect.clear();
		clock.saveToCache();
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
	public void tick() {
		bossbarEffect.update();
		ambientEffect.tick();
	}

	@Override
	public void onDayBefore() {
		Bloodmoon.getInstance().getLanguage().tell("bloodmoon-day-before", world.getPlayers());
	}

	@Override
	public void onStart() {
		onContinue();

		if(soundOnStart != null)
			soundOnStart.playAll(getWorld());
		
		if(commandsOnStart != null)
			for(ConfigCommand c : commandsOnStart)
				c.run(getWorld());
	}

	@Override
	public void onContinue() {
		Bloodmoon.getInstance().getLanguage().tell("bloodmoon-start", world.getPlayers());
		world.setTime(Bloodmoon.getInstance().getWorldManager().getStartTime());
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setMonsterSpawnLimit(spawnRate);
		
		manager().getWorldBorderEffect().addAll(world.getPlayers().toArray(new Player[0]));
		bossbarEffect.reset();
		bossbarEffect.show();
	}

	@Override
	public void onEnd() {
		Bloodmoon.getInstance().getLanguage().tell("bloodmoon-end", world.getPlayers());
		
		world.setTime(Bloodmoon.getInstance().getWorldManager().getEndTime());

		if(soundOnEnd != null)
			soundOnEnd.playAll(getWorld());
		
		if(commandsOnEnd != null)
			for(ConfigCommand c : commandsOnEnd)
				c.run(getWorld());
		
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		world.setMonsterSpawnLimit(originalSpawnRate);

		manager().getWorldBorderEffect().removeAll(world.getPlayers().toArray(new Player[0]));
		bossbarEffect.clear();
		
		firstBlood = false;
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
		
		Bloodmoon.getInstance().getLanguage().tell("bloodmoon-death", world.getPlayers(), "base", event.getDeathMessage());
		event.setDeathMessage(null);
		
		if(!firstBlood) {
			Bloodmoon.getInstance().getLanguage().tell("bloodmoon-first-blood", world.getPlayers(), "player", event.getEntity().getName());
			firstBlood = true;
		}
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

	@Override
	public void onPlayerJoin(PlayerJoinEvent event, boolean active) {
		if(active) {
			manager().getWorldBorderEffect().add(event.getPlayer());
			bossbarEffect.add(event.getPlayer());
		}
	}

	@Override
	public void onPlayerLeave(PlayerQuitEvent event, boolean active) {
		bossbarEffect.remove(event.getPlayer());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event, boolean active) {
		Toolkit.scheduleSync(() -> manager().getWorldBorderEffect().update(event.getPlayer()));
	}
	
	private static WorldManager manager() {
		return Bloodmoon.getInstance().getWorldManager();
	}
}
