package me.roopekoo.deathcounter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathHandler implements Listener {

    @EventHandler
    public void on(PlayerDeathEvent e)
    {
        DeathCounter.getPlugin().get_config().add(e);
    }

}
