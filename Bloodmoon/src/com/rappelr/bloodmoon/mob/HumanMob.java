package com.rappelr.bloodmoon.mob;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.component.ConfigPotionEffect;
import com.rappelr.bloodmoon.loottable.LootTable;

import lombok.val;

public class HumanMob extends BloodmoonMob {
	
	@SuppressWarnings("serial")
	private static final List<String> types = new ArrayList<String>() {{
		add("ZOMBIE");
		add("HUSK");
		add("SKELETON");
		add("STRAY");
		add("WITHER_SKELETON");
	}};
	
	// 0: main_h 1: off_h: 2: helmet, 3: chest, 4: legs, 5: boots
	private final ItemStack[] equipment;
	
	public HumanMob(final String name, final ItemStack[] equipment, final LootTable lootTable, final double healthModifier, final double damageModifier, final double experienceModifier, final ConfigPotionEffect hitEffect) {
		super(name, lootTable, healthModifier, damageModifier, experienceModifier, hitEffect);
		
		this.equipment = equipment;
	}
	
	public static HumanMob of(final ConfigurationSection section) {
		val healthModifier = section.contains("health-modifier") ? section.getDouble("health-modifier") : 1d;
		val damageModifier = section.contains("damage-modifier") ? section.getDouble("damage-modifier") : 1d;
		val experienceModifier = section.contains("experience-modifier") ? section.getDouble("experience-modifier") : 1d;
		
		val effect = ConfigPotionEffect.of(section.getString("hit-effect"));
		
		LootTable loottable = section.contains("loottable") ? Bloodmoon.getInstance().getLootTableManager().get(section.getString("loottable")) : null;
		
		ItemStack[] equipment = new ItemStack[] {
			section.getItemStack("equipment.main-hand"),
			section.getItemStack("equipment.off-hand"),
			section.getItemStack("equipment.helmet"),
			section.getItemStack("equipment.chestplate"),
			section.getItemStack("equipment.leggings"),
			section.getItemStack("equipment.boots")
		};
		
		return new HumanMob(section.getName(), equipment, loottable, healthModifier, damageModifier, experienceModifier, effect);
	}
	
	public void applyArmor(LivingEntity entity) {
		entity.getEquipment().setItemInMainHand(equipment[0]);
		entity.getEquipment().setItemInMainHandDropChance(0f);
		entity.getEquipment().setItemInOffHand(equipment[1]);
		entity.getEquipment().setItemInOffHandDropChance(0f);
		entity.getEquipment().setHelmet(equipment[2]);
		entity.getEquipment().setHelmetDropChance(0f);
		entity.getEquipment().setChestplate(equipment[3]);
		entity.getEquipment().setChestplateDropChance(0f);
		entity.getEquipment().setLeggings(equipment[4]);
		entity.getEquipment().setLeggingsDropChance(0f);
		entity.getEquipment().setBoots(equipment[5]);
		entity.getEquipment().setBootsDropChance(0f);
	}

	@Override
	public void applySpawn(final LivingEntity entity) {
		super.applySpawn(entity);
		applyArmor(entity);
	}
	
	public static boolean matchType(final String name) {
		for(final String match : types)
			if(match.equalsIgnoreCase(name))
				return true;
		return false;
	}
}
