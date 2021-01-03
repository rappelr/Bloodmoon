package com.rappelr.bloodmoon.mob;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import lombok.Getter;

public class MobManager {
	
	@Getter
	private List<BloodmoonMob> mobs;
	
	{
		mobs = new ArrayList<BloodmoonMob>();
	}
	
	public void load(ConfigurationSection section) {
		mobs.clear();
		
		if(section == null)
			return;
		
		for(String key : section.getKeys(false))
			mobs.add(HumanMob.matchType(key) ? HumanMob.of(section.getConfigurationSection(key))
											 : BloodmoonMob.of(section.getConfigurationSection(key)));
		
		mobs.removeIf(Objects::isNull);
	}
	
	public BloodmoonMob of(final Entity entity) {
		return of(entity.getName());
	}
	
	public BloodmoonMob of(final String name) {
		for(BloodmoonMob type : mobs)
			if(type.is(name))
				return type;
		return null;
	}

}
