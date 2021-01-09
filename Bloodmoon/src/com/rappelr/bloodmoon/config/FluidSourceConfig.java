package com.rappelr.bloodmoon.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;

public class FluidSourceConfig {

	@Getter(AccessLevel.PROTECTED)
	private final File file;
	
	@Getter
	private YamlConfiguration source;
	
	public FluidSourceConfig(final String name, final String source, final JavaPlugin plugin) {
		file = new File(plugin.getDataFolder(), name);
		
		if(!file.exists()) {
			Bukkit.getLogger().info("[Bloodmoon] world yaml " + name + " not found, generating new one");
			file.getParentFile().mkdirs();
			
			Configuration.util.copy("res/" + source, file.getAbsolutePath());
		}
				
		this.source = new YamlConfiguration();
	}
	
	public boolean load() {
		try {
			val f = new YamlConfiguration();
			f.load(file);
			source = f;
			return true;
        } catch (IOException | InvalidConfigurationException exception) {
        	exception.printStackTrace();
        	return false;
        }
	}

}
