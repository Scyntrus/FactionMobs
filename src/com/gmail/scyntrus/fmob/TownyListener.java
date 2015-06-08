package com.gmail.scyntrus.fmob;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.object.Town;

public class TownyListener implements Listener {

    FactionMobs plugin;

    public TownyListener(FactionMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTownRename(RenameTownEvent e) {
        String oldName = e.getOldName();
        Town nativeTown = e.getTown();
        FactionMobs.factionColors.put(nativeTown.getName(),
                FactionMobs.factionColors.containsKey(oldName) ?
                        FactionMobs.factionColors.remove(oldName) :
                            10511680);
        Faction town = FactionsManager.getFactionFromNativeObject(nativeTown);
        for (FactionMob fmob : FactionMobs.mobList) {
            if (fmob.getFactionName().equals(oldName)) {
                fmob.setFaction(FactionsManager.getFactionFromNativeObject(town));
            }
        }
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobRemoval(MobRemovalEvent e) {
        if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob) {
            e.setCancelled(true);
        }
    }
}
