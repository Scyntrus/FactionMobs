package com.gmail.scyntrus.fmob;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
	
	FactionMobs plugin;
	
	public CommandListener(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().toLowerCase().contains("f leave")
				|| e.getMessage().toLowerCase().contains("f kick")
				|| e.getMessage().toLowerCase().contains("f disband")) {
	        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	        	public void run() {
	    			plugin.updateList();
	        	}
	        }, 0L);
		}
	}
}
