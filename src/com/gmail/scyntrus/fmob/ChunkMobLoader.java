package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_6_R2.WorldServer;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class ChunkMobLoader implements Runnable {

	FactionMobs plugin;
	
    public ChunkMobLoader(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		if (FactionMobs.scheduleChunkMobLoad) {
			FactionMobs.scheduleChunkMobLoad = false;
			for (FactionMob fmob : FactionMobs.mobList) {
	 			if (!((WorldServer) fmob.getEntity().world).getTracker().trackedEntities.b(fmob.getEntity().id)) {
					try	{
						fmob.getEntity().world.addEntity(fmob.getEntity(), SpawnReason.CUSTOM);
					} catch (Exception ex) {}
					fmob.getEntity().dead = false;
	 			}
			}
		}
    }
}
