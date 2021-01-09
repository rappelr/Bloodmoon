package com.rappelr.bloodmoon.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rappelr.bloodmoon.Bloodmoon;
import com.rappelr.bloodmoon.config.LanguageConfiguration;
import com.rappelr.bloodmoon.loottable.LootTableManager;
import com.rappelr.bloodmoon.loottable.LootItem;
import com.rappelr.bloodmoon.loottable.LootTable;
import com.rappelr.bloodmoon.mob.BloodmoonMob;
import com.rappelr.bloodmoon.mob.HumanMob;
import com.rappelr.bloodmoon.utils.Toolkit;
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
			sender.sendMessage(ChatColor.RED + "Use \"/bm help\" for a list of commands");
			sender.sendMessage(ChatColor.RED + "Blame Rappelr for the bugs");

		}

		// about
		else if(args[0].equalsIgnoreCase("help")) {

			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("command-no-permission", sender);
				return true;
			}
			
			if(args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Help for bloodmoon");
				sender.sendMessage(ChatColor.RED + "/bm: " + ChatColor.WHITE + "Days until next bloodmoon");
				sender.sendMessage(ChatColor.RED + "/bm about: " + ChatColor.WHITE + "Information about current build");
				sender.sendMessage(ChatColor.RED + "/bm reload: " + ChatColor.WHITE + "Reload plugin individually");
				sender.sendMessage(ChatColor.RED + "/bm worlds: " + ChatColor.WHITE + "Lists all worlds and their status");
				sender.sendMessage(ChatColor.RED + "/bm last: " + ChatColor.WHITE + "Days since last bloodmoon + interval");
				sender.sendMessage(ChatColor.RED + "/bm start [world]: " + ChatColor.WHITE + "Start bloodmoon in a world");
				sender.sendMessage(ChatColor.RED + "/bm end [world]: " + ChatColor.WHITE + "End bloodmoon in a world");
				sender.sendMessage(ChatColor.RED + "/bm mobs [world]: " + ChatColor.WHITE + "Lists all customized mobs from a world");
				sender.sendMessage(ChatColor.RED + "/bm loot ...: " + ChatColor.WHITE + "loottable commands, use \"/bm help loot\" for more");
			
			} else if (args[1].equalsIgnoreCase("loot")) {
				sender.sendMessage(ChatColor.RED + "Help for bloodmoon loot");
				sender.sendMessage(ChatColor.RED + "...: " + ChatColor.WHITE + "List of all loottables and their sizes");
				sender.sendMessage(ChatColor.RED + "... <loottable>: " + ChatColor.WHITE + "List of loottable contents");
				sender.sendMessage(ChatColor.RED + "... <loottable> create: " + ChatColor.WHITE + "Create new loottable, reload required to apply");
				sender.sendMessage(ChatColor.RED + "... <loottable> set: " + ChatColor.WHITE + "set item from hand in loottable, name will be generated");
				sender.sendMessage(ChatColor.RED + "... <loottable> set <item> [chance]: " + ChatColor.WHITE + "same as ^ but with item name, chance will be 0 if left out");
				sender.sendMessage(ChatColor.RED + "... <loottable> remove <item>: " + ChatColor.WHITE + "Remove item from loottable");
				sender.sendMessage(ChatColor.RED + "... <loottable> details <item>: " + ChatColor.WHITE + "Show item details");
				sender.sendMessage(ChatColor.RED + "... <loottable> get <item>: " + ChatColor.WHITE + "Recieve item in inventory");
				sender.sendMessage(ChatColor.RED + "... <loottable> chance <item> <chance>: " + ChatColor.WHITE + "Change item drop chance");
				
			} else {
				sender.sendMessage(ChatColor.RED + "No specific help for " + args[1]);
			}
		}

		// reload
		else if(args[0].equalsIgnoreCase("reload")) {

			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("command-no-permission", sender);
				return true;
			}

			Bloodmoon.getInstance().reload(false);
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

			if(player != null && !player.hasPermission("bloodmoon.admin")) {
				lang.tell("command-no-permission", player);
				return true;
			}

			final LootTableManager manager = Bloodmoon.getInstance().getLootTableManager();

			if(args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Bloodmoon has " + manager.getLootTables().size() + " loottables set up");
				for(LootTable lootTable : manager.getLootTables())
					sender.sendMessage(ChatColor.RED + "[" + lootTable.getName() + "] " + ChatColor.WHITE + lootTable.getContents().size() + " items");
				return true;
			}
			
			if(args.length > 2 && args[2].equalsIgnoreCase("create")) {
				if(manager.create(args[1]))
					sender.sendMessage(ChatColor.BLUE + "Loottable [" + args[1] + "] created successfully, remember it only applies to mobs after you reload the plugin");
				else
					sender.sendMessage(ChatColor.RED + "A loottable with the name \"" + args[1] + "\" already exists");
				return true;
			}

			val lootTable = manager.get(args[1]);

			if(lootTable == null) {
				lang.tell("command-object-not-found", sender, "object", "Loottable \"" + args[1] + "\"");
				return true;
			}

			if(args.length == 2) {
				sender.sendMessage(ChatColor.RED + "Loottable " + lootTable.getName() + " has " + lootTable.getContents().size() + " entries");
				for(LootItem item : lootTable.getContents())
					sender.sendMessage(ChatColor.RED + "[" + item.getName() + "] " + ChatColor.WHITE + "chance: " + item.getChance() + "% type: " + item.getItem().getType().name() + " amount: " + item.getItem().getAmount());
				return true;
			}
			
			if(args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("add")) {
				if(player != null) {
					if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
						player.sendMessage(ChatColor.RED + "ItemStack in your hand will be added, this can not be air");
					else {
						ItemStack item = player.getInventory().getItemInMainHand();
						
						String name = Toolkit.formName(item);
						float chance = 0f;
						
						if(args.length > 3)
							name = args[3];
						if(args.length > 4)
							chance = Toolkit.parseOr(args[4], chance);
						
						int result = lootTable.setItem(name, item, chance);
						
						switch (result) {
						case 1:
							sender.sendMessage(ChatColor.BLUE + "[" + name + "] " + ChatColor.WHITE + "has been added to " + lootTable.getName());
							break;
						case 2:
							sender.sendMessage(ChatColor.BLUE + "[" + name + "] " + ChatColor.WHITE + "has been replaced in " + lootTable.getName());
							break;
						default:
							sender.sendMessage(ChatColor.RED + "failed to add " + name + " to " + lootTable.getName());
							break;
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "This command is only available for players");
				}
				
				return true;
			}

			if(args.length > 3) {
				LootItem item = lootTable.get(args[3]);

				if(item == null)
					lang.tell("command-object-not-found", sender, "object", "LootTable item \"" + args[3] + "\"");
				
				else if(args[2].equalsIgnoreCase("details")) {
					sender.sendMessage(ChatColor.RED + "Details of [" + item.getName() + "]:");
					sender.sendMessage("Drop chance: " + item.getChance());
					sender.sendMessage("Item display name: " + (item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() : "none"));
					sender.sendMessage("Item type: " + item.getItem().getType().name());
					sender.sendMessage("Item amount: " + item.getItem().getAmount());
					sender.sendMessage("Serialization format: " + item.getFormat());
				
				} else if(args[2].equalsIgnoreCase("get")) {
					if(player != null) {
						sender.sendMessage("Recieved item " + item.getName());
						if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
							player.getInventory().setItemInMainHand(item.getItem());
						else
							player.getInventory().addItem(item.getItem());
					} else {
						sender.sendMessage(ChatColor.RED + "This command is only available for players");
					}
				
				} else if(args[2].equalsIgnoreCase("remove")) {
					lootTable.removeItem(item);
					sender.sendMessage(ChatColor.BLUE + "[" + item.getName() + "] " + ChatColor.WHITE + "has been removed from " + lootTable.getName());
				
				} else if(args[2].equalsIgnoreCase("chance")) {
					float chance = Toolkit.parseOr(args[4], -1);
					
					if(chance < 0)
						sender.sendMessage(ChatColor.RED + "Chance has to be a number over or equal to zero");
					else {
						sender.sendMessage(ChatColor.BLUE + "[" + item.getName() + "] " + ChatColor.WHITE + "change changed from " + item.getChance() + " to " + chance);
						lootTable.setItemChance(item, chance);
					}
				}

				return true;
			}

			lang.tell("command-invalid-syntax", sender, "example", "bloodmoon loot <loot-table> <func> [item] [arg]");

		}

		// default
		else {
			sender.sendMessage(ChatColor.RED + "Subcommand \"" + args[0] + "\" not found");
		}

		return true;
	}

}
