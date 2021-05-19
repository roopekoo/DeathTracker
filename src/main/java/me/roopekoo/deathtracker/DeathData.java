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
	private final File ff = new File(BASEDIR+DEATHDATAPATH);
	private final HashMap<String, User> playerMap = new HashMap<>();

	private final ArrayList<User> mortals = new ArrayList<>();
	private final ArrayList<User> immortals = new ArrayList<>();
	private final ArrayList<User> zeroDeaths = new ArrayList<>();
	private final ArrayList<User> highDeaths = new ArrayList<>();
	private final ArrayList<User> lowDeaths = new ArrayList<>();
	private final ArrayList<User> highDeathRate = new ArrayList<>();
	private final ArrayList<User> lowDeathRate = new ArrayList<>();

	//Top 10 lists
	int TOP_LIMIT = 10;
	int INFINITY_I = Integer.MAX_VALUE;
	double INFINITY_D = Double.MAX_VALUE;

	public DeathData() {
		File f = new File(BASEDIR);
		if(!f.exists()) {
			f.mkdir();
		}
		if(!f.exists()) {
			try {
				ff.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		deathData = YamlConfiguration.loadConfiguration(ff);
	}

	public void addNewPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		User user = new User(uuid, 0, 0, 0);
		playerMap.put(uuid.toString(), user);
		immortals.add(user);
	}

	//At least template data exist on the PlayerData
	public void addDeath(Player player) {
		String uuid = player.getUniqueId().toString();
		User user = playerMap.get(uuid);

		int playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		int resetTime = user.resetTime;
		user.playTimeTicks = playTime-resetTime;
		user.deaths++;
		updateArraysDeath(user);
	}

	public void updateTime(String uuid, int totalPlaytime) {
		User user = playerMap.get(uuid);
		int resetTime = user.resetTime;
		user.playTimeTicks = totalPlaytime-resetTime;
		updateArraysTime(user);
	}

	private void updateArraysDeath(User user) {
		if(user.deaths == 1) {
			immortals.remove(hasUser(immortals, user.uuid.toString()));
			if(hasUser(zeroDeaths, user.uuid.toString()) != null) {
				sortImmortals();
			}
			mortals.add(user);
			sortMortals();
		} else {
			updateMortalTopLists(user);
		}
	}

	private void updateArraysTime(User user) {
		if(user.deaths == 0) {
			int lastPlaytime = 0;
			lastPlaytime = getLastPlayTimeValue(zeroDeaths, lastPlaytime);
			if(user.playTimeTicks>=lastPlaytime) {
				modifyTopList(zeroDeaths, user);
				zeroDeaths.sort(new compTime());
				trimTopList(zeroDeaths);
			}
		} else {
			updateMortalTopLists(user);
		}
	}

	public boolean noPlayerInYML(UUID uuid) {
		ConfigurationSection sec = deathData.getConfigurationSection("players");
		return sec == null || !sec.contains(uuid.toString());
	}

	public int getDeathsYML(UUID player) {
		return deathData.getInt("players."+player+".deaths");
	}

	public int getDeaths(UUID player) {
		return playerMap.get(player.toString()).deaths;
	}

	public int getResetTime(UUID player) {
		return playerMap.get(player.toString()).resetTime;
	}

	public int getResetTimeYML(UUID player) {
		return deathData.getInt("players."+player+".resetTime");
	}

	//Adds all missing players to the deathData.yml file
	public void initializePlayerData() {
		int deaths = 0;
		UUID uuid;
		int totalTime;
		int resetTime;
		int playTime;

		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		// Go through all offline players
		for(OfflinePlayer offlinePlayer: offlinePlayers) {
			uuid = offlinePlayer.getUniqueId();
			totalTime = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
			// Player is not yet on the deathData file
			if(noPlayerInYML(uuid)) {
				//Set reset time to amount of playtime on the server
				deathData.set("players."+uuid+".resetTime", totalTime);
				//Deaths = 0
				deathData.set("players."+uuid+".deaths", deaths);
			}
			resetTime = getResetTimeYML(uuid);
			playTime = totalTime-resetTime;
			deaths = getDeathsYML(uuid);

			// PlayerData is empty, add every player to the hashMap
			User user = new User(uuid, resetTime, deaths, playTime);
			playerMap.put(uuid.toString(), user);
			if(playTime != 0) {
				if(deaths == 0) {
					immortals.add(user);
				} else {
					mortals.add(user);
				}
			}

		}
		//Try to save the changes
		writeFile();
		System.out.println("Offline players death/playtime updated");
		sortImmortals();
		sortMortals();
	}

	private void sortImmortals() {
		User user;
		int limit = TOP_LIMIT;
		int containerSize = immortals.size();
		immortals.sort(new compTime());
		if(containerSize<limit) {
			limit = containerSize;
		}
		//Top 10 zero deaths
		for(int i = 0; i<containerSize; i++) {
			if(i == limit) {
				break;
			}
			user = immortals.get(i);
			zeroDeaths.add(user);
		}

	}

	private void sortMortals() {
		User user;
		int limit = TOP_LIMIT;
		int containerSize = mortals.size();
		if(containerSize<limit) {
			limit = containerSize;
		}
		//Top 10 most deaths
		mortals.sort(new compDeaths());
		for(int i = 0; i<containerSize; i++) {
			if(i == limit) {
				break;
			}
			user = mortals.get(i);
			highDeaths.add(user);
		}
		//Top 10 least deaths (>0)
		for(int i = containerSize-1; i>=0; i--) {
			if(i == containerSize-limit-1) {
				break;
			}
			user = mortals.get(i);
			lowDeaths.add(user);
		}

		//greatest death density
		mortals.sort(new compDeathTime());
		for(int i = 0; i<containerSize; i++) {
			if(i == limit) {
				break;
			}
			user = mortals.get(i);
			highDeathRate.add(user);
		}
		//Smallest death density
		for(int i = containerSize-1; i>=0; i--) {
			if(i == containerSize-limit-1) {
				break;
			}
			user = mortals.get(i);
			lowDeathRate.add(user);
		}
	}

	public void writeAllToDeathData() {
		User user;
		String uuid;
		for(Map.Entry<String, User> entry: playerMap.entrySet()) {
			uuid = entry.getKey();
			user = entry.getValue();
			deathData.set("players."+uuid+".resetTime", user.resetTime);
			deathData.set("players."+uuid+".deaths", user.deaths);
		}
		writeFile();
	}

	private void writeFile() {
		ForkJoinPool.commonPool().submit(()->{
			try {
				deathData.save(ff);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void printImmortal(CommandSender sender) {
		int i = 1;
		String iSTR;
		String name;
		String playTime;
		updateOnlinePlayers();
		for(User user: immortals) {
			if(i == TOP_LIMIT+1) {
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

	public void printTopDeathRate(CommandSender sender, boolean isAsc) {
		int i = 1;
		String iSTR;
		String name;
		String deathRate;
		ArrayList<User> topList = highDeathRate;
		if(isAsc) {
			topList = lowDeathRate;
		}
		updateOnlinePlayers();
		for(User user: topList) {
			iSTR = String.valueOf(i);
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deathRate = converter.deathPerTime((double) user.deaths/(double) user.playTimeTicks);
			String s = Lang.DEATHRATE_STATS.toString();
			s = s.replace("%0", iSTR);
			s = s.replace("%1", name);
			s = s.replace("%2", deathRate);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printTopDeaths(CommandSender sender, boolean isAsc) {
		int i = 1;
		String iSTR;
		String name;
		String deaths;
		String playTime;
		ArrayList<User> topList = highDeaths;
		if(isAsc) {
			topList = lowDeaths;
		}
		updateOnlinePlayers();
		for(User user: topList) {
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

	private User hasUser(ArrayList<User> topList, String uuid) {
		User target = null;
		for(User user: topList) {
			if(uuid.equals(user.uuid.toString())) {
				target = user;
				break;
			}
		}
		return target;
	}

	private void modifyTopList(ArrayList<User> topList, User user) {
		User targetUser = hasUser(topList, user.uuid.toString());
		if(targetUser != null) {
			targetUser.deaths = user.deaths;
			targetUser.playTimeTicks = user.playTimeTicks;
		} else {
			topList.add(user);
		}
	}

	private void trimTopList(ArrayList<User> topList) {
		if(topList.size()>TOP_LIMIT) {
			topList.remove(highDeaths.size()-1);
		}
	}

	private int getLastPlayTimeValue(ArrayList<User> topList, int initValue) {
		if(!topList.isEmpty()) {
			initValue = topList.get(topList.size()-1).playTimeTicks;
		}
		return initValue;
	}

	private int getLastDeathValue(ArrayList<User> topList, int initValue) {
		if(topList.size() != 0) {
			initValue = topList.get(topList.size()-1).deaths;
		}
		return initValue;
	}

	private double getLastDeathRateValue(ArrayList<User> topList, double initValue) {
		if(topList.size() != 0) {
			User highUser = topList.get(topList.size()-1);
			initValue = (double) highUser.deaths/(double) highUser.playTimeTicks;
		}
		return initValue;
	}

	private void updateMortalTopLists(User user) {
		double newDeathTime = (double) user.deaths/(double) user.playTimeTicks;

		//Last death values
		int deathHigh = 0;
		int deathLow = INFINITY_I;
		deathHigh = getLastDeathValue(highDeaths, deathHigh);
		deathLow = getLastDeathValue(lowDeaths, deathLow);

		//Update highDeaths
		if(user.deaths>=deathHigh) {
			modifyTopList(highDeaths, user);
			highDeaths.sort(new compDeaths());
			trimTopList(highDeaths);
		}
		//Update lowDeaths
		else if(user.deaths<=deathLow) {
			modifyTopList(lowDeaths, user);
			lowDeaths.sort(new compDeaths().reversed());
			trimTopList(lowDeaths);
		}

		double deathRateHigh = 0;
		double deathRateLow = INFINITY_D;
		deathRateHigh = getLastDeathRateValue(highDeathRate, deathRateHigh);
		deathRateLow = getLastDeathRateValue(lowDeathRate, deathRateLow);

		//Update highDeathRate
		if(newDeathTime>=deathRateHigh) {
			modifyTopList(highDeathRate, user);
			highDeathRate.sort(new compDeathTime());
			trimTopList(highDeathRate);
		}
		//Update lowDeathRate
		else if(newDeathTime<=deathRateLow) {
			modifyTopList(lowDeathRate, user);
			lowDeathRate.sort(new compDeathTime().reversed());
			trimTopList(lowDeathRate);
		}
	}

	private void updateOnlinePlayers() {
		int totalPlaytime;
		for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
			totalPlaytime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE);
			updateTime(pl.getUniqueId().toString(),totalPlaytime);
		}
	}

	static final class User {
		private final int resetTime;
		private final UUID uuid;
		private int deaths;
		private int playTimeTicks;

		public User(UUID uuid, int resetTime, int deaths, int playTimeTicks) {
			this.uuid = uuid;
			this.resetTime = resetTime;
			this.deaths = deaths;
			this.playTimeTicks = playTimeTicks;
		}
	}

	static class compDeaths implements Comparator<User> {
		@Override public int compare(User o1, User o2) {
			int diff = o2.deaths-o1.deaths;
			int result;
			if(diff<0) {
				result = -1;
			} else if(diff>0) {
				result = 1;
			} else {
				result = o1.playTimeTicks-o2.playTimeTicks;
			}
			return result;
		}

		@Override public Comparator<User> reversed() {
			return Comparator.super.reversed();
		}
	}

	static class compDeathTime implements Comparator<User> {
		@Override public int compare(User o1, User o2) {
			double diff = Double.compare((double) o2.deaths/(double) o2.playTimeTicks,
			                             (double) o1.deaths/(double) o1.playTimeTicks);
			int result;
			if(diff<0) {
				result = -1;
			} else if(diff>0) {
				result = 1;
			} else {
				result = 0;
			}
			return result;
		}

		@Override public Comparator<User> reversed() {
			return Comparator.super.reversed();
		}
	}

	static class compTime implements Comparator<User> {

		@Override public int compare(User o1, User o2) {
			return o2.playTimeTicks-o1.playTimeTicks;
		}

		@Override public Comparator<User> reversed() {
			return Comparator.super.reversed();
		}
	}
}
