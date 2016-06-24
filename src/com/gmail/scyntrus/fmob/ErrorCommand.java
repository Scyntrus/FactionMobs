package com.gmail.scyntrus.fmob;

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
        sender.sendMessage(Messages.get(Messages.Message.FM_ERROR, plugin.getName()));
        return true;
    }
}
