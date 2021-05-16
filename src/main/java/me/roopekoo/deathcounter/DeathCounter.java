package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DeathCounter extends JavaPlugin
{
	private static DeathCounter plugin = null;

	public static DeathCounter getPlugin()
	{
		return plugin;
	}

	private final DeathData dataFile = new DeathData();

	@Override
	public void onEnable()
	{
		plugin = this;
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setExecutor(new GetDeaths());
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setTabCompleter(new TabCompletion());
		Objects.requireNonNull(plugin.getCommand("deathstats")).setExecutor(new DeathStats());
		Bukkit.getPluginManager().registerEvents(new DeathHandler(), plugin);
		DeathCounter.getPlugin().get_file().initializePlayerData();
	}

	@Override
	public void onDisable()
	{
		DeathCounter.getPlugin().get_file().writeAllToDeathData();
	}

	public DeathData get_file()
	{
		return dataFile;
	}
}
