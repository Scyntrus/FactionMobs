package com.gmail.scyntrus.fmob;

import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.Factions;

public class RenameListener68 implements Listener {
	
	FactionMobs plugin;
	
	public RenameListener68(FactionMobs plugin) {
		this.plugin = plugin;
	}

	public void onFactionRename(com.massivecraft.factions.event.FactionRenameEvent e) {
		final String oldName = e.getOldFactionTag();
		final String newName = e.getFactionTag();
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
