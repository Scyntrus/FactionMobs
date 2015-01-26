package com.gmail.scyntrus.fmob;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.FactionsManager;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;

public class TownyListener implements Listener {
	
	FactionMobs plugin;
	
	public TownyListener(FactionMobs plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTownRename(RenameTownEvent e) {
		String oldName = e.getOldName();
		String newName = e.getTown().getName();
		FactionMobs.factionColors.put(newName, 
				FactionMobs.factionColors.containsKey(oldName) ? 
						FactionMobs.factionColors.remove(oldName) : 
							10511680);
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

            String oldName;
            String newName;

            public Runnable init(String oldName, String newName) {
                this.oldName = oldName;
                this.newName = newName;
                return this;
            }

            @Override
            public void run() {
                for (FactionMob fmob : FactionMobs.mobList) {
                    if (fmob.getFactionName().equals(oldName)) {
                        fmob.setFaction(FactionsManager.getFactionByName(newName));
                    }
                }
            }
        }.init(oldName,  newName), 0);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTownDelete(DeleteTownEvent e) {
		String townName = e.getTownName();
		for (int i = FactionMobs.mobList.size()-1; i >= 0; i--) {
			if (FactionMobs.mobList.get(i).getFactionName().equals(townName)) {
				FactionMobs.mobList.get(i).forceDie();
			}
		}
	}
}
