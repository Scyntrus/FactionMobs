package com.gmail.scyntrus.fmob;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ErrorCommand implements CommandExecutor {
	
	FactionMobs plugin;
	
	public ErrorCommand(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		sender.sendMessage(ChatColor.RED + "Faction Mobs was unable to load. Please inform your server admin.");
		return true;
	}
}
