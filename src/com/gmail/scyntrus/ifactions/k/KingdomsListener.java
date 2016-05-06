package com.gmail.scyntrus.ifactions.k;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.scyntrus.fmob.FactionMobs;

public class KingdomsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionDisband(org.kingdoms.events.KingdomDeleteEvent e) {
        String factionName = e.getKingdom().getKingdomName();
        for (int i = FactionMobs.mobList.size()-1; i >= 0; i--) {
            if (FactionMobs.mobList.get(i).getFactionName().equals(factionName)) {
                FactionMobs.mobList.get(i).forceDie();
            }
        }
    }
}
