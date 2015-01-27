package com.gmail.scyntrus.ifactions.t;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Towny implements Factions {

    private static Towny instance;
    private Towny() {
    }
    public static Factions get() {
        if (instance == null) {
            instance = new Towny();
        }
        return instance;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        try {
            return new Town(TownyUniverse.getDataSource().getTown(TownyUniverse.getTownName(loc)));
        } catch (NotRegisteredException e) {
        }
        return null;
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Town(TownyUniverse.getDataSource().getTown(name));
        } catch (NotRegisteredException e) {
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        try {
            return new Town(TownyUniverse.getDataSource().getResident(player.getName()).getTown());
        } catch (NotRegisteredException e) {
        }
        return null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Town(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        try {
            if (TownyUniverse.getDataSource().getResident(player.getName()).isMayor())
                return Rank.LEADER;
            else
                return Rank.MEMBER;
        } catch (Exception e) {
            Utils.handleError(e);
        }
        return Rank.MEMBER;
    }

}
