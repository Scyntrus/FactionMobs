package com.gmail.scyntrus.ifactions.f6;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;

public class Factions6 implements Factions {

    private com.massivecraft.factions.Factions factionsInstance;
    private Method getByTagMethod;
    private static Method fPlayersGet; // Because Factions 1.6 and old 1.6-U had different method signatures

    private static Factions6 instance = null;
    
    private Factions6(Plugin plugin) {
        instance = this;
        try {
            Field factionsInstanceField = com.massivecraft.factions.Factions.class.getField("i");
            factionsInstance = (com.massivecraft.factions.Factions) factionsInstanceField.get(null);
            getByTagMethod = com.massivecraft.factions.Factions.class.getDeclaredMethod("getByTag", String.class);
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
            fPlayersGet = FactionsManager.tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", OfflinePlayer.class);
            if (fPlayersGet != null) {
                log.append("FOUND com.massivecraft.factions.FPlayers.get(OfflinePlayer)\n");
                System.out.println("["+pluginName+"] Factions 1.6-U detected");
                new Factions6(plugin);
            } else {
                fPlayersGet = FactionsManager.tryGetMethod(com.massivecraft.factions.FPlayers.class, "get", Player.class);
                if (fPlayersGet != null) {
                    log.append("FOUND com.massivecraft.factions.FPlayers.get(Player)\n");
                    System.out.println("["+pluginName+"] Factions 1.6 detected. It is recommended you update to Factions 2.");
                    new Factions6(plugin);
                }
            }
        }
        return instance;   
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
