package com.gmail.scyntrus.ifactions.f6;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class Factions6 implements Factions {

    private static Factions6 instance;
    private Factions6(Method fPlayersGet) {
        this.fPlayersGet = fPlayersGet;
    }
    public static Factions get(Method fPlayersGet) {
        if (instance == null) {
            instance = new Factions6(fPlayersGet);
        }
        return instance;
    }

    private com.massivecraft.factions.Factions factionsInstance;
    private Method getByTagMethod;
    private Method fPlayersGet; // Because Factions 1.6 and old 1.6-U had different method signatures

    @Override
    public boolean init() {
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getField("i");
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            getByTagMethod = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
            return true;
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return false;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return new Faction6(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Faction6(getByTagMethod.invoke(factionsInstance, name));
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        try {
            return new Faction6(((com.massivecraft.factions.FPlayer) fPlayersGet.invoke(com.massivecraft.factions.FPlayers.i, player)).getFaction());
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return null;
    }

    @Override
    public Rank getPlayerRank(Player player) {
        try {
            com.massivecraft.factions.struct.Role role6 = ((com.massivecraft.factions.FPlayer) fPlayersGet.invoke(com.massivecraft.factions.FPlayers.i, player)).getRole();
            return Rank.getByName(role6.name());
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return Rank.MEMBER;
    }

}
