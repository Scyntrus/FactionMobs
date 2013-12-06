package com.gmail.scyntrus.fmob;

import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.Factions;

public class RenameListener68 implements Listener {
	
	FactionMobs plugin;
	
	public RenameListener68(FactionMobs plugin) {
		this.plugin = plugin;
	}

	public void onFactionRename(com.massivecraft.factions.event.FactionRenameEvent e) {
		String oldName = e.getOldFactionTag();
		String newName = e.getFactionTag();
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
