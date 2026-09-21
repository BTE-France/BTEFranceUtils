package fr.btefrance.btefranceutils.commands;

import fr.btefrance.btefranceutils.utils.Rules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /rules accept|deny", NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            Rules.instance().acceptRules(player.getUniqueId());
            player.sendMessage(Component.text("✓ Tu as accepté les règles, bienvenue! You have accepted the rules, welcome!", NamedTextColor.GREEN));
        } else if (args[0].equalsIgnoreCase("deny")) {
            Rules.instance().denyRules(player.getUniqueId());
            MiniMessage mm = MiniMessage.miniMessage();
            player.kick(mm.deserialize("<red>✗ Tu as refusé les règles, tu ne peux pas jouer sur ce serveur.<br>✗ You have denied the rules, you cannot play on this server."));
        } else {
            player.sendMessage(Component.text("Usage: /rules accept|deny", NamedTextColor.RED));
        }

        return true;
    }
}
