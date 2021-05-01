package fr.mAxYoLo01.BTEFranceUtils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.mAxYoLo01.BTEFranceUtils.inventories.BanRouletteInventory;

public class BanRouletteCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§4Only Players!");
			return true;
		}
		Player p = (Player) sender;
		if (p.hasPermission("btefrance.banroulette")) {
			p.openInventory(BanRouletteInventory.getInventory(p.getUniqueId()).getInventory());
			return true;
		} else {
			sender.sendMessage("§4You do not have the permission!");
			return true;
		}		
	}
}
