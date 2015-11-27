package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.f2.Factions2;
import com.gmail.scyntrus.ifactions.f6.Factions6;
import com.gmail.scyntrus.ifactions.f6u.Factions6U;
import com.gmail.scyntrus.ifactions.f8.Factions8;
import com.gmail.scyntrus.ifactions.t.Towny;

public class FactionsManager {

    public static enum Version {
        INVALID,
        F16, // Factions 1.6 and old UUID
        F16U, // new Factions 1.6-UUID
        F18, // Factions 1.8
        F2, // Factions 2
        TOWNY // Towny
    }

    private static boolean initialized = false;
    private static Method fPlayersGet;
    private static Factions instance;
    private static Version factionsVersion = Version.INVALID;

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

    public static Version getFactionsVersion() {
        return factionsVersion;
    }

    private static Version getFactionsVersion(String pluginName) {
        StringBuilder log = new StringBuilder();
        log.append("\nSTART FactionMobs getFactionsVersion\n");
        if (classExists("com.massivecraft.factions.Rel")) {
            log.append("FOUND com.massivecraft.factions.Rel\n");
            System.out.println("["+pluginName+"] Factions 2 detected");
            return Version.F2; //Factions 2.x
        } else if (classExists("com.massivecraft.factions.struct.Rel")) {
            log.append("FOUND com.massivecraft.factions.struct.Rel\n");
            System.out.println("["+pluginName+"] Factions 1.8 detected. It is recommended you update to Factions 2.");
            return Version.F18; //Factions 1.8
        } else if (classExists("com.massivecraft.factions.struct.Relation")) {
            log.append("FOUND com.massivecraft.factions.struct.Relation\n");
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected");
                return Version.F16; //Factions 1.6-U old
            }
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", Player.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(Player)\n");
                System.out.println("["+pluginName+"] Factions 1.6 detected. It is recommended you update to Factions 2.");
                return Version.F16; //Factions 1.6
            }
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "getByOfflinePlayer", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.getByOfflinePlayer(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected. It is recommended you update to Factions 2.");
                return Version.F16U; //Factions 1.6-U
            }
        } else if (classExists("com.palmergames.bukkit.towny.Towny")) {
            log.append("FOUND com.palmergames.bukkit.towny.Towny\n");
            System.out.println("["+pluginName+"] Towny detected. Towny support is highly experimental and ugly.");
            return Version.TOWNY; //Towny
        }
        ErrorManager.handleError(log.toString());
        ErrorManager.handleError("No compatible version of Factions detected. "+pluginName+" will not be enabled.");
        return Version.INVALID;
    }

    public static boolean init(String pluginName) {
        if (initialized) return true;

        factionsVersion = getFactionsVersion(pluginName);
        if (factionsVersion == Version.F2) {
            instance = Factions2.get();
        } else if (factionsVersion == Version.F16) {
            instance = Factions6.get(fPlayersGet);
        } else if (factionsVersion == Version.F16U) {
            instance = Factions6U.get(fPlayersGet);
        } else if (factionsVersion == Version.F18) {
            instance = Factions8.get();
        } else if (factionsVersion == Version.TOWNY) {
            instance = Towny.get();
        } else {
            return false;
        }
        initialized = true;
        return instance.init();
    }

    public static Faction getFactionByName(String name) {
        try {
            Faction faction = instance.getFactionByName(name);
            return faction != null ? faction : new NoneFaction();
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return new NoneFaction();
    }

    public static Faction getFactionAt(Location loc) {
        try {
            Faction faction = instance.getFactionAt(loc);
            return faction != null ? faction : new NoneFaction();
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return new NoneFaction();
    }

    public static Faction getPlayerFaction(Player player) {
        try {
            Faction faction = instance.getPlayerFaction(player);
            return faction != null ? faction : new NoneFaction();
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return new NoneFaction();
    }

    public static Faction getFactionFromNativeObject(Object nativeObject) {
        try {
            Faction faction = instance.getFactionFromNativeObject(nativeObject);
            return faction != null ? faction : new NoneFaction();
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return new NoneFaction();
    }

    public static Rank getPlayerRank(Player player) {
        try {
            Rank rank = instance.getPlayerRank(player);
            return rank != null ? rank : Rank.MEMBER;
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return Rank.MEMBER;
    }
}
