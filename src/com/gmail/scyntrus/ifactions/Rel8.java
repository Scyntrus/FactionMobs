package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

public class Rel8 {
	public static int getRelation(Object f1, Object f2) {
		try {
		Method grt = com.massivecraft.factions.Faction.class.getDeclaredMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
		grt.setAccessible(true);
		Object rel = grt.invoke((com.massivecraft.factions.Faction)f1, (com.massivecraft.factions.Faction)f2);
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
}
