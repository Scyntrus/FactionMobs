package com.gmail.scyntrus.ifactions.feudal;

import com.gmail.scyntrus.ifactions.Faction;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Challenge;
import us.forseth11.feudal.kingdoms.Kingdom;

class FeudalKingdom extends Faction {

    public Kingdom k;

    public FeudalKingdom(Kingdom faction) {
        this.k = faction;
    }

    public FeudalKingdom(Object faction) {
        this.k = (Kingdom) faction;
    }

    @Override
    public int getRelationTo(Faction other) {
        if (k == null || isNone()) return 0;
        if (this.getName().equals(other.getName())) return 1;
        if (k.isEnemied(((FeudalKingdom)other).k)) return -1;
        for (Challenge c : Feudal.getChallenges()) {
            if (c.isFighting()) {
                if (c.getAttacker().equals(k)) {
                    if (c.getDefender().getName().equals(other.getName())) return -1;
                } else if (c.getDefender().equals(k)) {
                    if (c.getAttacker().getName().equals(other.getName())) return -1;
                }
            }
        }
        if (k.isAllied(((FeudalKingdom)other).k)) return 1;
        return 0;
    }

    @Override
    public boolean isNone() {
        return (k == null);
    }

    @Override
    public String getName() {
        if (k == null) return "";
        return k.getName();
    }

    @Override
    public double getPower() {
        if (k == null) return 0;
        return k.getMaxLand();
    }

    @Override
    public boolean monstersNotAllowed() {
        return false;
    }
}
