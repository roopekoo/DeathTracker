package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class DeathData {

    private static final String BASEDIR = "plugins/DeathTracker";
    private static final String DEATHDATAPATH = "/deathData.yml";
    private final YamlConfiguration deathData;
    private final File ff = new File(BASEDIR + DEATHDATAPATH);

    public DeathData() {
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
        deathData = YamlConfiguration.loadConfiguration(ff);

    }
    public void add(Player player)
    {
        int count = 1;
        UUID uuid = player.getUniqueId();
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if (hasPlayer(uuid))
        {
            count += deathData.getInt("players." + uuid + ".deaths");
        }
        else
        {
            deathData.set("players." + uuid + ".resetTime", time);
            count = 0;
        }

        int resetTime = getResetTimeFor(uuid);
        deathData.set("players." + uuid + ".deaths", count);
        deathData.set("players." + uuid + ".playtimeTicks", time-resetTime);

        ForkJoinPool.commonPool().submit(() ->
        {
            try
            {
                deathData.save(ff);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
    public boolean hasPlayer(UUID uuid) {
        ConfigurationSection sec = deathData.getConfigurationSection("players");
        return sec != null && sec.contains(uuid.toString());
    }

    public int getDeathsFor(UUID player)
    {
        // Player is not on the deathData file
        if(!hasPlayer(player))
        {
            return 0;
        }
        return deathData.getInt("players." + player + ".deaths");
    }

    public int getPlayTimeFor(UUID player)
    {
        // Player is not on the deathData file
        if(!hasPlayer(player))
        {
            return 0;
        }
        return deathData.getInt("players." + player + ".playtimeTicks");
    }
    public int getResetTimeFor(UUID player)
    {
        // Player is not on the deathData file
        if(!hasPlayer(player))
        {
            return 0;
        }
        return deathData.getInt("players." + player + ".resetTime");
    }


    public void updateConfig()
    {
        int count = 0;
        int time;
        int resetTime;

        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        // Go through all offline players
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            UUID uuid = offlinePlayer.getUniqueId();
            time = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
            // Player is not yet on the deathData file
            if (!hasPlayer(uuid)) {
                deathData.set("players." + uuid + ".resetTime", time);
                deathData.set("players." + uuid + ".deaths", count);
            }
            resetTime = getResetTimeFor(uuid);
            deathData.set("players." + uuid + ".playtimeTicks", time-resetTime);
        }
        //Try to save the changes
        ForkJoinPool.commonPool().submit(() ->
        {
            try
            {
                deathData.save(ff);
                System.out.println("Offline players death/playtime updated");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
}
