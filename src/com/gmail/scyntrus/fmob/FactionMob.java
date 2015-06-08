package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityLiving;

import org.bukkit.Location;

import com.gmail.scyntrus.ifactions.Faction;

public interface FactionMob {
    public Faction getFaction();
    public Location getSpawn();
    public double getlocX();
    public double getlocY();
    public double getlocZ();
    public void updateMob();
    public String getTypeName();
    public Boolean getEnabled();
    public double getPowerCost();
    public double getMoneyCost();
    public double getPoiX();
    public double getPoiY();
    public double getPoiZ();
    public void setOrder(String order);
    public String getOrder();
    public void setPoi(double x, double y, double z);
    public EntityCreature getEntity();
    public String getFactionName();
    public void clearAttackedBy();
    public int getDrops();
    public boolean softAgro(EntityLiving entity);
    public void findTarget();
    public void setFaction(Faction faction);
    public void forceDie();
    public void setTarget(EntityLiving target);
    public EntityLiving findCloserTarget();
}
