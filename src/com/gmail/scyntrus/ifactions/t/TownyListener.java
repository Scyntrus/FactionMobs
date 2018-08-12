package com.gmail.scyntrus.ifactions.t;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Iterator;

public class TownyListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTownRename(RenameTownEvent e) {
        String oldName = e.getOldName();
        Town nativeTown = e.getTown();
        FactionMobs.factionColors.put(nativeTown.getName(),
                FactionMobs.factionColors.containsKey(oldName) ?
                        FactionMobs.factionColors.remove(oldName) :
                            10511680);
        for (FactionMob fmob : FactionMobs.mobList) {
            if (fmob.getFactionName().equals(oldName)) {
                fmob.updateMob();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTownDelete(DeleteTownEvent e) {
        String townName = e.getTownName();
        for (Iterator<FactionMob> it = FactionMobs.mobList.iterator(); it.hasNext();) {
            FactionMob mob = it.next();
            if (mob.getFactionName().equals(townName)) {
                mob.forceDie();
                it.remove();
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
