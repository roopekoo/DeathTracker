package me.roopekoo.deathtracker.commands;

import me.roopekoo.deathtracker.DeathData;
import me.roopekoo.deathtracker.DeathTracker;
import me.roopekoo.deathtracker.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathStats implements CommandExecutor {
	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("deathtracker.getstats")) {
			if(args.length>0) {
				DeathData deathData = DeathTracker.getPlugin().get_file();
				if(args.length == 2) {
					if(args[0].equals("deaths")) {
						if(args[1].equals("high")) {
							deathData.printTopDeaths(sender, false);
						} else if(args[1].equals("low")) {
							deathData.printTopDeaths(sender, true);
						} else {
							sender.sendMessage(Lang.TITLE.toString()+Lang.INVALID_PARAM);
							return false;
						}
					} else if(args[0].equals("deathrate")) {
						if(args[1].equals("high")) {
							deathData.printTopDeathRate(sender, false);
						} else if(args[1].equals("low")) {
							deathData.printTopDeathRate(sender, true);
						} else {
							sender.sendMessage(Lang.TITLE.toString()+Lang.INVALID_PARAM);
							return false;
						}
					} else {
						sender.sendMessage(Lang.TITLE.toString()+Lang.TOO_MANY_PARAMS);
						return false;
					}
				} else if(args.length == 1) {
					if(args[0].equals("immortals")) {
						deathData.printImmortal(sender);
					} else {
						sender.sendMessage(Lang.TITLE.toString()+Lang.NOT_ENOUGH_PARAMS);
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
