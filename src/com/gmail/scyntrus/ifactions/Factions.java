package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.Utils;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;

public class Factions {
	
	private static Field factionsInstanceField;
	private static com.massivecraft.factions.Factions factionsInstance;
    private static Field fPlayersInstanceField;
    private static com.massivecraft.factions.FPlayers fPlayersInstance;
	private static Method getByTagMethod;
	private static boolean initialized = false;
	private static Method fPlayerGet;
	public static int factionsVersion;
	
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
            fPlayerGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", OfflinePlayer.class);
            if (fPlayerGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected");
                return 6; //Factions 1.6-U old
            }
            fPlayerGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", Player.class);
            if (fPlayerGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(Player)\n");
                System.out.println("["+pluginName+"] Factions 1.6 detected. It is recommended you update to Factions 2.");
                return 6; //Factions 1.6
            }
            fPlayerGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "getByOfflinePlayer", OfflinePlayer.class);
            if (fPlayerGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.getByOfflinePlayer(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected. It is recommended you update to Factions 2.");
                return 6; //Factions 1.6-U
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
			factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			fPlayersInstanceField = com.massivecraft.factions.FPlayers.class.getDeclaredField("i");
		} catch (Exception e1) {
	        try {
	            // New Factions 1.6-UUID with Interfaces instead of Classes
	            factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("instance");
	            fPlayersInstanceField = com.massivecraft.factions.FPlayers.class.getDeclaredField("instance");
	            boardInstance = com.massivecraft.factions.Board.class.getMethod("getInstance").invoke(null); 
	        } catch (Exception e2) {
	            Utils.handleError(e1);
                Utils.handleError(e2);
	            return false;
	        }
		}
		try {
            factionsInstanceField.setAccessible(true);
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            getByTagMethod = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
            fPlayersInstanceField.setAccessible(true);
            fPlayersInstance = (FPlayers) fPlayersInstanceField.get(null);
            fPlayerGetFactionMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getFaction");
            fPlayerGetRoleMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getRole");
            boardGetFactionAt = com.massivecraft.factions.Board.class.getMethod("getFactionAt", new Class<?>[]{com.massivecraft.factions.FLocation.class}); 
		} catch (Exception e3) {
            Utils.handleError(e3);
            return false;
		}
		if (!Faction6.init()) {
            return false;
		}
		initialized = true;
		return true;
	}
	
	public static boolean init8() {
		try {
			factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("i");
			factionsInstanceField.setAccessible(true);
			factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
			getByTagMethod = com.massivecraft.factions.Factions.class.getMethod("getByTag", new Class<?>[]{String.class});
			Faction8.getRelationTo = com.massivecraft.factions.Faction.class.getMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
			Faction8.getFlag = com.massivecraft.factions.Faction.class.getMethod("getFlag", new Class<?>[]{com.massivecraft.factions.struct.FFlag.class});
		} catch (Exception e) {
            Utils.handleError(e);
			return false;
		}
		initialized = true;
		return true;
	}
	
	public static Faction getFactionByName(String worldName, String factionName) {
		if (factionsVersion == 2) {
			return new Faction2(com.massivecraft.factions.entity.FactionColl.get().getByName(factionName));
		} else if (factionsVersion == 6) {
			try {
				return new Faction6(getByTagMethod.invoke(factionsInstance, factionName));
			} catch (Exception e) {
	            Utils.handleError(e);
			}
		} else if (factionsVersion == 8) {
			try {
				return new Faction8(getByTagMethod.invoke(factionsInstance, factionName));
			} catch (Exception e) {
	            Utils.handleError(e);
			}
		}
		return null;
	}
	
	private static Object boardInstance = null;
	private static Method boardGetFactionAt;
	public static Faction getFactionAt(Location loc) {
		if (factionsVersion == 2) {
			return new Faction2(com.massivecraft.factions.entity.BoardColl.get().getFactionAt(com.massivecraft.massivecore.ps.PS.valueOf(loc)));
		} else if (factionsVersion == 6) {
		    try {
                Object f = boardGetFactionAt.invoke(boardInstance, new com.massivecraft.factions.FLocation(loc));
                return new Faction6(f);
            } catch (Exception e) {
                Utils.handleError(e);
            }
            return null;
		} else if (factionsVersion == 8) {
			return new Faction8(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
		}
		return null;
	}
	
	private static Method fPlayerGetFactionMethod;
    public static Faction getPlayerFaction(Player player) {
        if (factionsVersion == 2) {
            return new Faction2(com.massivecraft.factions.entity.MPlayer.get(player).getFaction());
        } else if (factionsVersion == 6) {
            try {
                return new Faction6(fPlayerGetFactionMethod.invoke(fPlayerGet.invoke(fPlayersInstance, player)));
            } catch (Exception e) {
                Utils.handleError(e);
            }
        } else if (factionsVersion == 8) {
            return new Faction8(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
        }
        return null;
    }

    private static Method fPlayerGetRoleMethod;
    public static FRank getPlayerRank(Player player) {
        try {
            switch (factionsVersion) {
                case 6:
                    com.massivecraft.factions.struct.Role role6 = (Role) fPlayerGetRoleMethod.invoke(fPlayerGet.invoke(fPlayersInstance, player));
                    return FRank.getByName(role6.name());
                case 8:
                    com.massivecraft.factions.struct.Role role8 = com.massivecraft.factions.FPlayers.i.get(player).getRole();
                    return FRank.getByName(role8.name());
                case 2:
                    com.massivecraft.factions.Rel rel = com.massivecraft.factions.entity.MPlayer.get(player).getRole();
                    return FRank.getByName(rel.name());
                default:
                    return FRank.MEMBER;
            }
        } catch (Exception e) {
            Utils.handleError(e);
            return FRank.MEMBER;
        }
    }
}
