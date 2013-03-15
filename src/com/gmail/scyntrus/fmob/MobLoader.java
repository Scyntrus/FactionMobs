package com.gmail.scyntrus.fmob;

public class MobLoader implements Runnable {

	FactionMobs plugin;
	
    public MobLoader(FactionMobs factionMobs) {
		this.plugin = factionMobs;
	}

	public void run() {
		if (this.plugin.getServer().getPluginManager().getPlugin("Factions") == null) {
	        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 5L);
			return;
		}
		this.plugin.loadMobList();
    }
}
