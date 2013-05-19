package com.gmail.scyntrus.fmob;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
	
	FactionMobs plugin;
	
	public ChunkListener(FactionMobs plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent e) {
		for (FactionMob fmob : FactionMobs.mobList) {
			if (fmob.getEntity().world.worldData.getName().equals(e.getChunk().getWorld().getName())) {
				Utils.addEntity(fmob.getEntity().world, fmob.getEntity()); //TODO: Fix problem
				fmob.getEntity().dead = false;
			}
		}
	}
}
