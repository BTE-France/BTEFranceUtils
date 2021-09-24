package fr.maxyolo01.btefranceutils.commands.schematicsync;

import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.commands.SegmentedCommandExecutor;
import fr.maxyolo01.btefranceutils.sync.SchematicSynchronizationService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ServiceCommandSegment extends SegmentedCommandExecutor {

    public ServiceCommandSegment() {
        this.setPermissionNode("btefrance.schematicsync.service");
        this.setErrorMessage(ChatColor.RED + "Invalid syntax: /syncschems service <start|status|stop>");
        this.addSegment("status", this::status);
        this.addSegment("start", this::start);
        this.addSegment("stop", this::stop);
        this.addSegment("terminate", this::terminate);
    }

    public boolean status(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        String state = "Schematic synchronization service status: " + ChatColor.BOLD;
        if (service == null) {
            state += ChatColor.DARK_RED + "MISSING";
        } else if (service.isRunning()){
            state += ChatColor.GREEN + "RUNNING";
        } else {
            state += ChatColor.RED + "STOPPED";
        }
        sender.sendMessage(state);
        return true;
    }

    public boolean start(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Service is missing, cannot start it.");
        } else if (service.isRunning()){
            sender.sendMessage(ChatColor.RED + "Service is already running.");
        } else {
            try {
                service.start();
                sender.sendMessage(ChatColor.GREEN + "Service has been started.");
            } catch(Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.DARK_RED + "An exception occurred when starting the service, check the console for more details.");
            }
        }
        return true;
    }

    public boolean stop(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Service is missing, cannot stop it.");
        } else if (!service.isRunning()){
            sender.sendMessage(ChatColor.RED + "Service is already stopped.");
        } else {
            try {
                service.stop();
                sender.sendMessage(ChatColor.GREEN + "Service has been stopped.");
            } catch(Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.DARK_RED + "An exception occurred when stopping the service, check the console for more details.");
            }
        }
        return true;
    }

    public boolean terminate(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;
        SchematicSynchronizationService service = BteFranceUtils.instance().schematicSynchronizationService();
        if (service == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Service is missing, cannot terminate it.");
        } else if (!service.isRunning()){
            sender.sendMessage(ChatColor.RED + "Service is already stopped.");
        } else {
            try {
                service.terminate();
                sender.sendMessage(ChatColor.GREEN + "Service has been terminated.");
            } catch(Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.DARK_RED + "An exception occurred when terminating the service, check the console for more details.");
            }
        }
        return true;
    }

}
