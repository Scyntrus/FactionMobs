package com.gmail.scyntrus.ifactions.t;

import com.gmail.scyntrus.ifactions.Faction;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;

class Town extends Faction {

    public com.palmergames.bukkit.towny.object.Town town;

    public Town (com.palmergames.bukkit.towny.object.Town faction) {
        this.town = faction;
    }

    public Town (Object faction) {
        this.town = (com.palmergames.bukkit.towny.object.Town) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (town == null || isNone()) return 0;
        if (town.equals(other)) return 1;
        if (!town.hasNation()) return 0;
        Nation nation = null;
        try {
            nation = town.getNation();
        } catch (NotRegisteredException e) {
            return 0;
        }
        if (nation == null) return 0;
        Town otherTown = (Town)other;
        if (nation.hasTown(otherTown.town)) return 1;
        if (!otherTown.town.hasNation()) return 0;
        Nation otherNation = null;
        try {
            otherNation = otherTown.town.getNation();
        } catch (NotRegisteredException e) {
            return 0;
        }
        if (otherNation == null) return 0;
        if (nation.hasAlly(otherNation)) return 1;
        if (nation.hasEnemy(otherNation)) return 1;
        if (TownyUniverse.isWarTime()) {
            if (nation.isNeutral()) return 0;
            if (otherNation.isNeutral()) return 0;
            return -1;
        }
        return 0;
    }

    @Override
    public boolean isNone() {
        return (town == null);
    }

    @Override
    public String getName() {
        if (town == null) return "";
        return town.getName();
    }

    @Override
    public double getPower() {
        if (town == null) return 0;
        return town.getTotalBlocks();
    }

    @Override
    public boolean monstersNotAllowed() {
        if (town == null) return false;
        return !(town.getWorld().hasWorldMobs() && town.hasMobs());
    }
}
