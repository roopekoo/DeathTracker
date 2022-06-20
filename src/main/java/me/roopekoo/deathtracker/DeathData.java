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
import java.util.logging.Level;

/**
 Class for handling computing death stats */
public class DeathData {
	private static final String BASEDIR = "plugins/DeathTracker";
	private static final String DEATHDATAPATH = "/deathData.yml";
	private final YamlConfiguration deathData;
	private final DeathTimeConverter converter = new DeathTimeConverter();
	private final File ff = new File(BASEDIR+DEATHDATAPATH);
	//Store players to memory
	private final HashMap<String, User> playerMap = new HashMap<>();
	//Store UUIDs in map where username is the key
	private final HashMap<String, UUID> name2uuid = new HashMap<>();
	//Main playerlists
	private final ArrayList<User> mortals = new ArrayList<>();
	private final ArrayList<User> immortals = new ArrayList<>();
	//Top playerLists
	private final ArrayList<User> zeroDeaths = new ArrayList<>();
	private final ArrayList<User> highDeaths = new ArrayList<>();
	private final ArrayList<User> lowDeaths = new ArrayList<>();
	private final ArrayList<User> highDeathRate = new ArrayList<>();
	private final ArrayList<User> lowDeathRate = new ArrayList<>();

	int totalDeaths = 0;
	int totalActPlayers = 0;
	int TOP_LIMIT = 10;
	int INFINITY_I = Integer.MAX_VALUE;
	double INFINITY_D = Double.MAX_VALUE;

	/**
	 Constructor Creates plugins/DeathTracker/deathData.yml and saves yml values to deathData
	 */
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

	/**
	 Get playtime and deaths for each player and sort them to topLists
	 */
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
			String name = offlinePlayer.getName();
			totalTime = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
			// Player is not yet on the deathData file
			if(noPlayerInYML(uuid)) {
				//Set reset time to amount of playtime on the server
				deathData.set("players."+uuid+".resetTime", totalTime);
				//Deaths = 0
				deathData.set("players."+uuid+".deaths", deaths);
			}
			resetTime = getResetTimeYML(uuid);
			//Calculate playtime in gameticks
			playTime = totalTime-resetTime;
			deaths = getDeathsYML(uuid);
			//Update totalDeaths amount
			totalDeaths += deaths;

