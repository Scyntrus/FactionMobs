package com.gmail.scyntrus.ifactions.f6;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.massivecraft.factions.struct.Relation;

class Faction6 extends Faction {

    public com.massivecraft.factions.Faction faction;

    public Faction6 (com.massivecraft.factions.Faction faction) {
        this.faction = faction;
    }

    public Faction6 (Object faction) {
        this((com.massivecraft.factions.Faction) faction);
    }

    @Override
    public int getRelationTo(Faction other) {
        if (faction == null || isNone()) return 0;
        try {
            Relation rel = faction.getRelationTo(((Faction6)other).faction);
            if (rel.equals(Relation.ENEMY)) {
                return -1;
            } else if (rel.equals(Relation.NEUTRAL)) {
                return 0;
            } else if (rel.equals(Relation.ALLY) || rel.equals(Relation.MEMBER)) {
                return 1;
            }
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return 0;
    }

    @Override
    public boolean isNone() {
        try {
            return (faction == null || faction.detached() || faction.isNone());
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return true;
    }

    @Override
    public String getName() {
        try {
            if (faction == null) return "";
            return faction.getTag();
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return "";
    }

    @Override
    public double getPower() {
        try {
            if (faction == null) return 0;
            return faction.getPower();
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return 0;
    }

    @Override
    public boolean monstersNotAllowed() {
        try {
            if (faction == null) return false;
            return faction.noMonstersInTerritory();
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return false;
    }

}
