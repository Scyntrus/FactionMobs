package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityLiving;
import net.minecraft.server.v1_5_R3.ItemStack;

import org.bukkit.Location;

import com.massivecraft.factions.entity.Faction;

public interface FactionMob {
	public Faction getFaction();
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
	public double getPoiX();
	public double getPoiY();
	public double getPoiZ();
	public void setOrder(String order);
	public String getOrder();
	public void setPoi(double x, double y, double z);
	public EntityLiving getEntity();
	public String getFactionName();
	public void clearAttackedBy();
	public int getDrops();
}
