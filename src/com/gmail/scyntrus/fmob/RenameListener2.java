package com.gmail.scyntrus.fmob;

import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.Factions;

public class RenameListener2 implements Listener {
	
	FactionMobs plugin;
	
	public RenameListener2(FactionMobs plugin) {
		this.plugin = plugin;
	}

	public void onFactionRename(com.massivecraft.factions.event.FactionsEventNameChange e) {
		String oldName = e.getFaction().getName();
		String newName = e.getNewName();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			
			String oldName;
			String newName;
			
			public Runnable init(String oldName, String newName) {
				this.oldName = oldName;
				this.newName = newName;
				return this;
			}
			
			public void run() {
				for (FactionMob fmob : FactionMobs.mobList) {
					if (fmob.getFactionName().equals(oldName)) {
						fmob.setFaction(Factions.getFactionByName(fmob.getSpawn().getWorld().getName(), newName));
					}
				}
			}
		}.init(oldName,  newName), 0);
	}
}
