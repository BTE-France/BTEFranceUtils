package fr.btefrance.btefranceutils;

import fr.btefrance.btefranceutils.commands.LuckpermsCommand;
import fr.btefrance.btefranceutils.commands.RulesCommand;
import fr.btefrance.btefranceutils.listeners.RulesListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BTEFranceUtils extends JavaPlugin {
    private static BTEFranceUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new RulesListener(), this);

        getCommand("rules").setExecutor(new RulesCommand());
        getCommand("promote").setExecutor(new LuckpermsCommand());
        getCommand("demote").setExecutor(new LuckpermsCommand());
    }

    public static BTEFranceUtils instance() {
        return instance;
    }
}