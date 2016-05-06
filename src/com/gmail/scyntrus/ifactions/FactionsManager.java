package com.gmail.scyntrus.ifactions;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.f2.Factions2;
import com.gmail.scyntrus.ifactions.f6.Factions6;
import com.gmail.scyntrus.ifactions.f6u.Factions6U;
import com.gmail.scyntrus.ifactions.f8.Factions8;
import com.gmail.scyntrus.ifactions.k.KingdomsConnector;
import com.gmail.scyntrus.ifactions.sc.SimpleClansConnector;
import com.gmail.scyntrus.ifactions.t.Towny;

public class FactionsManager {

    private static boolean initialized = false;
    private static Method fPlayersGet;
    private static Factions instance = null;

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

    public static boolean init(Plugin plugin) {
        String pluginName = plugin.getName();
        if (initialized) return true;

        StringBuilder log = new StringBuilder();
        log.append("\nSTART FactionMobs getFactionsVersion\n");
        if (classExists("com.massivecraft.factions.Rel")) {
            log.append("FOUND com.massivecraft.factions.Rel\n");
            System.out.println("["+pluginName+"] Factions 2 detected");
            instance = Factions2.get();
        } else if (classExists("com.massivecraft.factions.struct.Rel")) {
            log.append("FOUND com.massivecraft.factions.struct.Rel\n");
            System.out.println("["+pluginName+"] Factions 1.8 detected. It is recommended you update to Factions 2.");
            instance = Factions8.get();
        } else if (classExists("com.massivecraft.factions.struct.Relation")) {
            log.append("FOUND com.massivecraft.factions.struct.Relation\n");
            fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected");
                instance = Factions6.get(fPlayersGet);
            } else {
                fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", Player.class);
                if (fPlayersGet != null) {
                    log.append("FOUND com.massivecraft.factions.FPlayers.get(Player)\n");
                    System.out.println("["+pluginName+"] Factions 1.6 detected. It is recommended you update to Factions 2.");
                    instance = Factions6.get(fPlayersGet);
                } else {
                    fPlayersGet = tryGetMethod(com.massivecraft.factions.FPlayers.class, "getByOfflinePlayer", OfflinePlayer.class);
                    if (fPlayersGet != null) {
                        log.append("FOUND com.massivecraft.factions.FPlayers.getByOfflinePlayer(OfflinePlayer)\n");
                        System.out.println("["+pluginName+"] Factions 1.6-U detected.");
                        instance = Factions6U.get(fPlayersGet);
                    }
                }
            }
        } else if (classExists("com.palmergames.bukkit.towny.Towny")) {
            log.append("FOUND com.palmergames.bukkit.towny.Towny\n");
            System.out.println("["+pluginName+"] Towny detected. Towny support is experimental.");
            instance = Towny.get();
        } else if (classExists("net.sacredlabyrinth.phaed.simpleclans.SimpleClans")) {
            log.append("FOUND net.sacredlabyrinth.phaed.simpleclans.SimpleClans\n");
            System.out.println("["+pluginName+"] SimpleClans detected. SimpleClans support is highly experimental.");
            instance = SimpleClansConnector.get();
        } else if (classExists("org.kingdoms.constants.kingdom.Kingdom")) {
            log.append("FOUND org.kingdoms.constants.kingdom.Kingdom\n");
            System.out.println("["+pluginName+"] Kingdoms detected. Kingdoms support is highly experimental.");
            instance = KingdomsConnector.get();
        } else {
            ErrorManager.handleError(log.toString());
            ErrorManager.handleError("No compatible version of Factions detected. "+pluginName+" will not be enabled.");
            return false;
        }
        
        initialized = instance.init(plugin);
        return initialized;
    }
    
    public static String getVersionString() {
        if (instance == null) {
            return "INVALID";
        } else {
            return instance.getVersionString();
        }
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
    
    public static boolean supportsLandOwnership() {
        return instance.supportsLandOwnership();
    }
    
}
