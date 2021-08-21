package fr.maxyolo01.btefranceutils;

import java.io.File;

import fr.maxyolo01.btefranceutils.listeners.JoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.maxyolo01.btefranceutils.commands.BTEFranceCommand;
import fr.maxyolo01.btefranceutils.commands.BanRouletteCommand;
import fr.maxyolo01.btefranceutils.listeners.BanRouletteEvent;

public class BteFranceUtils extends JavaPlugin {
	public static BteFranceUtils instance;
	public File configFile;
	public FileConfiguration config;

	public void onEnable() {
		instance = this;
		getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &aenabled&e!"));
		
		loadConfig();
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new JoinEvent(), this);
		pm.registerEvents(new BanRouletteEvent(), this);
		
		getCommand("btefrance").setExecutor(new BTEFranceCommand());
		getCommand("banroulette").setExecutor(new BanRouletteCommand());
	}
	
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &4disabled&e!"));
	}
	
	public void loadConfig() {
		configFile = new File(getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}
	}
}
