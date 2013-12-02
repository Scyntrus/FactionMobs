package com.gmail.scyntrus.ifactions;

public class Rel2 {
	public static int getRelation(Object f1, Object f2) {
		Object rel = ((com.massivecraft.factions.entity.Faction)f1).getRelationTo((com.massivecraft.factions.entity.Faction)f2);
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
