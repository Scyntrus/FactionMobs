package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.FactionMobs;

public class Factions {
	
	private static Field i;
	private static com.massivecraft.factions.Factions f;
	private static Method gBT;
	private static boolean initialized = false;
	public static Method fPlayerGet;
	public static int factionsVersion;
	
	public static boolean init(String pluginName) {
    	if (initialized) return true;
    	try {
    	    Class.forName("com.massivecraft.factions.Rel");
    	    factionsVersion = 2; //Factions 2.0
    	    System.out.println("["+pluginName+"] Factions 2.x detected");
    	} catch (Exception e1) {
        	try {
        	    Class.forName("com.massivecraft.factions.struct.Relation");
        	    factionsVersion = 6; //Factions 1.6
        	    try {
        	    	fPlayerGet = com.massivecraft.factions.FPlayers.class.getMethod("get", OfflinePlayer.class);
            	    System.out.println("["+pluginName+"] Factions 1.6.x-U detected");
        	    } catch (NoSuchMethodException e2)
        	    {
        	    	fPlayerGet = com.massivecraft.factions.FPlayers.class.getMethod("get", Player.class);
            	    System.out.println("["+pluginName+"] Factions 1.6.x detected. It is recommended you switch to Factions UUID at http://ci.drtshock.net/job/FactionsUUID/");
        	    }
        	} catch (Exception e2) {
            	try {
            	    Class.forName("com.massivecraft.factions.struct.Rel");
            	    factionsVersion = 8; //Factions 1.8
            	    System.out.println("["+pluginName+"] Factions 1.8.x detected. Support for this version will be discontinued. Please switch to Factions 2.x or Factions UUID at http://ci.drtshock.net/job/FactionsUUID/");
            	} catch (Exception e3) {
					System.out.println("["+pluginName+"] No compatible version of Factions detected. "+pluginName+" will not be enabled.");
					if (!FactionMobs.silentErrors) {
						e1.printStackTrace();
						e2.printStackTrace();
						e3.printStackTrace();
					}
					return false;
            	}
        	}
    	}
    	
		if (factionsVersion == 2) {
			return init2();
		} else if (factionsVersion == 6) {
			return init6();
		} else if (factionsVersion == 8) {
			return init8();
		}
		return false;
	}
	
	public static boolean init2() {
		// Nothing to init
		initialized = true;
		return true;
	}
	
	public static boolean init6() {
		try {
			i = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			i.setAccessible(true);
			f = (com.massivecraft.factions.Factions) i.get(null);
			gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
		} catch (Exception e) {
			if (!FactionMobs.silentErrors) e.printStackTrace();
			return false;
		}
		initialized = true;
		return true;
	}
	
	public static boolean init8() {
		try {
			i = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			i.setAccessible(true);
			f = (com.massivecraft.factions.Factions) i.get(null);
			gBT = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
			Faction8.getRelationTo = com.massivecraft.factions.Faction.class.getDeclaredMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
			Faction8.getRelationTo.setAccessible(true);
			Faction8.getFlag = com.massivecraft.factions.Faction.class.getDeclaredMethod("getFlag", new Class<?>[]{com.massivecraft.factions.struct.FFlag.class});
			Faction8.getFlag.setAccessible(true);
		} catch (Exception e) {
			if (!FactionMobs.silentErrors) e.printStackTrace();
			return false;
		}
		initialized = true;
		return true;
	}
	
	public static Faction getFactionByName(String worldName, String factionName) {
		if (factionsVersion == 2) {
			return new Faction2(com.massivecraft.factions.entity.FactionColls.get().getForWorld(worldName).getByName(factionName));
		} else if (factionsVersion == 6) {
			try {
				return new Faction6(gBT.invoke(f, factionName));
			} catch (Exception e) {
				if (!FactionMobs.silentErrors) e.printStackTrace();
			}
		} else if (factionsVersion == 8) {
			try {
				return new Faction8(gBT.invoke(f, factionName));
			} catch (Exception e) {
				if (!FactionMobs.silentErrors) e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Faction getFactionAt(Location loc) {
		if (factionsVersion == 2) {
			return new Faction2(com.massivecraft.factions.entity.BoardColls.get().getFactionAt(com.massivecraft.mcore.ps.PS.valueOf(loc)));
		} else if (factionsVersion == 6) {
			return new Faction6(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
		} else if (factionsVersion == 8) {
			return new Faction8(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
		}
		return null;
	}
}
