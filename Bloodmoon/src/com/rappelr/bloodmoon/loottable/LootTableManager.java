package com.rappelr.bloodmoon.loottable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.utils.Toolkit;

import lombok.Getter;
import lombok.val;

public class LootTableManager {
	
	private final File directory;
	
	@Getter
	private final List<LootTable> lootTables;
	
	{
		lootTables = new ArrayList<LootTable>();
		directory = new File(Bloodmoon.getInstance().getDataFolder(), "loottables");
		
		reload();
	}

	public void reload() {
		if(!directory.exists())
			directory.mkdir();
		
		LootItem.attemptHook();
		
		List<LootTable> notFound = new ArrayList<LootTable>();
		notFound.addAll(lootTables);
		
		for(File f : directory.listFiles()) {
			if(!f.getName().endsWith(".yml"))
				continue;
			
			LootTable find = get(Toolkit.formatString(Toolkit.stripExtension(f.getName())));
			
			if(find == null)
				lootTables.add(LootTable.of(f));
			else {
				notFound.remove(find);
				find.load();
			}
		}
		lootTables.removeAll(notFound);
		Bukkit.getLogger().info("[Bloodmoon] loaded " + lootTables.size() + " loottables");
	}
	
	public boolean create(String name) {
		val find = get(name);
		
		if(find != null)
			return false;
		
		lootTables.add(LootTable.of(new File(Bloodmoon.getInstance().getDataFolder() + "/loottables/" + name + ".yml")));
		return true;
	}
	
	public LootTable get(String name) {
		for(LootTable lootTable : lootTables)
			if(lootTable.getName().equalsIgnoreCase(name))
				return lootTable;
		return null;
	}

}
