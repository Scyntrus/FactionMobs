package com.gmail.scyntrus.ifactions.feudal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

public class FeudalListener implements Listener {

    Plugin plugin;

    FeudalListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/f delete") || e.getMessage().toLowerCase()
                .startsWith("/f name")) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new FeudalChecker(), 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent e) {
        if (e.getCommand().toLowerCase().startsWith("/f delete")) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new FeudalChecker(), 1);
        }
    }
}
