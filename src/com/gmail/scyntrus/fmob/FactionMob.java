package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.EntityLiving;

import org.bukkit.Location;

import com.gmail.scyntrus.ifactions.Faction;

public interface FactionMob {
    Faction getFaction();
    Location getSpawn();
    double getlocX();
    double getlocY();
    double getlocZ();
    void updateMob();
    String getTypeName();
    Boolean getEnabled();
    double getPowerCost();
    double getMoneyCost();
    double getPoiX();
    double getPoiY();
    double getPoiZ();
    void setOrder(String order);
    String getOrder();
    void setPoi(double x, double y, double z);
    EntityCreature getEntity();
    String getFactionName();
    void clearAttackedBy();
    int getDrops();
    boolean softAgro(EntityLiving entity);
    void findTarget();
    void setFaction(Faction faction);
    void forceDie();
    void setTarget(EntityLiving target);
    EntityLiving findCloserTarget();
}
