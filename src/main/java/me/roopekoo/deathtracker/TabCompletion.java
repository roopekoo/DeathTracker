package me.roopekoo.deathtracker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
	@Override public List < String > onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("getdeaths") && args.length >= 2) {
			List < String > list = new ArrayList < > ();

			list.add("deaths");
			list.add("time");
			return list;
		} else if (command.getName().equalsIgnoreCase("deathstats")) {
			if (args.length == 1) {
				List < String > list = new ArrayList < > ();
				list.add("deathrate");
				list.add("deaths");
				list.add("immortals");
				return list;
			}
			if (args.length == 2 && !args[0].equals("immortals")) {
				List < String > list = new ArrayList < > ();
				list.add("high");
				list.add("low");
				return list;
			}
		}
		return null;
	}
}
