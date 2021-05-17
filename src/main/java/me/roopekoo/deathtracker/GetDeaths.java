package me.roopekoo.deathtracker;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetDeaths implements CommandExecutor {
	private final DeathTimeConverter converter = new DeathTimeConverter();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("deathtracker.getdeaths")) {
			if (args.length > 0) {
				String name = args[0];
				OfflinePlayer pl = Bukkit.getOfflinePlayer(name);
				if (!pl.hasPlayedBefore()) {
					sender.sendMessage(Lang.TITLE.toString() + Lang.INVALID_PLAYER);
					return true;
				}
				DeathData deathData = DeathTracker.getPlugin().get_file();
				double deaths = deathData.getDeaths(pl.getUniqueId());
				String deathSTR = String.format("%.0f", deaths);

				double playtime = pl.getStatistic(Statistic.PLAY_ONE_MINUTE) - deathData.getResetTime(pl.getUniqueId());
				String playTimeSTR = converter.playTicksToShortStr(playtime);

				if (args.length == 1) {
					// getdeaths [player]
					String s = Lang.TITLE.toString() + Lang.PLAYER_DEATHS;
					s = s.replace("%0", name);
					s = s.replace("%1", deathSTR);
					s = s.replace("%2", playTimeSTR);
					sender.sendMessage(s);
				} else if (args.length == 2) {
					// getdeaths [player] time
					if (args[1].equals("time")) {
						double deathTime = deaths / playtime;
						double timeDeath = playtime / deaths;
						if (playtime == 0) {
							deathTime = 0;
						}
						if (deaths == 0) {
							timeDeath = 0;
						}
						String deathPerTime = deathPerTime(deathTime);
						String timePerDeath = playTicksToShortStr(timeDeath);
						String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHTIME;
						s = s.replace("%0", name);
						s = s.replace("%1", deathPerTime);
						s = s.replace("%2", timePerDeath);
						sender.sendMessage(s);
					}
					// getdeaths [player] deaths
					else if (args[1].equals("deaths")) {
						String s = Lang.TITLE.toString() + Lang.PLAYER_DEATHS;
						s = s.replace("%0", name);
						s = s.replace("%1", deathSTR);
						s = s.replace("%2", playTimeSTR);
						sender.sendMessage(s);
					} else {
						sender.sendMessage(Lang.TITLE.toString() + Lang.INVALID_PARAM);
						return false;
					}
				}
			}
			else
			{
				sender.sendMessage(Lang.TITLE.toString()+Lang.PLAYER_MISSING);
				return false;
			}
		}
		else
		{
			sender.sendMessage(Lang.TITLE.toString()+Lang.NO_PERM);
		}
		return true;
	}

	String deathPerTime(double deathTime)
	{
		double seconds = deathTime*20;
		if(seconds>1)
		{
			return String.format("%.2f", seconds)+" deaths/second";
		}
		double minutes = seconds*60;
		if(minutes>1)
		{
			return String.format("%.2f", minutes)+" deaths/minute";
		}
		double hours = minutes*60;
		if(hours>1)
		{
			return String.format("%.2f", hours)+" deaths/hour";
		}
		double days = hours*24;
		if(days>1)
		{
			return String.format("%.2f", days)+" deaths/day";
		}
		double years = days*365.25;
		return String.format("%.2f", years)+" deaths/year";
	}

	String playTicksToShortStr(double playTime)
	{
		double milliseconds = playTime*50;
		if(1000>milliseconds)
		{
			return String.format("%.2f", milliseconds)+" milliseconds";
		}
		double seconds = milliseconds/1000;
		if(60>seconds)
		{
			return String.format("%.2f", seconds)+" seconds";
		}
		double minutes = seconds/60;
		if(60>minutes)
		{
			return String.format("%.2f", minutes)+" minutes";
		}
		double hours = minutes/60;
		if(24>hours)
		{
			return String.format("%.2f", hours)+" hours";
		}
		double days = hours/24;
		if(365.25>days)
		{
			return String.format("%.2f", days)+" days";
		}
		double years = days/365.25;
		return String.format("%.2f", years)+" years";
	}
}
