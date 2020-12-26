package com.rappelr.bloodmoon.mob;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.config.ConfigPotionEffect;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BloodmoonMob {
	
	@Getter 
	private final String name;
	
	@Getter
	private final LootTable loottable;
	
	private final double healthModifier, damageModifier, experienceModifier;
	
	private final ConfigPotionEffect hitEffect;
	
	public static BloodmoonMob of(final ConfigurationSection section) {
		val healthModifier = section.contains("health-modifier") ? section.getDouble("health-modifier") : 1d;
		val damageModifier = section.contains("damage-modifier") ? section.getDouble("damage-modifier") : 1d;
		val experienceModifier = section.contains("experience-modifier") ? section.getDouble("experience-modifier") : 1d;
		
		val effect = ConfigPotionEffect.of(section.getString("hit-effect"));
		
		LootTable loottable = section.contains("loottable") ? LootTable.of(section.getConfigurationSection("loottable")) : null;
		
		return new BloodmoonMob(section.getName(), loottable, healthModifier, damageModifier, experienceModifier, effect);
	}
	
	public void applySpawn(final LivingEntity entity) {
		val health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * healthModifier;
		entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
		entity.setHealth(health);
	}
	
	public double applyHit(final LivingEntity victim, final double damage, final boolean noShieldEffect) {
		if(hitEffect != null) {
			if(victim instanceof Player) {
				if(!noShieldEffect || !((Player) victim).isBlocking())
					hitEffect.apply(victim);
			} else
				hitEffect.apply(victim);
		}
		
		return damage * damageModifier;
	}
	
	public double applyDeath(LivingEntity entity, final double experience) {
		if(loottable != null)
			for(ItemStack item : loottable.get())
				entity.getWorld().dropItemNaturally(entity.getLocation(), item);
		return experience * experienceModifier;
	}

	boolean is(Entity entity) {
		return name.equalsIgnoreCase(entity.getType().name());
	}

	public String details() {
		return "hpMod: " + healthModifier + " dmgMod: " + damageModifier + " xpMod: " + experienceModifier + " hitEff: " + (hitEffect == null ? "none" : hitEffect.getEffect().getType().getName()) + " lootTable: " + (loottable == null ? "none" : loottable.size());
	}
}
