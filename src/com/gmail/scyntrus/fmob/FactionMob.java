package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;

import org.bukkit.Location;

import com.massivecraft.factions.Faction;

public interface FactionMob {
	public Faction getFaction();
	public void setFaction(Faction faction);
	public void setSpawn(Location loc);
	public Location getSpawn();
	public double getlocX();
	public double getlocY();
	public double getlocZ();
	public Entity findTarget();
	public void updateMob();
	public String getTypeName();
	public boolean isAlive();
	public int getHealth();
	public void die();
}
