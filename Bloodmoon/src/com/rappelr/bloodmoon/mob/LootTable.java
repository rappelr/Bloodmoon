package com.rappelr.bloodmoon.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.Getter;

public class LootTable {
	
	@Getter
	private final HashMap<ItemStack, Float> contents;
	
	private LootTable(final HashMap<ItemStack, Float> contents) {
		this.contents = contents;
	}
		
	public ItemStack[] get() {
		ArrayList<ItemStack> results = new ArrayList<ItemStack>();
		
		for(Entry<ItemStack, Float> entry : contents.entrySet())
			if(entry.getValue() > (ThreadLocalRandom.current().nextFloat() * 100f))
				results.add(entry.getKey());
		
		return null;
	}
	
	public static LootTable of(ConfigurationSection s) {
		HashMap<ItemStack, Float> builder = new HashMap<ItemStack, Float>();
		
		for(String key : s.getKeys(false)) {
			float chance = (float) s.getDouble(key + ".chance");
			ItemStack item = s.getItemStack(key + ".item");
			
			if(item == null || chance <= 0f)
				continue;
			
			builder.put(item, Float.valueOf(chance));
		}
			
		
		return new LootTable(builder);
	}

	public int size() {
		return contents.size();
	}

}
