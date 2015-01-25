package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.f2.Factions2;
import com.gmail.scyntrus.ifactions.f6.Factions6;
import com.gmail.scyntrus.ifactions.f6u.Factions6U;
import com.gmail.scyntrus.ifactions.f8.Factions8;

public class FactionsManager {
	
	private static boolean initialized = false;
	private static Method fPlayersGet;
	private static Factions instance;
	private static int factionsVersion = 0;
	
	private static Method tryGetMethod(Class<?> c, String name, Class<?>... parameterTypes) {
        try {
            return c.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
	}
	
	private static boolean classExists(String name) {
	    try {
	        Class.forName(name);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static int getFactionsVersion() {
	    return factionsVersion;
	}
	
	private static int getFactionsVersion(String pluginName) {
	    StringBuilder log = new StringBuilder();
        log.append("\nSTART FactionMobs getFactionsVersion\n");
	    if (classExists("com.massivecraft.factions.Rel")) {
            log.append("FOUND com.massivecraft.factions.Rel\n");
            System.out.println("["+pluginName+"] Factions 2 detected");
            return 2; //Factions 2.x
	    } else if (classExists("com.massivecraft.factions.struct.Rel")) {
            log.append("FOUND com.massivecraft.factions.struct.Rel\n");
            System.out.println("["+pluginName+"] Factions 1.8 detected. It is recommended you update to Factions 2.");
            return 8; //Factions 1.8
        } else if (classExists("com.massivecraft.factions.struct.Relation")) {
            log.append("FOUND com.massivecraft.factions.struct.Relation\n");
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected");
                return 6; //Factions 1.6-U old
            }
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", Player.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(Player)\n");
                System.out.println("["+pluginName+"] Factions 1.6 detected. It is recommended you update to Factions 2.");
                return 6; //Factions 1.6
            }
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "getByOfflinePlayer", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.getByOfflinePlayer(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected. It is recommended you update to Factions 2.");
                return 62; //Factions 1.6-U
            }
        }
	    Utils.handleError(log.toString());
        Utils.handleError("No compatible version of Factions detected. "+pluginName+" will not be enabled.");
        return 0;
	}
	
	public static boolean init(String pluginName) {
    	if (initialized) return true;

    	factionsVersion = getFactionsVersion(pluginName);
    	if (factionsVersion == 2) {
    	    instance = Factions2.get();
		} else if (factionsVersion == 6) {
            instance = Factions6.get(fPlayersGet);
        } else if (factionsVersion == 62) {
            instance = Factions6U.get(fPlayersGet);
		} else if (factionsVersion == 8) {
            instance = Factions8.get();
		} else {
		    return false;
		}
    	initialized = true;
    	return instance.init();
	}
	
	public static Faction getFactionByName(String name) {
		return instance.getFactionByName(name);
	}
	
	public static Faction getFactionAt(Location loc) {
        return instance.getFactionAt(loc);
	}
	
    public static Faction getPlayerFaction(Player player) {
        return instance.getPlayerFaction(player);
    }

    public static Rank getPlayerRank(Player player) {
        return instance.getPlayerRank(player);
    }
}
