package me.roopekoo.deathtracker;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
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
	private final DeathTimeConverter converter = new DeathTimeConverter();
	private final File ff = new File(BASEDIR + DEATHDATAPATH);
	private final HashMap < String, User > playerMap = new HashMap < > ();

	public DeathData()
	{
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

	public void addNewPlayer(Player player)
	{
		String uuid = player.getUniqueId().toString();
		User user = new User(0, 0, 0);
		playerMap.put(uuid, user);
	}

	//At least template data exist on the PlayerData
	public void addDeath(Player player) {
		String uuid = player.getUniqueId().toString();
		User user = playerMap.get(uuid);
		int playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		int resetTime = user.resetTime;

		user.deaths++;
		user.playTimeTicks = playTime - resetTime;
	}

	public void updateTime(Player player) {
		String uuid = player.getUniqueId().toString();
		User user = playerMap.get(uuid);
		int playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		int resetTime = user.resetTime;

		user.playTimeTicks = playTime - resetTime;
	}

	public boolean noPlayerInYML(UUID uuid) {
		ConfigurationSection sec = deathData.getConfigurationSection("players");
		return sec == null || !sec.contains(uuid.toString());
	}

	public int getDeathsYML(UUID player) {
		return deathData.getInt("players." + player + ".deaths");
	}

	public int getDeaths(UUID player) {
		return playerMap.get(player.toString()).deaths;
	}

	public int getResetTime(UUID player) {
		return playerMap.get(player.toString()).resetTime;
	}

	public int getResetTimeYML(UUID player) {
		return deathData.getInt("players." + player + ".resetTime");
	}

	//Adds all missing players to the deathData.yml file
	public void initializePlayerData() {
		int deaths = 0;
		UUID uuid;
		int playtime;
		int resetTime;

		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		// Go through all offline players
		for (OfflinePlayer offlinePlayer: offlinePlayers) {
			uuid = offlinePlayer.getUniqueId();
			playtime = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
			// Player is not yet on the deathData file
			if (noPlayerInYML(uuid)) {
				//Set reset time to amount of playtime on the server
				deathData.set("players."+uuid+".resetTime", playtime);
				//Deaths = 0
				deathData.set("players." + uuid + ".deaths", deaths);
			}
			resetTime = getResetTimeYML(uuid);
			deaths = getDeathsYML(uuid);
			deathData.set("players."+uuid+".playtimeTicks",
			              playtime-resetTime);

			// PlayerData is empty, add every player to the hashMap
			User user = new User(resetTime, deaths, playtime-resetTime);
			playerMap.put(uuid.toString(), user);
		}
		//Try to save the changes
		writeFile();
		System.out.println("Offline players death/playtime updated");
	}

	public void writeAllToDeathData()
	{
		User user;
		String uuid;
		for (Map.Entry < String, User > entry: playerMap.entrySet()) {
			uuid = entry.getKey();
			user = entry.getValue();
			deathData.set("players." + uuid + ".resetTime", user.resetTime);
			deathData.set("players." + uuid + ".deaths", user.deaths);
		}
		writeFile();
	}

	private void writeFile() {
		ForkJoinPool.commonPool().submit(() -> {
			try {
				deathData.save(ff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void printImmortal(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String playTime;
		for (User user: immortals) {
			if (i == TOP_LIMIT + 1) {
				break;
			}
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			playTime = converter.playTicksToShortStr(user.playTimeTicks);
			String s = Lang.IMMORTAL_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", playTime);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printLowDeathRate(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String deathRate;
		for (User user: lowDeathRate) {
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deathRate = converter.deathPerTime((double) user.deaths / (double) user.playTimeTicks);
			String s = Lang.DEATHRATE_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", deathRate);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printHighDeathRate(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String deathRate;
		for (User user: highDeathRate) {
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deathRate = converter.deathPerTime((double) user.deaths / (double) user.playTimeTicks);
			String s = Lang.DEATHRATE_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", deathRate);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printHighDeaths(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String deaths;
		String playTime;
		for (User user: highDeaths) {
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deaths = String.valueOf(user.deaths);
			playTime = converter.playTicksToShortStr(user.playTimeTicks);
			String s = Lang.DEATH_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", deaths);
			s = s.replace("%3", playTime);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printLowDeaths(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String deaths;
		String playTime;
		for (User user: lowDeaths) {
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deaths = String.valueOf(user.deaths);
			playTime = converter.playTicksToShortStr(user.playTimeTicks);
			String s = Lang.DEATH_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", deaths);
			s = s.replace("%3", playTime);
			sender.sendMessage(s);
			i++;
		}
	}

	static final class User {
		private final int resetTime;
		private int deaths;
		private int playTimeTicks;

		public User(int resetTime, int deaths, int playTimeTicks)
		{
			this.resetTime = resetTime;
			this.deaths = deaths;
			this.playTimeTicks = playTimeTicks;
		}
	}
}
