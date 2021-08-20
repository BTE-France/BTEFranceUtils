package fr.mAxYoLo01.BTEFranceUtils.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.mAxYoLo01.BTEFranceUtils.inventories.BanRouletteInventory;

public class BanRouletteEvent implements Listener {
	@EventHandler
	public void onInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem() == null) {
			return;
		}
		if (e.getClickedInventory().getHolder() instanceof BanRouletteInventory) {
			e.setCancelled(true);
			BanRouletteInventory inventory = BanRouletteInventory.getInventory(p.getUniqueId());
			
			if (e.getCurrentItem().getType() == Material.STAINED_CLAY) {
				inventory.startBanRoulette();
			}
			else if (e.getCurrentItem().getType() == Material.CONCRETE) {
				if (e.getCurrentItem().getDurability() == 13) {
					inventory.banPlayer();
				}
				p.closeInventory();
				BanRouletteInventory.removeInventory(p.getUniqueId());
			}
		}
		return;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof BanRouletteInventory) {
			BanRouletteInventory.removeInventory(e.getPlayer().getUniqueId());
		}
	}
}
