package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathCounter extends JavaPlugin {
    private static DeathCounter plugin = null;
    public static DeathCounter getPlugin() {
        return plugin;
    }
    private final Config cfg = new Config();
    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("getdeaths").setExecutor(new GetDeaths());
        Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
        DeathCounter.getPlugin().get_config().updateConfig();
    }
    public Config get_config() {
        return cfg;
    }
}
