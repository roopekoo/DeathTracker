package me.roopekoo.deathtracker;

import me.roopekoo.deathtracker.commands.DeathStats;
import me.roopekoo.deathtracker.commands.GetDeaths;
import me.roopekoo.deathtracker.commands.TabCompletion;
import me.roopekoo.deathtracker.events.DeathHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 Main plugin class */
public class DeathTracker extends JavaPlugin {
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	private static DeathTracker plugin = null;
	private final DeathData dataFile = new DeathData();

	/**
	 Get DeathTracker instance
	 @return this class
	 */
	public static DeathTracker getPlugin() {
		return plugin;
	}

	/**
	 Initialize plugin on startup
	 */
	@Override public void onEnable() {
		plugin = this;
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setExecutor(new GetDeaths());
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setTabCompleter(new TabCompletion());
		Objects.requireNonNull(plugin.getCommand("deathstats")).setExecutor(new DeathStats());
		Objects.requireNonNull(plugin.getCommand("deathstats")).setTabCompleter(new TabCompletion());
		Bukkit.getPluginManager().registerEvents(new DeathHandler(), plugin);
		loadLang();
		DeathTracker.getPlugin().get_file().initializePlayerData();
	}

	/**
	 Get DeathData instance
	 @return DeathData class
	 */
	public DeathData get_file() {
		return dataFile;
	}

	/**
	 Load the lang.yml file.
	 */
	public void loadLang() {
		File lang = new File(getDataFolder(), "lang.yml");
		if(!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
			} catch(IOException e) {
				// Send notice
				e.printStackTrace();
				Bukkit.getLogger().log(Level.SEVERE, Lang.TITLE+Lang.FILE_CREATE_FAIL1.toString());
				Bukkit.getLogger().log(Level.SEVERE, Lang.TITLE+Lang.FILE_CREATE_FAIL2.toString());
				// Without Messages loaded, we can't send the messages
				plugin.setEnabled(false);
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for(Lang item: Lang.values()) {
			if(conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		DeathTracker.LANG = conf;
		DeathTracker.LANG_FILE = lang;
		try {
			conf.save(getLangFile());
		} catch(IOException e) {
			//Saving failed for some reason
			Bukkit.getLogger().log(Level.WARNING, Lang.TITLE+Lang.SAVE_FAIL1.toString());
			Bukkit.getLogger().log(Level.WARNING, Lang.TITLE+Lang.SAVE_FAIL2.toString());
			e.printStackTrace();
		}
	}

	/**
	 Get the lang.yml file.
	 @return The lang.yml file.
	 */
	public File getLangFile() {
		return LANG_FILE;
	}

	/**
	 Save data from memory to file on shutdown
	 */
	@Override public void onDisable() {
		DeathTracker.getPlugin().get_file().writeAllToDeathData();
	}
}
