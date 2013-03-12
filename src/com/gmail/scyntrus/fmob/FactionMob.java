package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.ItemStack;

import org.bukkit.Location;

import com.massivecraft.factions.Faction;

public interface FactionMob {
	public Faction getFaction();
	public void setFaction(Faction faction);
	public void setSpawn(Location loc);
	public Location getSpawn();
	public void setPosition(double x, double y, double z);
	public double getlocX();
	public double getlocY();
	public double getlocZ();
	public Entity findTarget();
	public void updateMob();
	public String getTypeName();
	public boolean isAlive();
	public int getHealth();
	public void setHealth(int hp);
	public void die();
	public void setEquipment(int slot, ItemStack item);
	public Boolean getEnabled();
	public double getPowerCost();
	public double getMoneyCost();
	public EntityLiving getGoalTarget();
	public void setTarget(Entity entity);
}
