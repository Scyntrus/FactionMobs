package com.gmail.scyntrus.ifactions;

import com.gmail.scyntrus.fmob.ErrorManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class FactionsManager {

    private static boolean initialized = false;
    private static Factions instance = null;

    public static Method tryGetMethod(Class<?> c, String name, Class<?>... parameterTypes) {
        try {
            return c.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] factionsModules = {
            "com.gmail.scyntrus.ifactions.f2.Factions2",
            "com.gmail.scyntrus.ifactions.f6u.Factions6U",
            "com.gmail.scyntrus.ifactions.f8.Factions8",
            "com.gmail.scyntrus.ifactions.f6.Factions6",
            "com.gmail.scyntrus.ifactions.t.Towny",
            "com.gmail.scyntrus.ifactions.sc.SimpleClansConnector",
            "com.gmail.scyntrus.ifactions.k.KingdomsConnector",
            "com.gmail.scyntrus.ifactions.feudal.FeudalConnector"
    };

    public static boolean init(Plugin plugin) {
        String pluginName = plugin.getName();
        if (initialized) return true;

        StringBuilder log = new StringBuilder();
        log.append("\nSTART FactionMobs getFactionsVersion\n");

        for (String f : factionsModules) {
            try {
                @SuppressWarnings("unchecked")
                Class<Factions> c = (Class<Factions>) Class.forName(f);
                Method m = c.getMethod("get", Plugin.class, StringBuilder.class);
                instance = (Factions) m.invoke(null, plugin, log);
            } catch (ClassNotFoundException e) {
                log.append("Module not found: ").append(f).append("\n");
            } catch (Exception e) {
                ErrorManager.handleError(e);
            }
            if (instance != null) {
                break;
            }
        }

        if (instance != null) {
            initialized = true;
            return true;
        } else {
            ErrorManager.handleError(log.toString());
            ErrorManager.handleError("No compatible version of Factions detected. " + pluginName + " will not be enabled.");
            return false;
        }
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
