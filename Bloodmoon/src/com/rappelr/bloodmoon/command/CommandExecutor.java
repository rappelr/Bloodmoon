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
				lang.tell("command-no-permission", sender);
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
			sender.sendMessage(ChatColor.RED + Bloodmoon.getInstance().getDescription().getDescription());
			sender.sendMessage(ChatColor.RED + "Built for api " + Bloodmoon.getInstance().getDescription().getAPIVersion());
			sender.sendMessage(ChatColor.RED + "Loaded " + Bloodmoon.getInstance().getWorldManager().getWorlds().size() + " worlds");
			sender.sendMessage(ChatColor.RED + "Blame Rappelr for the bugs");

		}

		// about
		else if(args[0].equalsIgnoreCase("help")) {

			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("command-no-permission", sender);
				return true;
			}

			sender.sendMessage(ChatColor.RED + "Help for bloodmoon");
			sender.sendMessage(ChatColor.RED + "/bm: " + ChatColor.WHITE + "Days until next bloodmoon");
			sender.sendMessage(ChatColor.RED + "/bm about: " + ChatColor.WHITE + "Information about current build");
			sender.sendMessage(ChatColor.RED + "/bm reload: " + ChatColor.WHITE + "Reload plugin individually");
			sender.sendMessage(ChatColor.RED + "/bm worlds: " + ChatColor.WHITE + "Lists all worlds and their status");
			sender.sendMessage(ChatColor.RED + "/bm last: " + ChatColor.WHITE + "Days since last bloodmoon + interval");
			sender.sendMessage(ChatColor.RED + "/bm start [world]: " + ChatColor.WHITE + "Start bloodmoon in a world");
			sender.sendMessage(ChatColor.RED + "/bm end [world]: " + ChatColor.WHITE + "End bloodmoon in a world");
			sender.sendMessage(ChatColor.RED + "/bm mobs [world]: " + ChatColor.WHITE + "Lists all customized mobs from a world");
			sender.sendMessage(ChatColor.RED + "/bm loot <mob> [world]: " + ChatColor.WHITE + "Display the custom loottable of a mob");

		}

		// reload
		else if(args[0].equalsIgnoreCase("reload")) {

			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("command-no-permission", sender);
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
				lang.tell("command-no-permission", sender);
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
				lang.tell("command-no-permission", sender);
				return true;
			}

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
					lang.tell("command-no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				lang.tell("command-invalid-syntax", sender, "example", "bloodmoon start <worldname>");
				return true;
			}

			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);

			if(world == null) {
				lang.tell("command-object-not-found", sender, "object", "World");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}

			if(!world.getClock().isBloodmoon()) {
				sender.sendMessage(ChatColor.DARK_RED + "Starting bloodmoon");
				world.startBloodmoon();
			} else
				sender.sendMessage(ChatColor.RED + "Bloodmoon is already active in that world");
		} 

		// end bloodmoon
		else if (args[0].equalsIgnoreCase("end")) {

			if(player != null) {
				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("command-no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				lang.tell("command-invalid-syntax", sender, "example", "bloodmoon end <worldname>");
				return true;
			}

			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);

			if(world == null) {
				lang.tell("command-object-not-found", sender, "object", "World");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}

			if(world.getClock().isBloodmoon()) {
				sender.sendMessage(ChatColor.DARK_RED + "Ending bloodmoon");
				world.endBloodmoon();
			} else
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not active in that world");
		}

		// mobs
		else if (args[0].equalsIgnoreCase("mobs")) {

			if(player != null) {
				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("command-no-permission", sender);
					return true;
				}
			} else if(args.length < 2) {
				lang.tell("command-invalid-syntax", sender, "example", "bloodmoon mobs <worldname>");
				return true;
			}

			if(args.length > 1)
				world = Bloodmoon.getInstance().getWorldManager().by(args[1]);

			if(world == null) {
				lang.tell("command-object-not-found", sender, "object", "World");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}

			sender.sendMessage(ChatColor.RED + world.getWorld().getName() + " has " + world.getMobManager().getMobs().size() + " customized mobs");
			for(BloodmoonMob mob : world.getMobManager().getMobs()) {
				val prefix = ChatColor.RED + "[" + mob.getName() + "] " + ChatColor.WHITE;
				sender.sendMessage(prefix + "type: " + (mob instanceof HumanMob ? "human" : "normal") + " " + mob.details());
			}
		}

		// loot
		else if (args[0].equalsIgnoreCase("loot")) {

			if(player != null) {

				if(!player.hasPermission("bloodmoon.admin")) {
					lang.tell("command-no-permission", player);
					return true;
				} else if(args.length < 2) {
					lang.tell("command-invalid-syntax", player, "example", "bloodmoon loot <mob> [worldname]");
					return true;
				}

			} else if(args.length < 3) {
				lang.tell("command-invalid-syntax", sender, "example", "bloodmoon loot <mob> <worldname>");
				return true;
			}

			if(args.length > 2)
				world = Bloodmoon.getInstance().getWorldManager().by(args[2]);

			if(world == null) {
				lang.tell("command-object-not-found", sender, "object", "World");
				return true;
			}

			if(!world.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon is not enabled in this world");
				return true;
			}

			BloodmoonMob mob = world.getMobManager().of(args[1]);

			if(mob == null) {
				lang.tell("command-object-not-found", sender, "object", "Mob \"" + args[1] + "\"");
				return true;
			}

			if(mob.getLoottable() == null) {
				lang.tell("command-object-not-found", sender, "object", "Loottable for " + mob.getName());
				return true;
			}

			sender.sendMessage(ChatColor.RED + "Loottable of " + mob.getName() + " has " + mob.getLoottable().size() + " entries");
			mob.getLoottable().getContents().entrySet().forEach(e -> {
				sender.sendMessage("[" + e.getValue().floatValue() + "%] " + e.getKey().getType().name());
			});
		}

		// default
		else {
			sender.sendMessage(ChatColor.RED + "Subcommand \"" + args[0] + "\" not found");
		}

		return true;
	}

}
