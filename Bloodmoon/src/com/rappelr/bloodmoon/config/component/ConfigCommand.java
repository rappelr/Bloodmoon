package com.rappelr.bloodmoon.config.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ConfigCommand {
	
	private static final String INSERT_PLAYER = "$p", INSERT_WORLD = "$w";
	
	private final CommandType type;
	
	private final String command;
	
	public static ConfigCommand of(String string) {
		CommandType type = CommandType.of(string);
		
		if(type == null)
			return null;
		
		return new ConfigCommand(type, type.snip(string));
	}
	
	public static ConfigCommand[] of(List<String> strings) {
		if(strings == null)
			return null;
		
		val result = new ArrayList<ConfigCommand>();
		strings.forEach(s -> result.add(ConfigCommand.of(s)));
		result.removeIf(Objects::isNull);
		
		return result.toArray(new ConfigCommand[0]);
	}

	public void run(World world) {
		val worldName = world == null ? "" : world.getName();

		String processed = command.replaceAll(INSERT_WORLD, worldName);

		switch (type) {
		case SS:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed);
			break;

		case SE:
			Bukkit.getOnlinePlayers().forEach(p ->
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed.replaceAll(INSERT_PLAYER, p.getName())));
			break;

		case PE:
			Bukkit.getOnlinePlayers().forEach(p ->
					Bukkit.dispatchCommand(p, processed.replaceAll(INSERT_PLAYER, p.getName())));
			break;

		default:
			break;
		}
	}
	
	@AllArgsConstructor
	enum CommandType {
		
		SS(";s"),
		SE(";f"),
		PE(";p");

		private final String prefix;
		
		String snip(String cmd) {
			return cmd.replace(prefix, new String());
		}

		static CommandType of(String cmd) {
			
			if(cmd != null && !cmd.isEmpty())
				try {
					val prefix = cmd.substring(cmd.length() - 2);
	
					for(CommandType c : values())
						if(c.prefix.equalsIgnoreCase(prefix))
							return c;
				} catch (IndexOutOfBoundsException e) {}

			return null;
		}
	}
}


