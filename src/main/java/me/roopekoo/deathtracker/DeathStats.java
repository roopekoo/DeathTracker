package me.roopekoo.deathcounter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathStats implements CommandExecutor
{
	@Override public boolean onCommand(CommandSender sender, Command command,
	                                   String label, String[] args)
	{
		if(sender.hasPermission("deathcounter.getstats"))
		{
			//TODO: Do something here
		}
		else
		{
			sender.sendMessage("You don't have permission to do that!");
		}
		return true;
	}
}
