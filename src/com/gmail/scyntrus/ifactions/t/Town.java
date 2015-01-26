package com.gmail.scyntrus.ifactions.t;

import com.gmail.scyntrus.ifactions.Faction;

class Town extends Faction {

    public com.palmergames.bukkit.towny.object.Town faction;

    public Town (com.palmergames.bukkit.towny.object.Town faction) {
        this.faction = faction;
    }

    public Town (Object faction) {
        this.faction = (com.palmergames.bukkit.towny.object.Town) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (faction == null || isNone()) return 0;
        if (faction.equals(other)) return 1;
        if (!faction.hasNation()) return 0;
        com.palmergames.bukkit.towny.object.Nation nation = null;
        try {
            nation = faction.getNation();
        } catch (com.palmergames.bukkit.towny.exceptions.NotRegisteredException e) {
            return 0;
        }
        if (nation == null) return 0;
        Town otherTown = (Town)other;
        if (nation.hasTown(otherTown.faction)) return 1;
        if (!otherTown.faction.hasNation()) return 0;
        com.palmergames.bukkit.towny.object.Nation otherNation = null;
        try {
            otherNation = otherTown.faction.getNation();
        } catch (com.palmergames.bukkit.towny.exceptions.NotRegisteredException e) {
            return 0;
        }
        if (otherNation == null) return 0;
        if (nation.hasAlly(otherNation)) return 1;
        if (nation.hasEnemy(otherNation)) return 1;
        if (com.palmergames.bukkit.towny.object.TownyUniverse.isWarTime()) {
            if (nation.isNeutral()) return 0;
            if (otherNation.isNeutral()) return 0;
            return -1;
        }
        return 0;
    }

    @Override
    public boolean isNone() {
        return (faction == null);
    }

    @Override
    public String getName() {
        if (faction == null) return "";
        return faction.getName();
    }

    @Override
    public double getPower() {
        if (faction == null) return 0;
        return faction.getTotalBlocks();
    }

    @Override
    public boolean monstersNotAllowed() {
        if (faction == null) return false;
        return !(faction.getWorld().hasWorldMobs() && faction.hasMobs());
    }
}
