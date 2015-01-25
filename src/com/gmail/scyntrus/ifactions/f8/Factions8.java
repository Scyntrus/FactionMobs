package com.gmail.scyntrus.ifactions.f8;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class Factions8 implements Factions {

    private static Factions8 instance;
    private Factions8() {
    }
    public static Factions get() {
        if (instance == null) {
            instance = new Factions8();
        }
        return instance;
    }

    private com.massivecraft.factions.Factions factionsInstance;
    private Method getByTagMethod;
    private Method fPlayerGetRoleMethod;
    
    @Override
    public boolean init() {
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getDeclaredField("i");
            factionsInstanceField.setAccessible(true);
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            getByTagMethod = com.massivecraft.factions.Factions.class.getMethod("getByTag", new Class<?>[]{String.class});
            fPlayerGetRoleMethod = com.massivecraft.factions.FPlayer.class.getMethod("getRole");
            Faction8.getRelationTo = com.massivecraft.factions.Faction.class.getMethod("getRelationTo", new Class<?>[]{com.massivecraft.factions.iface.RelationParticipator.class});
            Faction8.getFlag = com.massivecraft.factions.Faction.class.getMethod("getFlag", new Class<?>[]{com.massivecraft.factions.struct.FFlag.class});
            return true;
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return false;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return new Faction8(com.massivecraft.factions.Board.getFactionAt(new com.massivecraft.factions.FLocation(loc)));
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Faction8(getByTagMethod.invoke(factionsInstance, name));
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        return new Faction8(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
    }

    @Override
    public Rank getPlayerRank(Player player) {
        try {
            com.massivecraft.factions.struct.Rel role6 = (com.massivecraft.factions.struct.Rel) fPlayerGetRoleMethod.invoke(com.massivecraft.factions.FPlayers.i.get(player));
            return Rank.getByName(role6.name());
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return Rank.MEMBER;
    }

}
