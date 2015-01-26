package com.gmail.scyntrus.ifactions.f2;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;

class Faction2 extends Faction {

    public com.massivecraft.factions.entity.Faction faction;

    public Faction2 (com.massivecraft.factions.entity.Faction faction) {
        this.faction = faction;
    }

    public Faction2 (Object faction) {
        this.faction = (com.massivecraft.factions.entity.Faction) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (faction == null || isNone()) return 0;
        try {
            com.massivecraft.factions.Rel rel = faction.getRelationTo(((Faction2)other).faction);
            if (rel.equals(com.massivecraft.factions.Rel.ENEMY)) {
                return -1;
            } else if (rel.equals(com.massivecraft.factions.Rel.NEUTRAL)) {
                return 0;
            } else if (rel.equals(com.massivecraft.factions.Rel.ALLY) || rel.equals(com.massivecraft.factions.Rel.MEMBER)) {
                return 1;
            }
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return 0;
    }

    @Override
    public boolean isNone() {
        if (faction == null || faction.detached()) return true;
        return faction.isNone();
    }

    @Override
    public String getName() {
        if (faction == null) return "";
        return faction.getName();
    }

    @Override
    public double getPower() {
        if (faction == null) return 0;
        return faction.getPower();
    }

    @Override
    public boolean monstersNotAllowed() {
        if (faction == null) return false;
        return !faction.getFlag(com.massivecraft.factions.entity.MFlag.ID_MONSTERS);
    }
}
