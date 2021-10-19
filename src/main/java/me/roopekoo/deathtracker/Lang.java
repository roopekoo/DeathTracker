package me.roopekoo.deathtracker;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 An enum for requesting strings from the language file.
 @author roopekoo */
public enum Lang {
	TITLE("title-name", "&4[&8Death&7Tracker&4]: "),
	NO_PERM("no-perm", "&cYou not have permission to do that!"),
	INVALID_PLAYER("invalid-player", "&cThat player has never played here!"),
	INVALID_PARAM("invalid-param", "&cInvalid parameter!"),
	TOO_MANY_PARAMS("too-many-params", "&cToo many parameters!"),
	NOT_ENOUGH_PARAMS("not-enough-params", "&cNot enough parameters!"),
	FILE_CREATE_FAIL1("file-create-fail1", "Couldn't create message file."),
	FILE_CREATE_FAIL2("file-create-fail2", "This is a fatal error. Now disabling"),
	SAVE_FAIL1("save-fail1", "Failed to save lang.yml."),
	SAVE_FAIL2("save-fail2", "Report this stack trace to Roopekoo!"),
	EMPTY_TOP_LIST("empty-top-list", "&cThis top 10 list is empty!"),
	PLAYER_DEATHS("player-deaths", "&e{pl} &7has &4{n} &7{death(s)} with a playtime of &a{pt}"),
	PLAYER_DEATHTIME("player-deathtime",
	                 "&e{pl} &7has a death rate of &4{dr} &7and dies approximately once per "+"&a{t}"),
	IMMORTAL_TITLE("immortal-title", "&6&nTop {n} immortal {player(s)}:&r"),
	IMMORTAL_STATS("immortal-stats", "{i}: &e{pl}&7, playtime of &a{pt}"),
	DEATHRATE_STATS("deathrate-stats", "{i}: &e{pl}&7, deathrate of &a{dr}"),
	LOW_DEATHRATE_TITLE("low-deathrate-title", "&6&nTop {n} low-deathrate {player(s)}:&r"),
	HIGH_DEATHRATE_TITLE("high-deathrate-title", "&6&nTop {n} high-deathrate {player(s)}:&r"),
	DEATH_STATS("death-stats", "{i}: &e{pl}, &4{n} &7{death(s)}, playtime of &a{pt}"),
	LOW_DEATHS_TITLE("low-deaths-title", "&6&nTop {n} {player(s)} with low mortality:&r"),
	HIGH_DEATHS_TITLE("high-deaths-title", "&6&nTop {n} {player(s)} with high mortality:&r"),
	TOT_DEATHRATE_TITLE("total-deathrate-title", "&6&nTotal deathrate of {n} {player(s)}:&r"),
	TOT_DEATHS_TITLE("total-deaths-title", "&6&nTotal deaths of {n} {player(s)}:&r"),
	TOT_DEATHRATE_STATS("total-deathrate-stats", "&7Total deathrate of &4{dr} &7and a death happens once per &a{t}"),
	TOT_DEATH_STATS("total-deaths-stats", "&7Total of &4{n} &7{death(s)} with a playtime of &a{pt}"),
	PLAYER("player", "player"),
	PLAYERS("players", "players"),
	DEATH("death", "death"),
	DEATHS("deaths", "deaths"),
	SEC("sec", "second"),
	SECS("secs", "seconds"),
	MIN("min", "minute"),
	MINS("mins", "minutes"),
	HOUR("hour", "hour"),
	HOURS("hours", "hours"),
	DAY("day", "day"),
	DAYS("days", "days"),
	YEAR("year", "year"),
	YEARS("years", "years");

	private static YamlConfiguration LANG;
	private final String path;
	private final String def;

	/**
	 Lang enum constructor.
	 @param path
	 The string path.
	 @param start
	 The default string.
	 */
	Lang(String path, String start) {
		this.path = path;
		this.def = start;
	}

	/**
	 Set the {@code YamlConfiguration} to use.
	 @param config
	 The config to set.
	 */
	public static void setFile(YamlConfiguration config) {
		LANG = config;
	}

	/**
	 Get the path to the string.
	 @return The path to the string.
	 */
	public String getPath() {
		return this.path;
	}

	@Override public String toString() {
		String s = LANG.getString(this.path, def);
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	/**
	 Get the default value of the path.
	 @return The default value of the path.
	 */
	public String getDefault() {
		return this.def;
	}
}
