package me.roopekoo.deathcounter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(command.getName().equalsIgnoreCase("getdeaths") && args.length >=2)
        {
            List<String> list = new ArrayList<>();
            list.add("time");
            list.add("deaths");
            return list;
        }
        return null;
    }
}
