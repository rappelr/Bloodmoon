package com.rappelr.bloodmoon.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.rappelr.bloodmoon.utils.YamlUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;

public class Configuration {
	
	static final YamlUtil util = new YamlUtil();
	
	@Getter(AccessLevel.PROTECTED)
	private final File file;
	
	@Getter
	private YamlConfiguration source;
	
	public Configuration(final String name, final JavaPlugin plugin, final boolean copy) {
		file = new File(plugin.getDataFolder(), name);
		
		if(!file.exists()) {
			Bukkit.getLogger().info("[Bloodmoon] " + name + " not found, copying from jar");
			file.getParentFile().mkdirs();
			if(copy)
				util.copy("res/" + name, file.getAbsolutePath());
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
