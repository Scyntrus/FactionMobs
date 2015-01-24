package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

import com.gmail.scyntrus.fmob.Utils;

public class Faction6 extends Faction {
	
	public com.massivecraft.factions.Faction faction;

	public Faction6 (com.massivecraft.factions.Faction faction) {
		this.faction = faction;
	}
	
	public Faction6 (Object faction) {
		this((com.massivecraft.factions.Faction) faction);
	}
	
	public static boolean init() {
	    try {
            getRelationTo = com.massivecraft.factions.Faction.class.getMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
	        isNone = com.massivecraft.factions.Faction.class.getMethod("isNone");
            getTag = com.massivecraft.factions.Faction.class.getMethod("getTag");
            getPower = com.massivecraft.factions.Faction.class.getMethod("getPower");
            noMonstersInTerritory = com.massivecraft.factions.Faction.class.getMethod("noMonstersInTerritory");
            return true;
        } catch (Exception e) {
            Utils.handleError(e);
        }
	    return false;
	}

    public static Method getRelationTo;
	@Override
	public int getRelationTo(Faction other) {
		if (faction == null || isNone()) return 0;
		try {
            Object rel = getRelationTo.invoke(faction, ((Faction6)other).faction);
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

	private static Method isNone;
	@Override
	public boolean isNone() {
	    try {
	        if (faction == null) return true;
	        return (Boolean) isNone.invoke(faction);
        } catch (Exception e) {
            Utils.handleError(e);
        }
	    return true;
	}

    private static Method getTag;
	@Override
	public String getName() {
        try {
            if (faction == null) return "";
            return (String) getTag.invoke(faction);
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return "";
	}

    private static Method getPower;
	@Override
	public double getPower() {
        try {
            if (faction == null) return 0;
            return (Double) getPower.invoke(faction);
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return 0;
	}

    private static Method noMonstersInTerritory;
	@Override
	public boolean monstersNotAllowed() {
        try {
            if (faction == null) return false;
            return (Boolean) noMonstersInTerritory.invoke(faction);
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return false;
	}
}
