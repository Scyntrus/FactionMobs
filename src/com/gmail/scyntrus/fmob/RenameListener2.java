package com.gmail.scyntrus.fmob;

import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.Factions;

public class RenameListener2 implements Listener {
	
	FactionMobs plugin;
	
	public RenameListener2(FactionMobs plugin) {
		this.plugin = plugin;
	}

	public void onTownRenameEvent(com.massivecraft.factions.event.FactionsEventNameChange e) {
		final String oldName = e.getFaction().getName();
		final String newName = e.getNewName();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			String oldname = oldName;
			String newname = newName;
			public void run() {
				for (FactionMob fmob : FactionMobs.mobList) {
					if (fmob.getFactionName().equals(oldname)) {
						fmob.setFaction(Factions.getFactionByName(fmob.getSpawn().getWorld().getName(), newname));
					}
				}
			}
		}, 0);
	}
}
