package me.roopekoo.deathtracker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DeathTracker extends JavaPlugin
{
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	private static DeathTracker plugin = null;
	private final DeathData dataFile = new DeathData();

	public static DeathTracker getPlugin()
	{
		return plugin;
	}

	@Override public void onEnable()
	{
		plugin = this;
		Objects.requireNonNull(plugin.getCommand("getdeaths"))
		       .setExecutor(new GetDeaths());
		Objects.requireNonNull(plugin.getCommand("getdeaths"))
		       .setTabCompleter(new TabCompletion());
		Objects.requireNonNull(plugin.getCommand("deathstats"))
		       .setExecutor(new DeathStats());
		Bukkit.getPluginManager().registerEvents(new DeathHandler(), plugin);
		DeathTracker.getPlugin().get_file().initializePlayerData();
		loadLang();
	}

	@Override public void onDisable()
	{
		DeathTracker.getPlugin().get_file().writeAllToDeathData();
	}

	public DeathData get_file()
	{
		return dataFile;
	}

	/**
	 Load the lang.yml file.
	 */
	public void loadLang()
	{
		File lang = new File(getDataFolder(), "lang.yml");
		if(!lang.exists())
		{
			try
			{
				getDataFolder().mkdir();
				lang.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace(); // So they notice
				System.out.println(
						"[PluginName] Couldn't create language file"+".");
				System.out.println(
						"[PluginName] This is a fatal error. Now disabling");
				this.setEnabled(false); // Without it loaded, we can't send
				// them
				// messages
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for(Lang item: Lang.values())
		{
			if(conf.getString(item.getPath()) == null)
			{
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		DeathTracker.LANG = conf;
		DeathTracker.LANG_FILE = lang;
		try
		{
			conf.save(getLangFile());
		}
		catch(IOException e)
		{
			System.out.println("PluginName: Failed to save lang.yml.");
			System.out.println(
					"PluginName: Report this stack trace to Roopekoo.");
			e.printStackTrace();
		}
	}

	/**
	 Gets the lang.yml config.
	 @return The lang.yml config.
	 */
	public YamlConfiguration getLang()
	{
		return LANG;
	}

	/**
	 Get the lang.yml file.
	 @return The lang.yml file.
	 */
	public File getLangFile()
	{
		return LANG_FILE;
	}
}
