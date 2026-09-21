package fr.btefrance.btefranceutils.commands;

import fr.btefrance.btefranceutils.BTEFranceUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LuckpermsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 2) {
            sender.sendMessage(Component.text("Usage: /" + cmd.toString() + " [username] [rank]", NamedTextColor.RED));
            return true;
        }
        if (!sender.hasPermission("luckperms.user.promote")) {
            sender.sendMessage(Component.text("You do not have the permission!", NamedTextColor.RED));
            return true;
        }

        String playerName;
        if (args.length > 0) {
            playerName = args[0];
        } else {
            // Username arg not given, therefore fallbacking to the sender of the command (not available in Console!)
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("This command can only be run by a player!", NamedTextColor.RED));
                return true;
            }
            playerName = player.getName();
        }

        OfflinePlayer player = BTEFranceUtils.instance().getServer().getOfflinePlayer(playerName);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage(Component.text("This player never connected to the server!", NamedTextColor.RED));
            return true;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        Track track = luckPerms.getTrackManager().getTrack("rank");
        String newGroupName;
        if (args.length == 2) {
            newGroupName = args[1];
        } else {
            // Get the next rank in the track for the user
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (cmd.getName().equalsIgnoreCase("promote")) {
                newGroupName = track.getNext(luckPerms.getGroupManager().getGroup(user.getPrimaryGroup()));
            } else {
                newGroupName = track.getPrevious(luckPerms.getGroupManager().getGroup(user.getPrimaryGroup()));
            }
        }

        if (newGroupName == null) {
            sender.sendMessage(Component.text("This group does not exist!", NamedTextColor.RED));
            return true;
        }
        Group group = luckPerms.getGroupManager().getGroup(newGroupName);
        if (group == null || !track.getGroups().contains(group.getName())) {
            sender.sendMessage(Component.text("The rank " + args[1] + " does not exist!", NamedTextColor.RED));
            return true;
        }

        // Remove all previous rank nodes and add the new one
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
            user.getNodes(NodeType.INHERITANCE).forEach(node -> {
                if (track.getGroups().contains(node.getGroupName())) {
                    user.data().remove(node);
                }
            });

            user.data().add(InheritanceNode.builder(group).build());
            sender.sendMessage(Component.text("Set rank of " + user.getUsername() + " to " + group.getName(), NamedTextColor.GOLD));
        });
        return true;
    }

    // TODO: add autocomplete for rank
}
