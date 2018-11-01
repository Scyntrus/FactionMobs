package com.gmail.scyntrus.ifactions;

import com.gmail.scyntrus.fmob.FactionMob;
import net.minecraft.server.v1_13_R2.Entity;

public abstract class Faction {

    public abstract int getRelationTo(Faction other);
    public abstract boolean isNone();
    public abstract String getName();
    public abstract double getPower();
    public abstract boolean monstersNotAllowed();

    public boolean equals(Faction other) {
        return this.getName().equals(other.getName());
    }
    public void processMob(FactionMob mob) { }
    public boolean dontAttack(Entity entity) { return false; }
}
