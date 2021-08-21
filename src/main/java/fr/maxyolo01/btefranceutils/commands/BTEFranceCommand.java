package fr.maxyolo01.btefranceutils.commands;

import fr.maxyolo01.btefranceutils.BteFranceUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BTEFranceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args[0].equalsIgnoreCase("reload")) {
			if (!(sender instanceof Player)) {
				reloadConfig(sender);
				return true;
			}
			if (((Player) sender).hasPermission("btefrance.reload")) {
				reloadConfig(sender);
				return true;
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have the permission!");
				return true;
			}
		}
		return true;
	}
	
	private void reloadConfig(CommandSender sender) {
		BteFranceUtils.instance.loadConfig();
		sender.sendMessage(
				ChatColor.DARK_GRAY + "[" +
				ChatColor.LIGHT_PURPLE + "BTEFranceUtils" +
				ChatColor.DARK_GRAY + "] " +
				ChatColor.GOLD + "Plugin reloaded!");
	}
}
