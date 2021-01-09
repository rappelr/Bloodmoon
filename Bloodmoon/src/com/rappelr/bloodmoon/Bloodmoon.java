package com.rappelr.bloodmoon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.rappelr.bloodmoon.command.CommandExecutor;
import com.rappelr.bloodmoon.config.Configuration;
import com.rappelr.bloodmoon.config.LanguageConfiguration;
import com.rappelr.bloodmoon.listener.BloodmoonListener;
import com.rappelr.bloodmoon.loottable.LootTableManager;
import com.rappelr.bloodmoon.world.WorldManager;

import lombok.Getter;

public class Bloodmoon extends JavaPlugin {
	
	@Getter private static Bloodmoon instance;
	
	@Getter private WorldManager worldManager;
	
	@Getter private LootTableManager lootTableManager;
	
	@Getter private Configuration configuration;
	
	@Getter private LanguageConfiguration language;
	
	{
		instance = this;
	}

	@Override
    public void onEnable() {
		language = new LanguageConfiguration();

		reloadConfiguration();
		
		lootTableManager = new LootTableManager();
		
		worldManager = new WorldManager();
		
		Bukkit.getPluginManager().registerEvents(new BloodmoonListener(), this);
		
		getCommand("bloodmoon").setExecutor(new CommandExecutor());
	}

	@Override
    public void onDisable() {

		worldManager.disable();
		
	}
	
	public void reload(boolean ia) {
		if(!ia)
			reloadConfiguration();
		lootTableManager.reload();
		worldManager.reload();
	}

	private void reloadConfiguration() {
		configuration = new Configuration("config.yml", this, true);
		language.load();
	}
	
}
