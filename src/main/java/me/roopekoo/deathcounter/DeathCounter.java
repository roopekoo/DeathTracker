package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DeathCounter extends JavaPlugin {
    private static DeathCounter plugin = null;
    public static DeathCounter getPlugin() {
        return plugin;
    }
    private final DeathDataFile dataFile = new DeathDataFile();
    @Override
    public void onEnable() {
        plugin = this;
        Objects.requireNonNull(this.getCommand("getdeaths")).setExecutor(new GetDeaths());
        Objects.requireNonNull(this.getCommand("getdeaths")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(this.getCommand("deathstats")).setExecutor(new DeathStats());
        Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
        DeathCounter.getPlugin().get_file().updateConfig();
    }
    public DeathDataFile get_file() {
        return dataFile;
    }
}
