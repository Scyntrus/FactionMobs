package com.gmail.scyntrus.ifactions;

public class NoneFaction extends Faction {

    @Override
    public int getRelationTo(Faction other) {
        return 0;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public double getPower() {
        return -1;
    }

    @Override
    public boolean monstersNotAllowed() {
        return false;
    }

}
