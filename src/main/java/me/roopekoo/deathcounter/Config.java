package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Config {

    private static final String BASEDIR = "plugins/DeathTracker";
    private static final String CONFIGPATH = "/config.yml";
    private final YamlConfiguration config;
    private final File ff = new File(BASEDIR + CONFIGPATH);

    public Config() {
        File f = new File(BASEDIR);
        if (!f.exists()) {
            f.mkdir();
        }
        if (!f.exists()) {
            try {
                ff.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(ff);

    }
    public void add(PlayerDeathEvent event)
    {
        double count = 1;
        if (config.isSet("players." + event.getEntity().getUniqueId()))
        {
            count = config.getInt("players." + event.getEntity().getUniqueId() + ".deaths") + 1;
        }
        config.set("players." + event.getEntity().getUniqueId() + ".deaths", (int)(count));
        double time = event.getEntity().getStatistic(Statistic.PLAY_ONE_MINUTE);
        config.set("players." + event.getEntity().getUniqueId() + ".playtimeTicks", (int)(time));
        ForkJoinPool.commonPool().submit(() ->
        {
            try
            {
                config.save(ff);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
    public boolean hasPlayer(String uuid) {
        ConfigurationSection sec = config.getConfigurationSection("players");
        return sec != null && sec.contains(uuid);
    }

    public int getDeathsFor(UUID player)
    {
        return config.getInt("players." + player.toString() + ".deaths");
    }

    public double getPlayTimeFor(UUID player)
    {
        return config.getInt("players." + player.toString() + ".playtimeTicks");
    }

    public void updateConfig()
    {
        double count = 0;
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String uuid = offlinePlayer.getUniqueId().toString();
            if (!config.isSet("players." + uuid)) {
                config.set("players." + uuid + ".deaths", (int) (count));
            }
            double time = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
            config.set("players." + uuid + ".playtimeTicks", (int) (time));
        }
        System.out.println("Offline players death/playtime updated");
    }
}
