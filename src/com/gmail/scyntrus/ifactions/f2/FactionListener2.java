package com.gmail.scyntrus.ifactions.f2;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;

public class FactionListener2 implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionRename(com.massivecraft.factions.event.EventFactionsNameChange e) {
        String oldName = e.getFaction().getName();
        String newName = e.getNewName();
        FactionMobs.factionColors.put(newName,
                FactionMobs.factionColors.containsKey(oldName) ?
                        FactionMobs.factionColors.remove(oldName) :
                            10511680);
        FactionMobs.instance.getServer().getScheduler().runTaskLater(FactionMobs.instance, new Runnable() {

            String oldName;
            String newName;

            public Runnable init(String oldName, String newName) {
                this.oldName = oldName;
                this.newName = newName;
                return this;
            }

            @Override
            public void run() {
                Faction faction = FactionsManager.getFactionByName(newName);
                if (faction == null || faction.isNone()) return;
                for (FactionMob fmob : FactionMobs.mobList) {
                    if (fmob.getFactionName().equals(oldName)) {
                        fmob.setFaction(faction);
                    }
                }
            }
        }.init(oldName,  newName), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionDisband(com.massivecraft.factions.event.EventFactionsDisband e) {
        String factionName = e.getFaction().getName();
        for (int i = FactionMobs.mobList.size()-1; i >= 0; i--) {
            if (FactionMobs.mobList.get(i).getFactionName().equals(factionName)) {
                FactionMobs.mobList.get(i).forceDie();
            }
        }
    }
}
