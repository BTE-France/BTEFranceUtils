package fr.maxyolo01.btefranceutils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class SegmentedCommandExecutor implements CommandExecutor, TabCompleter {

    private final Map<String, CommandExecutor> executors = new HashMap<>();
    private String permissionNode;
    private String errorMessage = ChatColor.RED + "Invalid command";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 ||
                !this.executors.containsKey(args[0]) ||
                (this.permissionNode != null && !sender.hasPermission(this.getPermissionNode()))) {
            sender.sendMessage(this.getErrorMessage());
            return false;
        }
        return this.executors.get(args[0]).onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> matches = new ArrayList<>();
        String startMatch = args.length > 0 ? args[0] : "";
        for (String key: this.executors.keySet()) if (key.startsWith(startMatch)) {
            CommandExecutor exec = this.executors.get(key);
            boolean hasPerm = !(exec instanceof SegmentedCommandExecutor) || sender.hasPermission(((SegmentedCommandExecutor) exec).getPermissionNode());
            if (!hasPerm) continue;
            if (key.equals(startMatch) && args.length > 1 && exec instanceof TabCompleter) {
                return ((TabCompleter) exec).onTabComplete(sender, command, key, Arrays.copyOfRange(args, 1, args.length));
            }
            matches.add(key);
        }
        return matches;
    }

    protected void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }

    public String getPermissionNode() {
        return this.permissionNode;
    }

    protected void addSegment(String segment, CommandExecutor executor) {
        this.executors.put(segment, executor);
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

}
