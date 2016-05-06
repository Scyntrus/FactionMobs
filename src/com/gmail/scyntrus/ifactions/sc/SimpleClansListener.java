package com.gmail.scyntrus.ifactions.sc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.fmob.FactionMobs;

public class SimpleClansListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionDisband(net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent e) {
        String factionName = e.getClan().getName();
        for (int i = FactionMobs.mobList.size()-1; i >= 0; i--) {
            if (FactionMobs.mobList.get(i).getFactionName().equals(factionName)) {
                FactionMobs.mobList.get(i).forceDie();
            }
        }
    }
}
