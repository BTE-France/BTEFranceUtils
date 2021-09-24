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
			if (!(sender instanceof Player) || sender.hasPermission("btefrance.reload")) {
				reloadConfig(sender);
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have the permission!");
			}
		}
		return true;
	}
	
	private void reloadConfig(CommandSender sender) {
		try {
			BteFranceUtils.instance().reload();
			sender.sendMessage(
					ChatColor.DARK_GRAY + "[" +
							ChatColor.LIGHT_PURPLE + "BTEFranceUtils" +
							ChatColor.DARK_GRAY + "] " +
							ChatColor.GOLD + "Plugin reloaded!");
		} catch(Exception e) {
			sender.sendMessage(
					ChatColor.DARK_GRAY + "[" +
							ChatColor.LIGHT_PURPLE + "BTEFranceUtils" +
							ChatColor.DARK_GRAY + "] " +
							ChatColor.DARK_RED + "Plugin reload failed!");
		}
	}
}
