package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;

import com.gmail.scyntrus.fmob.FactionMobs;

public class FactionColls {
	public static Faction getFactionByName(String worldName, String factionName) {
		if (FactionMobs.factionsVersion == 2) {
			return new Faction(com.massivecraft.factions.entity.FactionColls.get().getForWorld(worldName).getByName(factionName));
		} else if (FactionMobs.factionsVersion == 6) {
			try {
				Field i = com.massivecraft.factions.Factions.class.getField("i");
				i.setAccessible(true);
				com.massivecraft.factions.Factions f = (com.massivecraft.factions.Factions) i.get(null);
				Method gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
				return new Faction(gBT.invoke(f, factionName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (FactionMobs.factionsVersion == 8) {
			try {
				Field i = com.massivecraft.factions.Factions.class.getField("i");
				i.setAccessible(true);
				com.massivecraft.factions.Factions f = (com.massivecraft.factions.Factions) i.get(null);
				Method gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
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
