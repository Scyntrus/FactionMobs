package com.gmail.scyntrus.ifactions;

public class Rel2 {
	public static int getRelation(com.massivecraft.factions.entity.Faction f1, com.massivecraft.factions.entity.Faction f2) {
		Object rel = f1.getRelationTo(f2);
		if (rel.equals(com.massivecraft.factions.Rel.ENEMY)) {
			return -1;
		} else if (rel.equals(com.massivecraft.factions.Rel.NEUTRAL)) {
			return 0;
		} else if (rel.equals(com.massivecraft.factions.Rel.ALLY) || rel.equals(com.massivecraft.factions.Rel.MEMBER)) {
			return 1;
		}
		return 0;
	}
}
