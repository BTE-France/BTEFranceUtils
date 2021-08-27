package fr.maxyolo01.btefranceutils;

import java.io.File;

import fr.maxyolo01.btefranceutils.listeners.JoinEventListener;
import fr.maxyolo01.btefranceutils.sync.SchematicSynchronizationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.maxyolo01.btefranceutils.commands.BTEFranceCommand;
import fr.maxyolo01.btefranceutils.commands.BanRouletteCommand;
import fr.maxyolo01.btefranceutils.listeners.BanRouletteEventListener;

public class BteFranceUtils extends JavaPlugin {
	private static BteFranceUtils instance;

	private File configFile;
	private FileConfiguration config;

	public void onEnable() {
		instance = this;
		this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &aenabled&e!"));
		
		this.loadConfig();
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new JoinEventListener(), this);
		pm.registerEvents(new BanRouletteEventListener(), this);
		
		this.getCommand("btefrance").setExecutor(new BTEFranceCommand());
		this.getCommand("banroulette").setExecutor(new BanRouletteCommand());
	}
	
	public void onDisable() {
		this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &4disabled&e!"));
	}
	
	public void loadConfig() {
		this.configFile = new File(getDataFolder(), "config.yml");
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}
	}

	public FileConfiguration config() {
		return this.config;
	}

	public static BteFranceUtils instance() {
		return instance;
	}

}
