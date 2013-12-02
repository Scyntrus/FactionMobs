package com.gmail.scyntrus.ifactions;

public class Rel6 {
	public static int getRelation(Object f1, Object f2) {
		Object rel = ((com.massivecraft.factions.Faction)f1).getRelationTo((com.massivecraft.factions.Faction)f2);
		if (rel.equals(com.massivecraft.factions.struct.Relation.ENEMY)) {
			return -1;
		} else if (rel.equals(com.massivecraft.factions.struct.Relation.NEUTRAL)) {
			return 0;
		} else if (rel.equals(com.massivecraft.factions.struct.Relation.ALLY) || rel.equals(com.massivecraft.factions.struct.Relation.MEMBER)) {
			return 1;
		}
		return 0;
	}
}
