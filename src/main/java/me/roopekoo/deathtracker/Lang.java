package me.roopekoo.deathtracker;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 An enum for requesting strings from the language file.
 @author roopekoo */
public enum Lang
{
	TITLE("title-name", "&4[&8Death&7Tracker&4]: "),
	NO_PERM("no-perm", "&cYou not have permission to do that!"),
	PLAYER_MISSING("player-missing", "&cPlease supply a player name!"),
	INVALID_PLAYER("invalid-player", "&cThat player has never played here!"),
	INVALID_PARAMETER("invalid-parameter",
	                  "&cInvalid parameter! Expected &8time &cor &8deaths"),
	PLAYER_DEATHS("player-deaths",
	              "&e%0 &7has died &4%1 &7times with a playtime of &a%2"),
	PLAYER_DEATHTIME("player-deathtime", "&e%0 &7has a death rate of &4%1 "+
	                                     "&7and dies approximately once per " +
	                                     "&a%2");

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
	Lang(String path, String start)
	{
		this.path = path;
		this.def = start;
	}

	/**
	 Set the {@code YamlConfiguration} to use.
	 @param config
	 The config to set.
	 */
	public static void setFile(YamlConfiguration config)
	{
		LANG = config;
	}

	@Override public String toString()
	{
		String s = LANG.getString(this.path, def);
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	/**
	 Get the default value of the path.
	 @return The default value of the path.
	 */
	public String getDefault()
	{
		return this.def;
	}

	/**
	 Get the path to the string.
	 @return The path to the string.
	 */
	public String getPath()
	{
		return this.path;
	}
}