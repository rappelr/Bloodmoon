package com.rappelr.bloodmoon.loottable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.Configuration;
import com.rappelr.bloodmoon.utils.Toolkit;

import lombok.Getter;
import lombok.val;

public class LootTable {
	
	private final Configuration config;
	
	@Getter
	private final String name;
	
	@Getter
	private List<LootItem> contents;
	
	private LootTable(Configuration config, String name){
		this.config = config;
		this.name = name;
		
		contents = new ArrayList<LootItem>();
		
		load();
	}
		
	public ItemStack[] get() {
		ArrayList<ItemStack> results = new ArrayList<ItemStack>();
		
		for(LootItem i : contents)
			if(i.roll())
				results.add(i.getItem());
		
		return results.toArray(new ItemStack[0]);
	}
	
	static LootTable of(File file) {
		val config = new Configuration(file, Bloodmoon.getInstance(), "default_loottable.yml");
		return new LootTable(config, Toolkit.formatString(Toolkit.stripExtension(file.getName())));
	}
	
	public void load() {
		config.load();
		contents.clear();
		
		for(String key : config.getSource().getKeys(false)) {
			val item = LootItem.of(config.getSource().getConfigurationSection(key));
			
			if(item != null)
				contents.add(item);
		}
	}
	
	public LootItem get(String item) {
		for(LootItem i : contents)
			if(i.getName().equalsIgnoreCase(item))
				return i;
		return null;
	}
	
	// failure: 0 success: 1
	public int removeItem(String item) {
		LootItem find = get(item);
		
		if(find == null)
			return 0;
		
		removeItem(find);
		return 1;
	}
	
	public void removeItem(LootItem item) {
		contents.remove(item);
		config.set(item.getName(), null);
		config.save();
	}
	
	// failure: 0 success: 1 replaced: 2
	public int setItem(String name, ItemStack baseItem, float chance) {
		LootItem find = get(name);
		
		if(find != null) {
			find.setItem(baseItem);
			find.setChance(chance);
			find.set(config.getSource());
			config.save();
			return 2;
		}
		
		LootItem item = LootItem.of(name, baseItem, chance);
		
		if(item == null)
			return 0;
		
		setItem(item);
		
		return 1;
	}
	
	public void setItem(LootItem item) {
		contents.add(item);
		item.set(config.getSource());
		config.save();
	}
	
	public void setItemChance(LootItem item, float chance) {
		item.setChance(chance);
		item.set(config.getSource());
		config.save();
	}
}
