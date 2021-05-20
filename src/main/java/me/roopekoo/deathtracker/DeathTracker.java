package me.roopekoo.deathtracker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DeathTracker extends JavaPlugin {
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	private static DeathTracker plugin = null;
	private final DeathData dataFile = new DeathData();

	public static DeathTracker getPlugin() {
		return plugin;
	}

	@Override public void onEnable() {
		plugin = this;
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setExecutor(new GetDeaths());
		Objects.requireNonNull(plugin.getCommand("getdeaths")).setTabCompleter(new TabCompletion());
		Objects.requireNonNull(plugin.getCommand("deathstats")).setExecutor(new DeathStats());
		Objects.requireNonNull(plugin.getCommand("deathstats")).setTabCompleter(new TabCompletion());
		Bukkit.getPluginManager().registerEvents(new DeathHandler(), plugin);
		DeathTracker.getPlugin().get_file().initializePlayerData();
		loadLang();
	}

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
				System.out.println(Lang.TITLE+"Couldn't create language file.");
				System.out.println(Lang.TITLE+"This is a fatal error. Now disabling");
				// Without it loaded, we can't send them messages
				this.setEnabled(false);
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
			System.out.println(Lang.TITLE+"Failed to save lang.yml.");
			System.out.println(Lang.TITLE+"Report this stack trace to Roopekoo.");
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

	@Override public void onDisable() {
		DeathTracker.getPlugin().get_file().writeAllToDeathData();
	}
}

