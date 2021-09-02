package fr.maxyolo01.btefranceutils;

import java.io.File;
import java.io.IOException;

import fr.maxyolo01.btefranceutils.commands.SyncSchematicsCommand;
import fr.maxyolo01.btefranceutils.listeners.JoinEventListener;
import fr.maxyolo01.btefranceutils.sync.SchematicSyncConfig;
import fr.maxyolo01.btefranceutils.sync.SchematicSynchronizationService;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
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

	private FileConfiguration config;
	private SchematicSynchronizationService schematicSyncService;

	public void onEnable() {
		instance = this;
		this.loadConfig();
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new JoinEventListener(), this);
		pm.registerEvents(new BanRouletteEventListener(), this);
		
		this.getCommand("btefrance").setExecutor(new BTEFranceCommand());
		this.getCommand("banroulette").setExecutor(new BanRouletteCommand());
		this.getCommand("syncschems").setExecutor(new SyncSchematicsCommand());

		DiscordSRV.api.subscribe(this);
		this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &aenabled&e!"));

	}

	@github.scarsz.discordsrv.api.Subscribe
	public void discordReadyEvent(DiscordReadyEvent event) {
		SchematicSyncConfig syncConfig = new SchematicSyncConfig(this.config.getConfigurationSection("schematicSync"));
		try {
			this.schematicSyncService = syncConfig.makeService();
			this.schematicSyncService.setup();
			this.schematicSyncService.start();
		} catch (SchematicSyncConfig.InvalidSchematicSyncConfigException e) {
			this.getLogger().severe("Configuration problem for the schematic synchronization service: " + e.getMessage());
		} catch (IOException e) {
			this.getLogger().severe("An error happened when setting up the schematic synchronization service. It is disabled.");
			e.printStackTrace();
		} catch (Exception e) {
			this.getLogger().severe("Failed to setup or start the schematic synchronization service!");
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		if (this.schematicSyncService != null && this.schematicSyncService.isRunning()) {
			try {
				this.schematicSyncService.stop();
			} catch (InterruptedException e) {
				this.getLogger().severe("Failed to stop the schematic synchronization service!");
				e.printStackTrace();
			}
		}
		this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dBTEFranceUtils&8] &ehas been &4disabled&e!"));
	}
	
	public void loadConfig() {
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}

	public FileConfiguration config() {
		return this.config;
	}

	public static BteFranceUtils instance() {
		return instance;
	}

	public SchematicSynchronizationService schematicSynchronizationService() {
		return this.schematicSyncService;
	}

}
