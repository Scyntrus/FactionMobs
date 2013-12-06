package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

public class Faction8 extends Faction {

	public static Method grt;
	
	public com.massivecraft.factions.Faction faction;

	public Faction8 (com.massivecraft.factions.Faction faction) {
		this.faction = faction;
	}
	
	public Faction8 (Object faction) {
		this.faction = (com.massivecraft.factions.Faction) faction;
	}

	@Override
	public int getRelationTo(Faction other) {
		if (faction == null) return 0;
		try {
			Object rel = grt.invoke(faction, ((Faction8)other).faction);
			if (rel.equals(com.massivecraft.factions.struct.Rel.ENEMY)) {
				return -1;
			} else if (rel.equals(com.massivecraft.factions.struct.Rel.NEUTRAL)) {
				return 0;
			} else if (rel.equals(com.massivecraft.factions.struct.Rel.ALLY) || rel.equals(com.massivecraft.factions.struct.Rel.MEMBER)) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isNone() {
		if (faction == null) return true;
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
}
