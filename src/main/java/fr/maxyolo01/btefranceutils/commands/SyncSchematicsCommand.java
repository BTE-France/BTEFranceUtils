package fr.maxyolo01.btefranceutils.commands;

import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.sync.SchematicSynchronizationService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyncSchematicsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (! player.hasPermission("btefrance.schematicsync.process")) {
                return false;
            }
        }
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || ! service.isRunning()) {
            sender.sendMessage(ChatColor.RED + "Schematic synchronization service is not running");
        } else {
            service.processExistingSchematics(sender);
        }
        return true;
    }
}
