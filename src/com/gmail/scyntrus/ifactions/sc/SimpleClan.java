package com.gmail.scyntrus.ifactions.sc;

import com.gmail.scyntrus.ifactions.Faction;

import net.sacredlabyrinth.phaed.simpleclans.Clan;

class SimpleClan extends Faction {

    public Clan clan;

    public SimpleClan (Clan faction) {
        this.clan = faction;
    }

    public SimpleClan (Object faction) {
        this.clan = (Clan) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (clan == null || isNone()) return 0;
        if (this.getName().equals(other.getName())) return 1;
        if (clan.isAlly(other.getName())) return 1;
        if (clan.isRival(other.getName())) return -1;
        return 0;
    }

    @Override
    public boolean isNone() {
        return (clan == null);
    }

    @Override
    public String getName() {
        if (clan == null) return "";
        return clan.getTag();
    }

    @Override
    public double getPower() {
        if (clan == null) return 0;
        return clan.getSize(); // Power will be number of members
    }

    @Override
    public boolean monstersNotAllowed() {
        return false;
    }
}
