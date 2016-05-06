package com.gmail.scyntrus.ifactions.k;

import com.gmail.scyntrus.ifactions.Faction;

class Kingdom extends Faction {

    public org.kingdoms.constants.kingdom.Kingdom k;

    public Kingdom (org.kingdoms.constants.kingdom.Kingdom faction) {
        this.k = faction;
    }

    public Kingdom (Object faction) {
        this.k = (org.kingdoms.constants.kingdom.Kingdom) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (k == null || isNone()) return 0;
        if (this.getName().equals(other.getName())) return 1;
        if (k.isAllianceWith(((Kingdom)other).k)) return 1;
        if (k.isEnemyWith(((Kingdom)other).k)) return -1;
        return 0;
    }

    @Override
    public boolean isNone() {
        return (k == null);
    }

    @Override
    public String getName() {
        if (k == null) return "";
        return k.getKingdomName();
    }

    @Override
    public double getPower() {
        if (k == null) return 0;
        return k.getMight() + k.getLand(); // Power will be might + land
    }

    @Override
    public boolean monstersNotAllowed() {
        return false;
    }
}
