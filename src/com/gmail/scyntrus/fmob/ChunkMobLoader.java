package com.gmail.scyntrus.fmob;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_5_R3.WorldServer;

public class ChunkMobLoader implements Runnable {

	FactionMobs plugin;
	
    public ChunkMobLoader(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		if (FactionMobs.scheduleChunkMobLoad) {
			FactionMobs.scheduleChunkMobLoad = false;
			for (FactionMob fmob : FactionMobs.mobList) {
//				Utils.addEntity(fmob.getEntity().world, fmob.getEntity()); //TODO: Fix problem
//				fmob.getEntity().dead = false;
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
