package me.roopekoo.deathtracker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathStats implements CommandExecutor {
	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("deathtracker.getstats")) {
			if (args.length > 0) {
				DeathData deathData = DeathTracker.getPlugin().get_file();
				if (args.length == 2) {
					if (args[0].equals("deaths")) {
						if (args[1].equals("high")) {
							sender.sendMessage(Lang.TITLE.toString() + Lang.HIGH_DEATHS);
							deathData.printHighDeaths(sender);
						} else if (args[1].equals("low")) {
							sender.sendMessage(Lang.TITLE.toString() + Lang.LOW_DEATHS);
							deathData.printLowDeaths(sender);
						} else {
							sender.sendMessage(Lang.TITLE.toString() + Lang.INVALID_PARAM);
							return false;
						}
					} else if (args[0].equals("deathrate")) {
						if (args[1].equals("high")) {
							sender.sendMessage(Lang.TITLE.toString() + Lang.HIGH_DEATHRATE_TITLE);
							deathData.printHighDeathRate(sender);
						} else if (args[1].equals("low")) {
							sender.sendMessage(Lang.TITLE.toString() + Lang.LOW_DEATHRATE_TITLE);
							deathData.printLowDeathRate(sender);
						} else {
							sender.sendMessage(Lang.TITLE.toString() + Lang.INVALID_PARAM);
							return false;
						}
					} else {
						sender.sendMessage(Lang.TITLE.toString() + Lang.TOO_MANY_PARAMS);
						return false;
					}
				} else if (args.length == 1) {
					if (args[0].equals("immortals")) {
						sender.sendMessage(Lang.TITLE.toString() + Lang.IMMORTAL_TITLE);
						deathData.printImmortal(sender);
					} else {
						sender.sendMessage(Lang.TITLE.toString() + Lang.NOT_ENOUGH_PARAMS);
						return false;
					}
				} else {
					sender.sendMessage(Lang.TITLE.toString() + Lang.TOO_MANY_PARAMS);
					return false;
				}
			} else {
				sender.sendMessage(Lang.TITLE.toString() + Lang.NOT_ENOUGH_PARAMS);
				return false;
			}
		} else {
			sender.sendMessage(Lang.TITLE.toString() + Lang.NO_PERM);
		}
		return true;
	}
}
