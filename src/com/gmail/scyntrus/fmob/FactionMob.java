package com.gmail.scyntrus.fmob;

import com.gmail.scyntrus.ifactions.Faction;
import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;

public interface FactionMob {
    enum Command {
        home,
        poi,
        wander,
        phome,
        ppoi,
        path
    }

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
    void setCommand(Command command);
    Command getCommand();
    void setPoi(double x, double y, double z);
    EntityCreature getEntity();
    String getFactionName();
    void clearAttackedBy();
    Material getDrops();
    boolean softAgro(EntityLiving entity);
    void findTarget();
    void setFaction(Faction faction);
    void forceDie();
    void setTarget(EntityLiving target);
    EntityLiving findCloserTarget();
    String getLocalizedName();
    void setAttackAll(boolean value);
    boolean getAttackAll();
    void updateNameTag();
}
