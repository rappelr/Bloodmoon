package com.rappelr.bloodmoon.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.Bloodmoon;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class LanguageConfiguration {
	
	private abstract static class Entry {
		
		protected static final char INSERT = '%', COLOR = '&';
		protected static final String SPLITTER = ";;";
		
		abstract void post(List<Conversable> recievers, String[] inserts);

		static Entry of(final String string) {
			try {
				
				if(string.startsWith("TITLE"))
					return TitleMessage.of(string);
				
				return ChatMessage.of(string);
				
			} catch(ArrayIndexOutOfBoundsException e) {}
			
			return null;
		}
	}
	
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class ChatMessage extends Entry {
		
		private final String base;

		@Override
		void post(final List<Conversable> recievers, final String[] inserts) {
			
			String builder = base;
			
			for(int i = 0; i + 1 < inserts.length; i += 2)
				builder = builder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
			
			final String r = builder;
			recievers.forEach(c -> c.sendRawMessage(r));
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
		void post(final List<Conversable> recievers, final String[] inserts) {
			
			String titleBuilder = titleBase, subtitleBuilder = subtitleBase;
			
			for(int i = 0; i + 1 < inserts.length; i += 2)
				if(titleBase.contains(inserts[i]))
					titleBuilder = titleBuilder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
				else
					subtitleBuilder = subtitleBuilder.replace(INSERT + inserts[i] + INSERT, inserts[i + 1]);
			
			for(Conversable c : recievers)
				if(c instanceof Player)
					((Player) c).sendTitle(titleBuilder, subtitleBuilder);
				
		}
		
		static TitleMessage of(final String string) throws ArrayIndexOutOfBoundsException {
			return new TitleMessage(ChatColor.translateAlternateColorCodes(COLOR, string.split(SPLITTER)[1]),
									ChatColor.translateAlternateColorCodes(COLOR, string.split(SPLITTER)[2]));
		}
	}
	
	private static final String FILE_NAME = "lang.yml";
	
	private final Configuration config;
	
	private final HashMap<String, LanguageConfiguration.Entry> entries;
	
	{
		config = new Configuration(FILE_NAME, Bloodmoon.getInstance(), true);
		entries = new HashMap<String, LanguageConfiguration.Entry>();
	}
	
	@SuppressWarnings("serial")
	public void tell(final String message, final Player reciever, final String... insterts) {
		post(message, new ArrayList<Conversable>() {{add(reciever);}}, insterts);
	}

	@SuppressWarnings("serial")
	public void tell(final String message, final CommandSender reciever, final String... insterts) {
		if(reciever instanceof ConsoleCommandSender)
			post(message, new ArrayList<Conversable>() {{add((ConsoleCommandSender) reciever);}}, insterts);
		else
			post(message, new ArrayList<Conversable>() {{add((Conversable) reciever);}}, insterts);
	}
	
	@SuppressWarnings("serial")
	public void tell(final String message, final List<Player> players, final String... inserts) {
		post(message, new ArrayList<Conversable>() {{addAll(players);}}, inserts);
	}
	
	private void post(final String message, final List<Conversable> recievers, final String... inserts) {
		if(!entries.containsKey(message))
			Bukkit.getLogger().warning("Message key " + message + " not found in " + FILE_NAME);
		else
			entries.get(message).post(recievers, inserts);
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
