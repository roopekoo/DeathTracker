package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DeathCounter extends JavaPlugin {
    private static DeathCounter plugin = null;
    public static DeathCounter getPlugin() {
        return plugin;
    }
    private final DeathData cfg = new DeathData();
    @Override
    public void onEnable() {
        plugin = this;
        Objects.requireNonNull(this.getCommand("getdeaths")).setExecutor(new GetDeaths());
        Objects.requireNonNull(this.getCommand("getdeaths")).setTabCompleter(new TabCompletion());
        Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
        DeathCounter.getPlugin().get_config().updateConfig();
    }
    public DeathData get_config() {
        return cfg;
    }
}
