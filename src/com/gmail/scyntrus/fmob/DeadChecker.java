package com.gmail.scyntrus.fmob;

public class DeadChecker implements Runnable {

	FactionMobs plugin;
	
    public DeadChecker(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		for (FactionMob fmob : FactionMobs.mobList) {
 			if (fmob.getEntity().dead && fmob.getHealth() > 0) {
 				fmob.getEntity().dead = false;
 				//fmob.getEntity().world.addEntity(fmob.getEntity(), SpawnReason.CUSTOM);
 			}
		}
    }
}
