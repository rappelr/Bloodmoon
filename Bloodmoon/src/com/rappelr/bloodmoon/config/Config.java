package com.rappelr.bloodmoon.config;

import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends ConfigCore {
	
	public Config(final String name, final JavaPlugin plugin) {
		super(name, plugin, false);
	}
	
	public boolean contains(final String key) {
		return getSource().contains(key);
	}
	
	public ConfigurationSection getChild(final String key) {
		return getSource().getConfigurationSection(key);
	}
	
	public void set(final String key, final Object value) {
		getSource().set(key, value);
	}
	
	public Object get(final String key) {
		return getSource().get(key);
	}

	public void save() {
		try {
			getSource().save(getFile());
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}

