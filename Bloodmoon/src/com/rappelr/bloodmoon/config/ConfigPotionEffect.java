package com.rappelr.bloodmoon.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigPotionEffect {
	
	@Getter
	private final PotionEffect effect;
	
	public static ConfigPotionEffect of(String string) {
		if(string == null || string.isEmpty())
			return null;
		
		val split = string.split(":");
		
		val effect = PotionEffectType.getByName(split[0]);
		
		if(effect == null)
			return null;
		
		if(split.length == 1)
			return new ConfigPotionEffect(new PotionEffect(effect, 30, 0)); // IMPORTANT, DEFAULT DURATION SHOULD BE 0
		
		if(split.length == 2)
			return new ConfigPotionEffect(new PotionEffect(effect, Integer.parseInt(split[1]), 0));
		
		if(split.length == 3)
			return new ConfigPotionEffect(new PotionEffect(effect, Integer.parseInt(split[1]), Integer.parseInt(split[2])));

		if(split.length == 4)
			return new ConfigPotionEffect(new PotionEffect(effect, Integer.parseInt(split[1]), Integer.parseInt(split[2]) - 1, Boolean.parseBoolean(split[3])));

		if(split.length == 5)
			return new ConfigPotionEffect(new PotionEffect(effect, Integer.parseInt(split[1]), Integer.parseInt(split[2]) - 1, Boolean.parseBoolean(split[3]), Boolean.parseBoolean(split[4])));

		if(split.length >= 6)
			return new ConfigPotionEffect(new PotionEffect(effect, Integer.parseInt(split[1]), Integer.parseInt(split[2]) - 1, Boolean.parseBoolean(split[3]), Boolean.parseBoolean(split[4]), Boolean.parseBoolean(split[5])));
		
		return null;
	}
	
	public static List<ConfigPotionEffect> of(List<String> strings) {
		if(strings == null)
			return null;
		
		val result = new ArrayList<ConfigPotionEffect>();
		strings.forEach(s -> result.add(ConfigPotionEffect.of(s)));
		result.removeIf(Objects::isNull);
		
		return result;
	}
	
	public void applyAll() {
		Bukkit.getOnlinePlayers().forEach(this::apply);
	}
	
	public void applyAll(World world) {
		world.getPlayers().forEach(this::apply);
	}
	
	public void apply(LivingEntity entity) {
		entity.addPotionEffect(effect);
	}

}
