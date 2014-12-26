package com.gmail.scyntrus.ifactions;

import com.gmail.scyntrus.fmob.Utils;

public class Faction6 extends Faction {
	
	public com.massivecraft.factions.Faction faction;

	public Faction6 (com.massivecraft.factions.Faction faction) {
		this.faction = faction;
	}
	
	public Faction6 (Object faction) {
		this.faction = (com.massivecraft.factions.Faction) faction;
	}
	
	@Override
	public int getRelationTo(Faction other) {
		if (faction == null || isNone()) return 0;
		try {
    		Object rel = faction.getRelationTo(((Faction6)other).faction);
    		if (rel.equals(com.massivecraft.factions.struct.Relation.ENEMY)) {
    			return -1;
    		} else if (rel.equals(com.massivecraft.factions.struct.Relation.NEUTRAL)) {
    			return 0;
    		} else if (rel.equals(com.massivecraft.factions.struct.Relation.ALLY) || rel.equals(com.massivecraft.factions.struct.Relation.MEMBER)) {
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
		return faction.getTag();
	}

	@Override
	public double getPower() {
		if (faction == null) return 0;
		return faction.getPower();
	}

	@Override
	public boolean monstersNotAllowed() {
		if (faction == null) return false;
		return faction.noMonstersInTerritory();
	}
}
