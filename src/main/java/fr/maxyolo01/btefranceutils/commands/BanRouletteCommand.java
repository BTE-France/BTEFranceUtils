package fr.maxyolo01.btefranceutils.commands;

import fr.maxyolo01.btefranceutils.inventories.BanRouletteInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanRouletteCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_RED + "Only Players!");
			return true;
		}
		Player p = (Player) sender;
		if (p.hasPermission("btefrance.banroulette")) {
			p.openInventory(BanRouletteInventory.getInventory(p.getUniqueId()).getInventory());
			return true;
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "You do not have the permission!");
			return true;
		}		
	}
}
