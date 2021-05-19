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
		if(sender.hasPermission("deathtracker.getdeaths")) {
			if(args.length>0) {
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
				deathData.updateTime(pl.getUniqueId().toString(),totalPlayTime);

				double deaths = deathData.getDeaths(pl.getUniqueId());
				String deathSTR = String.format("%.0f", deaths);

				double playtime = totalPlayTime-deathData.getResetTime(pl.getUniqueId());
				String playTimeSTR = converter.playTicksToShortStr(playtime);

				if(args.length == 1) {
					// getdeaths [player]
					String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHS;
					s = s.replace("%0", name);
					s = s.replace("%1", deathSTR);
					s = s.replace("%2", playTimeSTR);
					sender.sendMessage(s);
				} else if(args.length == 2) {
					// getdeaths [player] time
					if(args[1].equals("time")) {
						double deathTime = deaths/playtime;
						double timeDeath = playtime/deaths;
						if(playtime == 0) {
							deathTime = 0;
						}
						if(deaths == 0) {
							timeDeath = 0;
						}
						String deathPerTime = converter.deathPerTime(deathTime);
						String timePerDeath = converter.playTicksToShortStr(timeDeath);
						String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHTIME;
						s = s.replace("%0", name);
						s = s.replace("%1", deathPerTime);
						s = s.replace("%2", timePerDeath);
						sender.sendMessage(s);
					}
					// getdeaths [player] deaths
					else if(args[1].equals("deaths")) {
						String s = Lang.TITLE.toString()+Lang.PLAYER_DEATHS;
						s = s.replace("%0", name);
						s = s.replace("%1", deathSTR);
						s = s.replace("%2", playTimeSTR);
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
}
