package com.gmail.scyntrus.ifactions.sc;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Iterator;

public class SimpleClansListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionDisband(net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent e) {
        String factionName = e.getClan().getName();
        for (Iterator<FactionMob> it = FactionMobs.mobList.iterator(); it.hasNext();) {
            FactionMob mob = it.next();
            if (mob.getFactionName().equals(factionName)) {
                mob.forceDie();
                it.remove();
            }
        }
    }
}
