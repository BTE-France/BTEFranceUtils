package fr.maxyolo01.btefranceutils.commands.schematicsync;

import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.commands.SegmentedCommandExecutor;
import fr.maxyolo01.btefranceutils.sync.SchematicSynchronizationService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BulkUpdateCommandSegment extends SegmentedCommandExecutor {

    public BulkUpdateCommandSegment() {
        this.setPermissionNode("btefrance.schematicsync.bulk");
        this.setErrorMessage(ChatColor.RED + "Invalid syntax: /syncschems bulk <start|status|cancel|unsubscribe>");
        this.addSegment("start", this::start);
        this.addSegment("status", this::status);
        this.addSegment("cancel", this::stop);
        this.addSegment("subscribe", this::subscribe);
        this.addSegment("unsubscribe", this::unsubscribe);
    }

    public boolean start(CommandSender sender, Command command, String label, String[] args) {
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || !service.isRunning()) {
            sender.sendMessage(ChatColor.DARK_RED + "Schematic synchronization service is not running");
            return true;
        }
        service.startBulkUpdate(sender);
        return true;
    }

    public boolean status(CommandSender sender, Command command, String label, String[] args) {
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || !service.isRunning()) {
            sender.sendMessage(ChatColor.DARK_RED + "Schematic synchronization service is not running");
            return true;
        }
        SchematicSynchronizationService.BulkUpdateTask task = service.getBulkUpdateTask();
        if (task == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No bulk update task is running");
            return true;
        }
        task.sendProgressTo(sender);
        return true;
    }

    public boolean stop(CommandSender sender, Command command, String label, String[] args) {
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || !service.isRunning()) {
            sender.sendMessage(ChatColor.DARK_RED + "Schematic synchronization service is not running");
            return true;
        }
        SchematicSynchronizationService.BulkUpdateTask task = service.getBulkUpdateTask();
        if (task == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No bulk update task is running");
            return true;
        }
        task.subscribeSender(sender); // Makes sure sender receives termination feedback
        task.cancel();
        return true;
    }

    public boolean subscribe(CommandSender sender, Command command, String label, String[] args) {
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || !service.isRunning()) {
            sender.sendMessage(ChatColor.DARK_RED + "Schematic synchronization service is not running");
            return true;
        }
        SchematicSynchronizationService.BulkUpdateTask task = service.getBulkUpdateTask();
        if (task == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No bulk update task is running");
            return true;
        }
        task.subscribeSender(sender);
        return true;
    }

    public boolean unsubscribe(CommandSender sender, Command command, String label, String[] args) {
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null || !service.isRunning()) {
            sender.sendMessage(ChatColor.DARK_RED + "Schematic synchronization service is not running");
            return true;
        }
        SchematicSynchronizationService.BulkUpdateTask task = service.getBulkUpdateTask();
        if (task == null) {
            sender.sendMessage(ChatColor.DARK_RED + "No bulk update task is running");
            return true;
        }
        task.unsubscribeSender(sender);
        return true;
    }

}
