package fr.mAxYoLo01.BTEFranceUtils.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.mAxYoLo01.BTEFranceUtils.Main;

public class BanRouletteInventory implements InventoryHolder {
	private static HashMap<UUID, BanRouletteInventory> inventories = new HashMap<>();
	
	public static BanRouletteInventory getInventory(UUID uuid) {
		BanRouletteInventory inventory = inventories.get(uuid);
		if (inventory == null) {
			inventories.put(uuid, new BanRouletteInventory(uuid));
			inventory = inventories.get(uuid);
		}
		return inventory;
	}
	
	public static void removeInventory(UUID uuid) {
		inventories.remove(uuid);
	}
	
	///////////////////////////////////////////////////////////
	
	private Inventory inventory;
	private UUID uuid;
	public boolean animationFinished;
	
	public BanRouletteInventory(UUID uuid) {
		this.uuid = uuid;
		this.animationFinished = false;
		inventory = Bukkit.createInventory(this, 27, "BanRoulette");
		initialize();
	}
	
	private void initialize() {
		Random rand = new Random();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)rand.nextInt(16));
			inventory.setItem(i, item);
		}
		ItemStack start = new ItemStack(Material.STAINED_CLAY, 1, (short)13);
		ItemMeta startMeta = start.getItemMeta();
		startMeta.setDisplayName("Start BanRoulette");
		start.setItemMeta(startMeta);
		inventory.setItem(13, start);
	}

	@Override
    public Inventory getInventory() {
        return inventory;
    }
	
	public void startBanRoulette() {
		Player p = Bukkit.getPlayer(uuid);
		List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		List<ItemStack> heads = new ArrayList<ItemStack>();
		for (Player player : players) {
			if (!player.getUniqueId().equals(uuid)) {
				heads.add(getHead(player));
			}
		}
		if (heads.size() <= 0) {
			p.sendMessage("Not enough players connected!");
			p.closeInventory();
			return;
		}
		new BukkitRunnable() {
			int counter = 20;
			
			@Override
			public void run() {
				if (counter > 0) {
					Random rand = new Random();
					for (int i = 0; i < 9; i++) {
						inventory.setItem(i + 9, heads.get(rand.nextInt(heads.size())));
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_SNARE, 1, 5);
					p.updateInventory();
					counter--;
				} else {
					this.cancel();
					confirmBan();
				}
			}
		}.runTaskTimer(Main.instance, 0, 5);
	}
	
	private ItemStack getHead(Player p) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwningPlayer(p);
		headMeta.setDisplayName("Ban " + p.getName());
		head.setItemMeta(headMeta);
		return head;
	}
	
	private void confirmBan() {
		for (int i = 0; i < inventory.getSize(); i++) {
			if (i != 13) {
				inventory.setItem(i, null);
			}
		}
		inventory.setItem(21, new ItemStack(Material.CONCRETE, 1, (short) 13));
		inventory.setItem(23, new ItemStack(Material.CONCRETE, 1, (short) 14));
	}
	
	public void banPlayer() {
		OfflinePlayer banned = ((SkullMeta)inventory.getItem(13).getItemMeta()).getOwningPlayer();
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "[BanRoulette] " + banned.getName() + " was chosen to get banned!");
	}
}
