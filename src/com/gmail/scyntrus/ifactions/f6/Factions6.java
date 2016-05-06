package com.gmail.scyntrus.ifactions.f6;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;

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
    public boolean init(Plugin plugin) {
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getField("i");
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            getByTagMethod = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", new Class<?>[]{String.class});
            plugin.getServer().getPluginManager().registerEvents(new FactionListener68(), plugin);
            return true;
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return false;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return new Faction6(Board.getFactionAt(new FLocation(loc)));
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Faction6(getByTagMethod.invoke(factionsInstance, name));
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        try {
            return new Faction6(((FPlayer) fPlayersGet.invoke(FPlayers.i, player)).getFaction());
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Faction6(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        try {
            Role role6 = ((FPlayer) fPlayersGet.invoke(FPlayers.i, player)).getRole();
            return Rank.getByName(role6.name());
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
        return Rank.MEMBER;
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

    @Override
    public String getVersionString() {
        return "F1.6";
    }
}
