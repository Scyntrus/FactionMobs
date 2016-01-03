package com.gmail.scyntrus.ifactions;

public abstract class Faction {

    public abstract int getRelationTo(Faction other);
    public abstract boolean isNone();
    public abstract String getName();
    public abstract double getPower();
    public abstract boolean monstersNotAllowed();

    public boolean equals(Faction other) {
        return this.getName().equals(other.getName());
    }
}
