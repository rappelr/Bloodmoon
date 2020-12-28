package com.rappelr.bloodmoon.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;

public class Configuration {
	
	@Getter(AccessLevel.PROTECTED)
	private final File file;
	
	@Getter
	private YamlConfiguration source;
	
	public Configuration(final String name, final JavaPlugin plugin, final boolean copy) {
		file = new File(plugin.getDataFolder(), name);
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			if(copy)
				plugin.saveResource(name, false);
			else
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
				
		source = new YamlConfiguration();
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

	public boolean contains(String key) {
		return source.contains(key);
	}

	public void set(String key, Object value) {
		source.set(key, value);
	}

	public void save() {
		try {
			getSource().save(getFile());
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

}