			// PlayerData is empty, add every player to the hashMap
			User user = new User(uuid, resetTime, deaths, playTime);
			playerMap.put(uuid.toString(), user);
			if(name != null) {
				name2uuid.put(name.toLowerCase(), uuid);
				if(playTime>0) {
					if(deaths == 0) {
						immortals.add(user);
					} else {
						mortals.add(user);
					}
					//Add player as active if playtime is valid (positive)
					totalActPlayers++;
				}
			}
		}
		//Try to save the changes
		writeFile();
		Bukkit.getLogger().log(Level.INFO, Lang.TITLE+Lang.DATA_LOAD.toString());
		//Sort players to sub-toplist
		sortImmortals();
		sortMortals();
	}

	/**
	 Checks if given uuid is found in the yml
	 @param uuid
	 UUID of the player
	 @return true if players section or uuid is missing, false otherwise
	 */
	public boolean noPlayerInYML(UUID uuid) {
		ConfigurationSection sec = deathData.getConfigurationSection("players");
		return sec == null || !sec.contains(uuid.toString());
	}

	/**
	 Returns the playtime from the yml when the stats were reset.
	 @param uuid
	 UUID of the player
	 @return player's resetTime
	 */
	public int getResetTimeYML(UUID uuid) {
		return deathData.getInt("players."+uuid+".resetTime");
	}

	/**
	 Returns the amount of deaths player has from the yml.
	 @param uuid
	 UUID of the player
	 @return amount of deaths
	 */
	public int getDeathsYML(UUID uuid) {
		return deathData.getInt("players."+uuid+".deaths");
	}

	/**
	 Tries to save the data from memory to file
	 */
	private void writeFile() {
		ForkJoinPool.commonPool().submit(()->{
			try {
				deathData.save(ff);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 Sorts players from immortals to topList, sorted descending by playtime
	 */
	private void sortImmortals() {
		immortals.sort(new compTime());
		topListSorter(immortals, zeroDeaths, false);
	}

	/**
	 Put top n values from mainList to target topList
	 @param mainList
	 Sorted Array of players
	 @param topList
	 Target Array to put at most TOP_LIMIT amount of users
	 @param reversed
	 Go the list in reversed order
	 */
	private void topListSorter(ArrayList<User> mainList, ArrayList<User> topList, boolean reversed) {
		int limit = TOP_LIMIT;
		int containerSize = mainList.size();
		if(containerSize<limit) {
			limit = containerSize;
		}
		topList.clear();
		//Top n most deaths
		if(reversed) {
			for(int i = containerSize-1; i>=0; i--) {
				if(i == containerSize-limit-1) {
					break;
				}
				User user = mainList.get(i);
				topList.add(user);
			}
		} else {
			for(int i = 0; i<containerSize; i++) {
				if(i == limit) {
					break;
				}
				User user = mainList.get(i);
				topList.add(user);
			}
		}
	}

	/**
	 Sort player with deaths to their own mortal topLists
	 */
	private void sortMortals() {
		//Sort based on death amounts
		mortals.sort(new compDeaths());
		topListSorter(mortals, highDeaths, false);
		topListSorter(mortals, lowDeaths, true);
		//Sort based on deathrate
		mortals.sort(new compDeathTime());
		topListSorter(mortals, highDeathRate, false);
		topListSorter(mortals, lowDeathRate, true);
	}

	/**
	 Write all deathData from memory to file
	 */
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

	/**
	 Adds new player to YML and to memory
	 @param player
	 Player
	 */
	public void addNewPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		int resetTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		//Save player to hashmaps
		User user = new User(uuid, resetTime, 0, 0);
		playerMap.put(uuid.toString(), user);
		name2uuid.put(player.getName().toLowerCase(), uuid);
		immortals.add(user);

		totalActPlayers++;
		deathData.set("players."+uuid+".resetTime", resetTime);
		//Deaths = 0
		deathData.set("players."+uuid+".deaths", 0);
		writeFile();
	}

	/**
	 Update player's time to memory, also update topList if necessary
	 @param uuid
	 Player string uuid
	 @param totalPlaytime
	 current playtime
	 @param forceUpdate
	 false if topLists do not need updating
	 */
	public void updateTime(String uuid, int totalPlaytime, boolean forceUpdate) {
		User user = playerMap.get(uuid);
		int resetTime = user.resetTime;
		//DeathData might be deleted and player is not new but has zero playtime
		if(user.playTimeTicks == 0) {
			totalActPlayers++;
			immortals.add(user);
		}
		user.playTimeTicks = totalPlaytime-resetTime;
		// player is online
		if(forceUpdate || Bukkit.getPlayer(user.uuid) != null) {
			updateArraysTime(user);
		}
	}

	/**
	 Add death to player
	 @param player
	 Player that died
	 */
	public void addDeath(Player player) {
		String uuid = player.getUniqueId().toString();
		User user = playerMap.get(uuid);

		int playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		int resetTime = user.resetTime;
		user.playTimeTicks = playTime-resetTime;
		user.deaths++;
		totalDeaths++;
		updateArraysDeath(user);
		//Update deathData.yml with a new death
		deathData.set("players."+uuid+".deaths", user.deaths);
		writeFile();
	}

	/**
	 Updates toplists on player death
	 @param user
	 User object
	 */
	private void updateArraysDeath(User user) {
		if(user.deaths == 1) {
			//remove user from immortals
			immortals.remove(user);
			//add user to mortals
			mortals.add(user);
			//Sort main lists
			sortImmortals();
			sortMortals();
		} else {
			updateMortalTopLists(user);
		}
	}

	/**
	 Modify topList if player's deathrate or deaths are below the last value of the topList
	 @param user
	 User object
	 */
	private void updateMortalTopLists(User user) {
		double newDeathTime = (double) user.deaths/(double) user.playTimeTicks;

		//Last death values
		int deathHigh = 0;
		int deathLow = INFINITY_I;
		deathHigh = getLastDeathValue(highDeaths, deathHigh);
		deathLow = getLastDeathValue(lowDeaths, deathLow);

		//Update highDeaths
		if(user.deaths>=deathHigh) {
			editList(highDeaths, user);
		}
		//Update lowDeaths
		if(user.deaths<=deathLow) {
			editList(lowDeaths, user);
		}

		double deathRateHigh = 0;
		double deathRateLow = INFINITY_D;
		deathRateHigh = getLastDeathRateValue(highDeathRate, deathRateHigh);
		deathRateLow = getLastDeathRateValue(lowDeathRate, deathRateLow);

		//Update highDeathRate
		if(newDeathTime>=deathRateHigh) {
			editList(highDeathRate, user);
		}
		//Update lowDeathRate
		if(newDeathTime<=deathRateLow) {
			editList(lowDeathRate, user);
		}
	}

	/**
	 Get last death value from deaths topList
	 @param topList
	 Deaths topList
	 @param initValue
	 Initial limit for current topList
	 @return death value from last player in toplist if toplist is not empty, otherwise return initValue
	 */
	private int getLastDeathValue(ArrayList<User> topList, int initValue) {
		if(topList.size() != 0) {
			initValue = topList.get(topList.size()-1).deaths;
		}
		return initValue;
	}

	/**
	 Get last deathrate value from deathrate topList
	 @param topList
	 DeathRate topList
	 @param initValue
	 Initial limit for current topList
	 @return deathrate value from last player in toplist if toplist is not empty, otherwise return initValue
	 */
	private double getLastDeathRateValue(ArrayList<User> topList, double initValue) {
		if(topList.size() != 0) {
			User highUser = topList.get(topList.size()-1);
			initValue = (double) highUser.deaths/(double) highUser.playTimeTicks;
		}
		return initValue;
	}

	/**
	 Add user to top list, sort given toplist, delete
	 @param topList
	 topList to be edited
	 @param user
	 User Object
	 */
	private void editList(ArrayList<User> topList, User user) {
		modifyTopList(topList, user);
		reorderTopList(topList);
		trimTopList(topList);
	}

	/**

	 */
	private void modifyTopList(ArrayList<User> topList, User user) {
		User targetUser = hasUser(topList, user.uuid.toString());
		// targetUser found->update deaths and playtime
		if(targetUser != null) {
			targetUser.deaths = user.deaths;
			targetUser.playTimeTicks = user.playTimeTicks;
		} else {
			topList.add(user);
		}
	}

	/**

	 */
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

	/**
	 Sort correct topList
	 */
	private void reorderTopList(ArrayList<User> topList) {
		if(highDeaths.equals(topList)) {
			topList.sort(new compDeaths());
		} else if(lowDeaths.equals(topList)) {
			topList.sort(new compDeaths().reversed());
		} else if(highDeathRate.equals(topList)) {
			topList.sort(new compDeathTime());
		} else if(lowDeathRate.equals(topList)) {
			topList.sort(new compDeathTime().reversed());
		} else {
			topList.sort(new compTime());
		}
	}

	public void printTopDeaths(CommandSender sender, boolean isAsc) {
		String statsTitle = Lang.HIGH_DEATHS_TITLE.toString();
		ArrayList<User> topList = highDeaths;
		if(isAsc) {
			topList = lowDeaths;
			statsTitle = Lang.LOW_DEATHS_TITLE.toString();
		}
		printTitle(sender, statsTitle, topList);

		int i = 1;
		String name;
		String deaths;
		String playTime;
		String deathsText;
		String s;

		updateOnlinePlayers();
		reorderTopList(topList);

		for(User user: topList) {
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deaths = String.valueOf(user.deaths);
			deathsText = Lang.DEATHS.toString();
			if(user.deaths == 1) {
				deathsText = Lang.DEATH.toString();
			}
			playTime = converter.playTicksToShortStr(user.playTimeTicks);
			s = Lang.DEATH_STATS.toString();
			s = s.replace("{i}", String.valueOf(i));
			s = s.replace("{pl}", name);
			s = s.replace("{n}", deaths);
			s = s.replace("{death(s)}", deathsText);
			s = s.replace("{pt}", playTime);
			sender.sendMessage(s);
			i++;
		}
	}

	private void updateOnlinePlayers() {
		int totalPlaytime;
		for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
			totalPlaytime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE);
			updateTime(pl.getUniqueId().toString(), totalPlaytime, true);
		}
	}

	private void updateArraysTime(User user) {
		//update immortals list
		if(user.deaths == 0) {
			int lastPlaytime = 0;
			lastPlaytime = getLastPlayTimeValue(zeroDeaths, lastPlaytime);
			//Last value of zeroDeaths is smaller than user's playtime
			if(user.playTimeTicks>lastPlaytime) {
				editList(zeroDeaths, user);
			}
		}
		//update mortals list
		else {
			updateMortalTopLists(user);
		}
	}

	private int getLastPlayTimeValue(ArrayList<User> topList, int initValue) {
		if(!topList.isEmpty()) {
			initValue = topList.get(topList.size()-1).playTimeTicks;
		}
		return initValue;
	}

	public int getResetTime(UUID player) {
		return playerMap.get(player.toString()).resetTime;
	}

	private void trimTopList(ArrayList<User> topList) {
		if(topList.size()>TOP_LIMIT) {
			topList.remove(topList.size()-1);
		}
	}

	public int getDeaths(UUID player) {
		return playerMap.get(player.toString()).deaths;
	}


	private void printTitle(CommandSender sender, String statsTitle, ArrayList<User> topList) {
		String playerText = Lang.PLAYERS.toString();
		int n = topList.size();
		if(n<2) {
			if(n == 0) {
				sender.sendMessage(Lang.TITLE.toString()+Lang.EMPTY_TOP_LIST);
				return;
			}
			// n = 1
			else {
				playerText = Lang.PLAYER.toString();
			}
		}

		String s = Lang.TITLE+statsTitle;
		s = s.replace("{n}", String.valueOf(n));
		s = s.replace("{player(s)}", playerText);
		sender.sendMessage(s);
	}


	public void printTopDeathRate(CommandSender sender, boolean isAsc) {
		String statsTitle = Lang.HIGH_DEATHRATE_TITLE.toString();
		ArrayList<User> topList = highDeathRate;
		if(isAsc) {
			topList = lowDeathRate;
			statsTitle = Lang.LOW_DEATHRATE_TITLE.toString();
		}
		printTitle(sender, statsTitle, topList);

		int i = 1;
		String name;
		String deathRate;
		String s;

		updateOnlinePlayers();
		reorderTopList(topList);
		for(User user: topList) {
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			deathRate = converter.deathPerTime((double) user.deaths/(double) user.playTimeTicks);
			s = Lang.DEATHRATE_STATS.toString();
			s = s.replace("{i}", String.valueOf(i));
			s = s.replace("{pl}", name);
			s = s.replace("{dr}", deathRate);
			sender.sendMessage(s);
			i++;
		}
	}

	public void printImmortal(CommandSender sender) {
		printTitle(sender, Lang.IMMORTAL_TITLE.toString(), zeroDeaths);

		int i = 1;
		String name;
		String playTime;
		String s;

		updateOnlinePlayers();
		reorderTopList(zeroDeaths);
		for(User user: zeroDeaths) {
			name = Bukkit.getOfflinePlayer(user.uuid).getName();
			assert name != null;
			playTime = converter.playTicksToShortStr(user.playTimeTicks);
			s = Lang.IMMORTAL_STATS.toString();
			s = s.replace("{i}", String.valueOf(i));
			s = s.replace("{pl}", name);
			s = s.replace("{pt}", playTime);
			sender.sendMessage(s);
			i++;
		}
	}

	public int getTotalDeaths() {
		return totalDeaths;
	}

	public int getTotalActPlayer() {
		return totalActPlayers;
	}

	public long getTotalPlaytime() {
		OfflinePlayer pl;
		long totalPlaytime = 0;
		int playtime;

		for(User user: immortals) {
			pl = Bukkit.getOfflinePlayer(user.uuid);
			if(pl.isOnline()) {
				playtime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE)-user.resetTime;
			} else {
				playtime = user.playTimeTicks;
			}
			totalPlaytime += playtime;
		}
		for(User user: mortals) {
			pl = Bukkit.getOfflinePlayer(user.uuid);
			if(pl.isOnline()) {
				playtime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE)-user.resetTime;
			} else {
				playtime = user.playTimeTicks;
			}
			totalPlaytime += playtime;
		}
		return totalPlaytime;
	}

	public boolean hasPlayer(String uuid) {
		return playerMap.containsKey(uuid);
	}


	public UUID nameToUuid(String name) {
		return name2uuid.get(name.toLowerCase());
	}

	/**
	 User class to store player playtime and deaths
	 */
	static final class User {
		private final int resetTime;
		private final UUID uuid;
		private int deaths;
		private int playTimeTicks;

		/**
		 @param uuid
		 UUID of the player
		 @param resetTime
		 playtime before stats reset
		 @param deaths
		 amount of deaths
		 @param playTimeTicks
		 total playtime in game ticks
		 */
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

	/**
	 Compare users based on time
	 */
	static class compTime implements Comparator<User> {

		/**
		 Greater playtime is first (Descending sorting)
		 @param o1
		 user1
		 @param o2
		 use2
		 @return playtime difference
		 */
		@Override public int compare(User o1, User o2) {
			return o2.playTimeTicks-o1.playTimeTicks;
		}

		@Override public Comparator<User> reversed() {
			return Comparator.super.reversed();
		}
	}
}
