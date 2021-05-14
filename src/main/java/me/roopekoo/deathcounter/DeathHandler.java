package me.roopekoo.deathcounter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DeathHandler implements Listener {

    @EventHandler
    public void on(PlayerDeathEvent e) {
        Player player = e.getEntity();
        DeathCounter.getPlugin().get_file().addDeath(player);
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DeathData d = DeathCounter.getPlugin().get_file();
        if (!p.hasPlayedBefore()) {
            d.addNewPlayer(p);
        } else {
            d.updateTime(p);
        }
    }
    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        DeathCounter.getPlugin().get_file().updateTime(p);
    }
}
