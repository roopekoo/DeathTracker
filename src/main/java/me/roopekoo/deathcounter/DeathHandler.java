package me.roopekoo.deathcounter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DeathHandler implements Listener {

    @EventHandler
    public void on(PlayerDeathEvent e)
    {
        Player player = e.getEntity();
        DeathCounter.getPlugin().get_config().add(player);
    }

    @EventHandler
    public void on(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if(p.hasPlayedBefore())
        {
            Player player = e.getPlayer();
            DeathCounter.getPlugin().get_config().add(player);
        }
    }
}
