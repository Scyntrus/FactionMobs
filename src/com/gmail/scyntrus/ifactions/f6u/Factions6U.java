package com.gmail.scyntrus.ifactions.f6u;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import com.gmail.scyntrus.ifactions.f6.FactionListener68;

public class Factions6U implements Factions {

    private com.massivecraft.factions.Factions factionsInstance;
    private Method getByTagMethod;
    private FPlayers fPlayersInstance;
    private static Method fPlayersGet;
    private Method fPlayerGetFactionMethod;
    private Method fPlayerGetRoleMethod;
    private Object boardInstance = null;
    private Method boardGetFactionAt;

    private static Factions6U instance;
    
    private Factions6U(Plugin plugin) {
        instance = this;
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("instance");
            factionsInstanceField.setAccessible(true);
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            Field fPlayersInstanceField = FPlayers.class.getDeclaredField("instance");
            fPlayersInstanceField.setAccessible(true);
            fPlayersInstance = (FPlayers) fPlayersInstanceField.get(null);
            fPlayerGetFactionMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getFaction");
            fPlayerGetRoleMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getRole");
            getByTagMethod = com.massivecraft.factions.Factions.class.getMethod("getByTag", String.class);
            boardInstance = Board.class.getMethod("getInstance").invoke(null);
            boardGetFactionAt = Board.class.getMethod("getFactionAt", FLocation.class);
            Faction6U.getRelationTo = com.massivecraft.factions.Faction.class.getMethod("getRelationTo", RelationParticipator.class);
            Faction6U.isNone = com.massivecraft.factions.Faction.class.getMethod("isNone");
            Faction6U.getTag = com.massivecraft.factions.Faction.class.getMethod("getTag");
            Faction6U.getPower = com.massivecraft.factions.Faction.class.getMethod("getPower");
            Faction6U.noMonstersInTerritory = com.massivecraft.factions.Faction.class.getMethod("noMonstersInTerritory");
        } catch (Exception e) {
            ErrorManager.handleError(e);
            instance = null;
        }
        plugin.getServer().getPluginManager().registerEvents(new FactionListener68(), plugin);
    }

    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) {
            return instance;
        }
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("com.massivecraft.factions.struct.Relation")) {
            log.append("FOUND com.massivecraft.factions.struct.Relation\n");
            fPlayersGet = FactionsManager.tryGetMethod(FPlayers.class, "getByOfflinePlayer", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.getByOfflinePlayer(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected.");
                new Factions6U(plugin);
            }
        }
        return instance;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        try {
            Object f;
            f = boardGetFactionAt.invoke(boardInstance, new FLocation(loc));
            return new Faction6U(f);
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Faction6U(getByTagMethod.invoke(factionsInstance, name));
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        try {
            Object fPlayer = fPlayersGet.invoke(fPlayersInstance, player);
            return fPlayer != null ? new Faction6U(fPlayerGetFactionMethod.invoke(fPlayer)) : null;
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Faction6U(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        try {
            Object fPlayer = fPlayersGet.invoke(fPlayersInstance, player);
            if (fPlayer == null) return Rank.UNKNOWN;
            Role role6 = (Role) fPlayerGetRoleMethod.invoke(fPlayer);
            return Rank.getByName(role6.name());
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return Rank.UNKNOWN;
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

    @Override
    public String getVersionString() {
        return "F1.6U";
    }
}
