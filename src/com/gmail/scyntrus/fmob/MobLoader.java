package com.gmail.scyntrus.fmob;

public class MobLoader implements Runnable {

	FactionMobs plugin;
	
    public MobLoader(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		this.plugin.loadMobList();
    }
}
