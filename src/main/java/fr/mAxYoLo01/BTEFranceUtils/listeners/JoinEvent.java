package fr.mAxYoLo01.BTEFranceUtils.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.mAxYoLo01.BTEFranceUtils.Main;

public class JoinEvent implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			String publicMsg = Main.instance.config.getString("publicWelcomeMessage");
			e.setJoinMessage(publicMsg.replace('&', ChatColor.COLOR_CHAR).replace("{player}", p.getName()).replace("{count}", "" + Bukkit.getOfflinePlayers().length));
			
			List<String> privateMsgList = Main.instance.config.getStringList("privateWelcomeMessage");
			for(String msg : privateMsgList) {
				p.sendMessage(msg.replace('&', ChatColor.COLOR_CHAR).replace("{player}", p.getName()).replace("{online}", "" + Bukkit.getOnlinePlayers().size()));
			}
		}
	}
}
