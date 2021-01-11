package com.rappelr.bloodmoon.loottable;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.hook.ItemsAdderHook;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@AllArgsConstructor
public class LootItem {
	
	private static final String SIMPLE = "simple", SERIALIZED = "serialized", ITEMSADDER = "itemsadder";
	
	private static final ItemsAdderHook itemsAdderHook;
	
	static {
		itemsAdderHook = new ItemsAdderHook();
	}
	
	static void attemptHook() {
		itemsAdderHook.attempt();
	}
	
	@Getter
	private final String name;

	@Getter
	private String format;
	
	@Getter
	private ItemStack item;
	
	@Getter @Setter(AccessLevel.PACKAGE)
	private float chance;
	
	static LootItem of(ConfigurationSection section) {
		if(!section.contains("format") || !section.contains("item"))
			return null;
		
		String format = section.getString("format");
		
		ItemStack item = null;
		
		if(format.equalsIgnoreCase(SIMPLE)) {
			val material = Material.getMaterial(section.getString("item"));
			
			if(material != null)
				item = new ItemStack(material);
		} else if(format.equalsIgnoreCase(SERIALIZED)) {
			item = section.getItemStack("item");
		} else if(format.equalsIgnoreCase(ITEMSADDER)) {
			if(itemsAdderHook.isPresent())
				item = itemsAdderHook.getItemStack(section.getString("item")); 
		}
			
		
		float chance = (float) section.getDouble("chance", 0d);
		
		if(item == null || chance < 0f)
			return null;
		
		if(section.contains("amount"))
			item.setAmount(section.getInt("amount", 1));
		
		return new LootItem(section.getName(), format, item, chance);
	}
	
	static LootItem of(String name, ItemStack item, float chance) {
		if(item != null)
			return new LootItem(name, getFormat(item), item, chance);
		return null;
	}
	
	static String getFormat(ItemStack item) {
		String format = SERIALIZED;
		
		if(itemsAdderHook.isPresent() && itemsAdderHook.isCustom(item))
			format = ITEMSADDER;
		else if(item.isSimilar(new ItemStack(item.getType())))
			format = SIMPLE;
		
		return format;
	}
	
	void setItem(ItemStack item) {
		this.item = item;
		format = getFormat(item);
	}
	
	void set(YamlConfiguration config) {
		config.set(name + ".format", format);
		config.set(name + ".chance", chance);
		
		if(!format.equalsIgnoreCase(SERIALIZED))
			config.set(name + ".amount", item.getAmount());
		
		if(format.equalsIgnoreCase(SIMPLE))
			config.set(name + ".item", item.getType().name());
		else if(format.equalsIgnoreCase(ITEMSADDER) && itemsAdderHook.isPresent())
			config.set(name + ".item", itemsAdderHook.getName(item));
		else
			config.set(name + ".item", item);
	}
	
	boolean roll() {
		return chance > ThreadLocalRandom.current().nextFloat() * 100f;
	}

}
