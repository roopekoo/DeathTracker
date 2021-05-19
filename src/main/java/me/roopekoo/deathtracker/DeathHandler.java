package me.roopekoo.deathtracker;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class DeathHandler implements Listener {
	private final DeathData deathData = DeathTracker.getPlugin().get_file();

	@EventHandler public void on(PlayerDeathEvent e) {
		Player player = e.getEntity();
		deathData.addDeath(player);
	}

	@EventHandler public void on(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPlayedBefore()) {
			deathData.addNewPlayer(p);
		} else {
			int totalPlayTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
			deathData.updateTime(p.getUniqueId().toString(), totalPlayTime);
		}
	}

	@EventHandler public void on(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		int totalPlayTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
		deathData.updateTime(p.getUniqueId().toString(), totalPlayTime);
	}
}
