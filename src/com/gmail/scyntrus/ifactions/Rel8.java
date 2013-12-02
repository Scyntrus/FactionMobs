package com.gmail.scyntrus.ifactions;

public class Rel8 {
	public static int getRelation(Object f1, Object f2) {
		Object rel = ((com.massivecraft.factions.Faction)f1).getRelationTo((com.massivecraft.factions.Faction)f2);
		if (rel.equals(com.massivecraft.factions.struct.Rel.ENEMY)) {
			return -1;
		} else if (rel.equals(com.massivecraft.factions.struct.Rel.NEUTRAL)) {
			return 0;
		} else if (rel.equals(com.massivecraft.factions.struct.Rel.ALLY) || rel.equals(com.massivecraft.factions.struct.Rel.MEMBER)) {
			return 1;
		}
		return 0;
	}
}
