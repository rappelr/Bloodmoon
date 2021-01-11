package com.rappelr.bloodmoon.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;

public class MythicMobsHook extends Hook {
	
	public MythicMobsHook() {
		super("MythicMobs");
		
		if(isPresent())
			Bukkit.getLogger().info("[Bloodmoon] MythicMobs detected, registered listener");
		else
			Bukkit.getLogger().info("[Bloodmoon] MythicMobs not detected, dependant features disabled");
	}
	
	@EventHandler
	public void onMythicConditionLoad(MythicConditionLoadEvent event)	{
		Bukkit.getLogger().info("MythicConditionLoadEvent called for condition " + event.getConditionName());

		if(event.getConditionName().equalsIgnoreCase(MythicMobsCondition.NAME))	{
			SkillCondition condition = new MythicMobsCondition(event.getConfig());
			event.register(condition);
			Bukkit.getLogger().info("-- Registered MythicMobsCondition condition!");
		}
	}

}
