package com.gmail.scyntrus.ifactions.f6u;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class Factions6U implements Factions {

    private static Factions6U instance;
    private Factions6U(Method fPlayersGet) {
        this.fPlayersGet = fPlayersGet;
    }
    public static Factions get(Method fPlayersGet) {
        if (instance == null) {
            instance = new Factions6U(fPlayersGet);
        }
        return instance;
    }

    private com.massivecraft.factions.Factions factionsInstance;
    private Method getByTagMethod;
    private com.massivecraft.factions.FPlayers fPlayersInstance;
    private Method fPlayersGet;
    private Method fPlayerGetFactionMethod;
    private Method fPlayerGetRoleMethod;
    private Object boardInstance = null;
    private Method boardGetFactionAt;

    @Override
    public boolean init() {
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("instance");
            factionsInstanceField.setAccessible(true);
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            Field fPlayersInstanceField = com.massivecraft.factions.FPlayers.class.getDeclaredField("instance");
            fPlayersInstanceField.setAccessible(true);
            fPlayersInstance = (com.massivecraft.factions.FPlayers) fPlayersInstanceField.get(null);
            fPlayerGetFactionMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getFaction");
            fPlayerGetRoleMethod = Class.forName("com.massivecraft.factions.FPlayer").getMethod("getRole");
            getByTagMethod = com.massivecraft.factions.Factions.class.getMethod("getByTag", new Class<?>[]{String.class});
            boardInstance = com.massivecraft.factions.Board.class.getMethod("getInstance").invoke(null);
            boardGetFactionAt = com.massivecraft.factions.Board.class.getMethod("getFactionAt", new Class<?>[]{com.massivecraft.factions.FLocation.class});
            Faction6U.getRelationTo = com.massivecraft.factions.Faction.class.getMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
            Faction6U.isNone = com.massivecraft.factions.Faction.class.getMethod("isNone");
            Faction6U.getTag = com.massivecraft.factions.Faction.class.getMethod("getTag");
            Faction6U.getPower = com.massivecraft.factions.Faction.class.getMethod("getPower");
            Faction6U.noMonstersInTerritory = com.massivecraft.factions.Faction.class.getMethod("noMonstersInTerritory");
            return true;
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return false;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        try {
            Object f;
            f = boardGetFactionAt.invoke(boardInstance, new com.massivecraft.factions.FLocation(loc));
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
            return new Faction6U(fPlayerGetFactionMethod.invoke(fPlayersGet.invoke(fPlayersInstance, player)));
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
            com.massivecraft.factions.struct.Role role6 = (com.massivecraft.factions.struct.Role) fPlayerGetRoleMethod.invoke(fPlayersGet.invoke(fPlayersInstance, player));
            return Rank.getByName(role6.name());
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return Rank.MEMBER;
    }

}
