package com.rappelr.bloodmoon.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.LanguageConfiguration;
import com.rappelr.bloodmoon.mob.BloodmoonMob;
import com.rappelr.bloodmoon.mob.HumanMob;
import com.rappelr.bloodmoon.world.BloodmoonWorld;
import lombok.val;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
	
	private final LanguageConfiguration lang = Bloodmoon.getInstance().getLanguage();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		val player = (sender instanceof Player) ? (Player) sender : null;
		BloodmoonWorld world = player != null ? Bloodmoon.getInstance().getWorldManager().by(player) : null;
		
		// no sub
		if(args.length == 0) {
			
			if(player == null) {
				sender.sendMessage(ChatColor.DARK_RED + "Running Bloodmoon v" + Bloodmoon.getInstance().getDescription().getVersion());
				return true;
			}
			
			if(!player.hasPermission("bloodmoon.checkdays")) {
				lang.tell("no-permission", sender);
				return true;
			}
			
			if(world != null) {

				if(world.isEnabled())
					if(world.getClock().isBloodmoon())
						sender.sendMessage(ChatColor.RED + "Bloodmoon currently!");
					else
						sender.sendMessage(ChatColor.RED + "Bloodmoon in " + world.getClock().daysLeft() + " days!");
				else
					sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				
			}
		}
		
		// about
		else if(args[0].equalsIgnoreCase("about")) {
			
			sender.sendMessage(ChatColor.DARK_RED + "Running Bloodmoon v" + Bloodmoon.getInstance().getDescription().getVersion());
			sender.sendMessage(ChatColor.RED + "Built for api " + Bloodmoon.getInstance().getDescription().getAPIVersion());
			sender.sendMessage(ChatColor.RED + "Loaded " + Bloodmoon.getInstance().getWorldManager().getWorlds().size() + " worlds");
			sender.sendMessage(ChatColor.RED + "Blame Rappelr for the bugs");
			
		}
		
		// reload
		else if(args[0].equalsIgnoreCase("reload")) {
			
			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("no-permission", sender);
				return true;
			}
			
			Bloodmoon.getInstance().reload();
			sender.sendMessage(ChatColor.GREEN + "Reload complete");
			
		}
		
		// last bloodmoon
		else if(args[0].equalsIgnoreCase("last")) {
			
			if(world == null) {
				sender.sendMessage(ChatColor.RED + "That command is only available for players");
				return true;
			}
			
			if(!player.hasPermission("bloodmoon.last")) {
				lang.tell("no-permission", sender);
				return true;
			}
			
			if(world.isEnabled())
				sender.sendMessage(ChatColor.BLUE + "Last bloodmoon was " + world.getClock().getLastBloodmoon() + " days ago, interval: " + world.getInterval());
			else
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
		}
		
		// list worlds
		else if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("worlds")) {
			
			if(player != null && !player.hasPermission("bloodmoon.list")) {
				lang.tell("no-permission", sender);
				return true;
			}
			
			sender.sendMessage(ChatColor.BLUE + "Server has " + Bloodmoon.getInstance().getWorldManager().getWorlds().size() + " worlds");
			for(BloodmoonWorld current : Bloodmoon.getInstance().getWorldManager().getWorlds()) {
				val prefix = ChatColor.RED + "[" + current.getWorld().getName() + "] " + ChatColor.WHITE;
				if(current.isEnabled()) {
					if(current.getClock().isBloodmoon())
						sender.sendMessage(prefix + "bloodmoon active");
					else
						sender.sendMessage(prefix + "bloodmoon in " + current.getClock().daysLeft() + " days");
				} else {
					sender.sendMessage(prefix + "bloodmoon not enabled");
				}
			}
			
		} 
		
		// start bloodmoon
		else if (args[0].equalsIgnoreCase("start")) {
			
			if(player != null) {
				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: >bloodmoon start <worldname>");
				return true;
			}
			
			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);
			
			if(world == null) {
				sender.sendMessage(ChatColor.RED + "World not found");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}
			
			if(!world.getClock().isBloodmoon()) {
				sender.sendMessage(ChatColor.GREEN + "Starting bloodmoon");
				world.startBloodmoon();
			} else
				sender.sendMessage(ChatColor.RED + "Bloodmoon is already active in that world");
		} 

		// end bloodmoon
		else if (args[0].equalsIgnoreCase("end")) {

			if(player != null) {
				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: >bloodmoon end <worldname>");
				return true;
			}

			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);

			if(world == null) {
				sender.sendMessage(ChatColor.RED + "World not found");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}

			if(world.getClock().isBloodmoon()) {
				sender.sendMessage(ChatColor.GREEN + "Ending bloodmoon");
				world.endBloodmoon();
			} else
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not active in that world");
		}
		
		// mobs
		else if (args[0].equalsIgnoreCase("mobs")) {
			
			if(player != null) {
				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: >bloodmoon mobs <worldname>");
				return true;
			}
			
			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);
			
			if(world == null) {
				sender.sendMessage(ChatColor.RED + "World not found");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}
			
			sender.sendMessage(ChatColor.BLUE + world.getWorld().getName() + " has " + world.getMobManager().getMobs().size() + " customized mobs");
			for(BloodmoonMob mob : world.getMobManager().getMobs()) {
				val prefix = ChatColor.RED + "[" + mob.getName() + "] " + ChatColor.WHITE;
				sender.sendMessage(prefix + "(" + (mob instanceof HumanMob ? "human" : "other") + ") " + mob.details());
			}
		} 
		
		// default
		else {
			sender.sendMessage(ChatColor.RED + "Subcommand \"" + args[0] + "\" not found");
		}
		
		return true;
	}

}
