package com.gmail.scyntrus.fmob;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class CommandListener implements Listener {

    FactionMobs plugin;

    public CommandListener(FactionMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().contains("f leave")
                || e.getMessage().toLowerCase().contains("f kick")
                || e.getMessage().toLowerCase().contains("f disband")
                || e.getMessage().toLowerCase().contains("butcher")
                ) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.updateList();
                }
            }, 0L);
        }
        if (e.getMessage().toLowerCase().contains("kill") || e.getMessage().toLowerCase().contains("butcher")) {
            checkDeath();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent e) {
        if (e.getCommand().toLowerCase().contains("kill") || e.getCommand().toLowerCase().contains("butcher")) {
            checkDeath();
        }
    }

    public void checkDeath() {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DeadChecker(plugin), 1);
    }

    long lastSave = 0;

    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        long now = System.currentTimeMillis();
        if (now - lastSave > 30000) {
            lastSave = now;
            plugin.saveMobList();
        }
    }
}
