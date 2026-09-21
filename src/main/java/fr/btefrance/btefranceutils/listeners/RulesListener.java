package fr.btefrance.btefranceutils.listeners;

import fr.btefrance.btefranceutils.utils.Rules;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RulesListener implements Listener {
    private void sendRules(Player player) {
        MiniMessage mm = MiniMessage.miniMessage();
        // Use https://webui.advntr.dev/ to visualize the MiniMessage
        player.sendMessage(mm.deserialize("<blue>═══════════════════════════════</blue><br><gold>Bienvenue sur BTE France! Welcome to BTE France!</gold><br><gold>Tu dois accepter les règles pour jouer. You must accept the rules to play.</gold><br><click:open_url:'https://discord.com/channels/694003889506091100/793846913203306526'><br><u><white>Lien des règles. Link to the rules.</white></u></click><br><br><click:run_command:'/rules accept'><dark_green>[Accepter ✓]</dark_green></click>  <click:run_command:'/rules deny'><dark_red>[Refuser ✗]</dark_red></click><br><blue>═══════════════════════════════</blue>"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!Rules.instance().hasAcceptedRules(e.getPlayer().getUniqueId())) {
            sendRules(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.hasChangedBlock() && !Rules.instance().hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            sendRules(e.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!Rules.instance().hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            sendRules(e.getPlayer());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Rules.instance().hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            sendRules(e.getPlayer());
        }
    }
}
