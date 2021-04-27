package me.roopekoo.deathcounter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetDeaths implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("deathcounter.playtime")) {
            if (args.length > 0) {
                String name = args[0];
                OfflinePlayer pl = Bukkit.getOfflinePlayer(name);
                if(args.length == 2)
                {
                    if(args[1].equals("time"))
                    {
                        double deaths = DeathCounter.getPlugin().get_config().getDeathsFor(pl.getUniqueId());
                        double playtime = DeathCounter.getPlugin().get_config().getPlayTimeFor(pl.getUniqueId());
                        double deathTime = deaths/playtime;
                        String deathPerTime = deathPerTime(deathTime);
                        String timePerDeath = timePerDeath(1/deathTime);
                        String s = "Deathtime for " + name + ": " + deathPerTime + " or " + timePerDeath;
                        sender.sendMessage(s);
                    }
                }
                else
                {
                    if (DeathCounter.getPlugin().get_config().hasPlayer(pl.getUniqueId().toString())) {
                        String s = "Deaths for " + name + ": " + DeathCounter.getPlugin().get_config().getDeathsFor(pl.getUniqueId());
                        sender.sendMessage(s);
                    } else {
                        sender.sendMessage("Unknown player!");
                    }
                }
            } else {
                sender.sendMessage("Please supply a player!");
            }
        } else {
            sender.sendMessage("You don't have permission to do that!");
        }
        return true;
    }
    String deathPerTime(double deathTime)
    {
        double seconds = deathTime*20;
        if(seconds>1)
        {
            return String.format("%.2f",seconds) + " deaths/second";
        }
        double minutes = seconds*60;
        if(minutes>1)
        {
            return String.format("%.2f",minutes) + " deaths/minute";
        }
        double hours = minutes*60;
        if(hours>1)
        {
            return String.format("%.2f",hours) + " deaths/hour";
        }
        double days = hours*24;
        if(days>1)
        {
            return String.format("%.2f",days) + " deaths/day";
        }
        double years = days*365.25;
        return String.format("%.2f",years) + " deaths/year";
    }

    String timePerDeath(double timeDeath)
    {
        double milliseconds = timeDeath*50;
        if(1000>milliseconds)
        {
            return String.format("%.2f",milliseconds) + " milliseconds/death";
        }
        double seconds = milliseconds/1000;
        if(60>seconds)
        {
            return String.format("%.2f",seconds) + " seconds/death";
        }
        double minutes = seconds/60;
        if(60>minutes)
        {
            return String.format("%.2f",minutes) + " minutes/death";
        }
        double hours = minutes/60;
        if(24>hours)
        {
            return String.format("%.2f",hours) + " hours/death";
        }
        double days = hours/24;
        if(365.25>days)
        {
            return String.format("%.2f",days) + " days/death";
        }
        double years = days/365.25;
        return String.format("%.2f",years) + " years/death";
    }
}
