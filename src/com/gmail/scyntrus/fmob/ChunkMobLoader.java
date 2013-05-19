package com.gmail.scyntrus.fmob;

public class ChunkMobLoader implements Runnable {

	FactionMobs plugin;
	
    public ChunkMobLoader(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		if (FactionMobs.scheduleChunkMobLoad) {
			FactionMobs.scheduleChunkMobLoad = false;
			for (FactionMob fmob : FactionMobs.mobList) {
				Utils.addEntity(fmob.getEntity().world, fmob.getEntity()); //TODO: Fix problem
				fmob.getEntity().dead = false;
			}
		}
    }
}
