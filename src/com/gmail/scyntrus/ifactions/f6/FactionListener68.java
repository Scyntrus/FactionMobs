package com.gmail.scyntrus.ifactions.f6;

import java.util.Iterator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;

public class FactionListener68 implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionRename(com.massivecraft.factions.event.FactionRenameEvent e) {
        String oldName = e.getOldFactionTag();
        String newName = e.getFactionTag();
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
    public void onFactionDisband(com.massivecraft.factions.event.FactionDisbandEvent e) {
        String factionName = FactionsManager.getFactionFromNativeObject(e.getFaction()).getName();
        for (Iterator<FactionMob> it = FactionMobs.mobList.iterator(); it.hasNext();) {
            FactionMob mob = it.next();
            if (mob.getFactionName().equals(factionName)) {
                mob.forceDie();
                it.remove();
            }
        }
    }
}
