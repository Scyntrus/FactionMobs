package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;

import com.gmail.scyntrus.fmob.FactionMobs;

public class FactionColls {
	
	private static Field i;
	private static com.massivecraft.factions.Factions f;
	private static Method gBT;
	
	public static void init() {
		if (FactionMobs.factionsVersion == 2) {
			init2();
		} else if (FactionMobs.factionsVersion == 6) {
			init6();
		} else if (FactionMobs.factionsVersion == 8) {
			init8();
		}
	}
	
	public static void init2() {
		// Nothing to init
	}
	
	public static void init6() {
		try {
			i = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			i.setAccessible(true);
			f = (com.massivecraft.factions.Factions) i.get(null);
			gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void init8() {
		try {
			i = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			i.setAccessible(true);
			f = (com.massivecraft.factions.Factions) i.get(null);
			gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
			Rel8.grt = com.massivecraft.factions.Faction.class.getDeclaredMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
			Rel8.grt.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Faction getFactionByName(String worldName, String factionName) {
		if (FactionMobs.factionsVersion == 2) {
			return new Faction(com.massivecraft.factions.entity.FactionColls.get().getForWorld(worldName).getByName(factionName));
		} else if (FactionMobs.factionsVersion == 6) {
			try {
				return new Faction(gBT.invoke(f, factionName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (FactionMobs.factionsVersion == 8) {
			try {
				return new Faction(gBT.invoke(f, factionName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Faction getFactionAt(Location loc) {
		if (FactionMobs.factionsVersion == 2) {
			return new Faction(com.massivecraft.factions.entity.BoardColls.get().getFactionAt(com.massivecraft.mcore.ps.PS.valueOf(loc)));
		} else if (FactionMobs.factionsVersion == 6) {
			return new Faction(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
		} else if (FactionMobs.factionsVersion == 8) {
			return new Faction(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
		}
		return null;
	}
}
