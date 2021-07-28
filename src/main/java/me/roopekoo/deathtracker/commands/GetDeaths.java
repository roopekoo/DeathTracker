package me.roopekoo.deathtracker.commands;

import me.roopekoo.deathtracker.DeathData;
import me.roopekoo.deathtracker.DeathTimeConverter;
import me.roopekoo.deathtracker.DeathTracker;
import me.roopekoo.deathtracker.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GetDeaths implements CommandExecutor {
	private final DeathTimeConverter converter = new DeathTimeConverter();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("deathtracker.getdeaths")) {
			if(args.length>0) {
				if(args[0].equals("total")) {
					if(args.length == 1) {
						sender.sendMessage(Lang.TITLE.toString()+Lang.NOT_ENOUGH_PARAMS);
						return false;
					} else if(args.length == 2) {
						DeathData dd = DeathTracker.getPlugin().get_file();
						if(args[1].equals("deaths")) {
							//TITLE
							String msg = Lang.TITLE.toString()+Lang.TOT_DEATHS_TITLE;
							String totPl = String.valueOf(dd.getTotalActPlayer());
							String plText = Lang.PLAYERS.toString();
							if(totPl.equals("1")) {
								plText = Lang.PLAYER.toString();
							}
							String s = combineTotalTitle(msg, totPl, plText);
							sender.sendMessage(s);

							//MAIN MESSAGE
							msg = Lang.TOT_DEATH_STATS.toString();
							String totDeaths = String.valueOf(dd.getTotalDeaths());
							String deathText = Lang.DEATHS.toString();
							if(totDeaths.equals("1")) {
								deathText = Lang.DEATH.toString();
							}

							String totPlayTime = converter.playTicksToShortStr(dd.getTotalPlaytime());
							s = combineDeathsSTR(msg, "", totDeaths, deathText, totPlayTime);
							sender.sendMessage(s);
						} else if(args[1].equals("time")) {
							//TITLE
							String msg = Lang.TITLE.toString()+Lang.TOT_DEATHRATE_TITLE;
							String totPl = String.valueOf(dd.getTotalActPlayer());
							String plText = Lang.PLAYERS.toString();
							if(totPl.equals("1")) {
								plText = Lang.PLAYER.toString();
							}
							String s = combineTotalTitle(msg, totPl, plText);
							sender.sendMessage(s);

							//MAIN MESSAGE

							double deathValue = dd.getTotalDeaths();
							double playtime = dd.getTotalPlaytime();
							double deathTime = 0;
							double timeDeath = 0;
							if(playtime != 0) {
								deathTime = deathValue/playtime;
							}
							if(deathValue != 0) {
								timeDeath = playtime/deathValue;
							}
							String deathPerTime = converter.deathPerTime(deathTime);
							String timePerDeath = converter.playTicksToShortStr(timeDeath);

							msg = Lang.TOT_DEATHRATE_STATS.toString();
							s = combineTimeSTR(msg, "", deathPerTime, timePerDeath);
							sender.sendMessage(s);
						} else {
							sender.sendMessage(Lang.TITLE.toString()+Lang.INVALID_PARAM);
						}
					} else {
						sender.sendMessage(Lang.TITLE.toString()+Lang.TOO_MANY_PARAMS);
					}
				} else {
					String name = args[0];
					OfflinePlayer pl = Bukkit.getOfflinePlayer(name);
					if(!pl.hasPlayedBefore()) {
						sender.sendMessage(Lang.TITLE.toString()+Lang.INVALID_PLAYER);
						return true;
					}
					name = pl.getName();
					assert name != null;
					DeathData deathData = DeathTracker.getPlugin().get_file();
					int totalPlayTime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE);
					deathData.updateTime(pl.getUniqueId().toString(), totalPlayTime, false);

				double deathValue = deathData.getDeaths(pl.getUniqueId());
				String deathText = Lang.DEATHS.toString();
				if(deathValue == 1) {
					deathText = Lang.DEATH.toString();
				}
				String deathSTR = String.format("%.0f", deathValue);
				double playtime = totalPlayTime-deathData.getResetTime(pl.getUniqueId());

				if(args.length == 1) {
					// getdeaths [player]
					String playTimeSTR = converter.playTicksToShortStr(playtime);
					String s = combineDeathsSTR(name, deathSTR, deathText, playTimeSTR);
					sender.sendMessage(s);
				} else if(args.length == 2) {
					// getdeaths [player] time
					if(args[1].equals("time")) {
						double deathTime = 0;
						double timeDeath = 0;
						if(playtime != 0) {
							deathTime = deathValue/playtime;
						}
						if(deathValue != 0) {
							timeDeath = playtime/deathValue;
						}
						String deathPerTime = converter.deathPerTime(deathTime);
						String timePerDeath = converter.playTicksToShortStr(timeDeath);
						String s = combineTimeSTR(name, deathPerTime, timePerDeath);
						sender.sendMessage(s);
					}
					// getdeaths [player] deaths
					else if(args[1].equals("deaths")) {
						String playTimeSTR = converter.playTicksToShortStr(playtime);
						String s = combineDeathsSTR(name, deathSTR, deathText, playTimeSTR);
						sender.sendMessage(s);
					} else {
						sender.sendMessage(Lang.TITLE.toString()+Lang.INVALID_PARAM);
						return false;
					}
				} else {
					sender.sendMessage(Lang.TITLE.toString()+Lang.TOO_MANY_PARAMS);
					return false;
				}
			} else {
				sender.sendMessage(Lang.TITLE.toString()+Lang.NOT_ENOUGH_PARAMS);
				return false;
			}
		} else {
			sender.sendMessage(Lang.TITLE.toString()+Lang.NO_PERM);
		}
		return true;
	}

	private String combineDeathsSTR(String name, String deathSTR, String deathText, String playTimeSTR) {
		String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHS;
		s = s.replace("{pl}", name);
		s = s.replace("{n}", deathSTR);
		s = s.replace("{death(s)}", deathText);
		s = s.replace("{pt}", playTimeSTR);
		return s;
	}

	private String combineTimeSTR(String name, String deathPerTime, String timePerDeath) {
		String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHTIME;
		s = s.replace("{pl}", name);
		s = s.replace("{dr}", deathPerTime);
		s = s.replace("{t}", timePerDeath);
		return s;
	}
}
