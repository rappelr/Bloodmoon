package com.rappelr.bloodmoon.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.Bloodmoon;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class LanguageConfiguration {
	
	private abstract static class Entry {
		
		protected static final char INSERT = '%', COLOR = '&';
		protected static final String SPLITTER = ";;";
		
		abstract void post(String message, List<Player> players, String[] inserts);

		static Entry of(final String string) {
			try {
				
				if(string.startsWith("TITLE"))
					return TitleMessage.of(string);
				
				return ChatMessage.of(string);
				
			} catch(ArrayIndexOutOfBoundsException e) {
				Bukkit.getLogger().info("aioobe");}
			
			return null;
		}
	}
	
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class ChatMessage extends Entry {
		
		private final String base;

		@Override
		void post(final String message, final List<Player> players, final String[] inserts) {
			
			String builder = base;
			
			for(int i = 0; i + 1 < inserts.length; i += 2)
				builder = builder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
			
			for(Player player : players)
				player.sendMessage(message);
		}
		
		static ChatMessage of(final String string) throws ArrayIndexOutOfBoundsException {
			return new ChatMessage(ChatColor.translateAlternateColorCodes(COLOR, string.split(SPLITTER)[1]));
		}
	}
	
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class TitleMessage extends Entry {
		
		private final String titleBase, subtitleBase;

		@SuppressWarnings("deprecation")
		@Override
		void post(final String message, final List<Player> players, final String[] inserts) {
			
			String titleBuilder = titleBase, subtitleBuilder = subtitleBase;
			
			for(int i = 0; i + 1 < inserts.length; i += 2)
				if(titleBase.contains(inserts[i]))
					titleBuilder = titleBuilder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
				else
					subtitleBuilder = subtitleBuilder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
			
			for(Player player : players)
				player.sendTitle(titleBuilder, subtitleBuilder);
		}
		
		static TitleMessage of(final String string) throws ArrayIndexOutOfBoundsException {
			return new TitleMessage(ChatColor.translateAlternateColorCodes(COLOR, string.split(SPLITTER)[1]),
									ChatColor.translateAlternateColorCodes(COLOR, string.split(SPLITTER)[2]));
		}
	}
	
	private static final String FILE_NAME = "lang.yml";
	
	private final ConfigCore config;
	
	private final HashMap<String, LanguageConfiguration.Entry> entries;
	
	{
		config = new ConfigCore(FILE_NAME, Bloodmoon.getInstance(), true);
		entries = new HashMap<String, LanguageConfiguration.Entry>();
	}
	
	@SuppressWarnings("serial")
	public void post(final String message, final Player player, final String... insterts) {
		post(message, new ArrayList<Player>() {{add(player);}}, insterts);
	}
	
	public void post(final String message, final List<Player> players, final String... inserts) {
		if(!entries.containsKey(message))
			Bukkit.getLogger().warning("Message key " + message + " not found in " + FILE_NAME);
		else
			entries.get(message).post(message, players, inserts);
	}

	public void load() {
		config.load();
		entries.clear();
		
		for(String key : config.getSource().getKeys(true)) {
			val entry = Entry.of(config.getSource().getString(key));
			
			if(entry != null)
				entries.put(key, entry);
		}
	}
}
