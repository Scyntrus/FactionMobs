package com.gmail.scyntrus.ifactions.sc;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class SimpleClansConnector implements Factions {

    private static SimpleClansConnector instance;
    private SimpleClansConnector() {
    }
    public static Factions get() {
        if (instance == null) {
            instance = new SimpleClansConnector();
        }
        return instance;
    }
    
    private static SimpleClans SCInstance;

    @Override
    public boolean init(Plugin plugin) {
        try {
            for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
                if (p instanceof SimpleClans) {
                    SCInstance = (SimpleClans) p;
                    plugin.getServer().getPluginManager().registerEvents(new SimpleClansListener(), plugin);
                    return true;
                }
            }
        } catch (NoClassDefFoundError e) {
            ErrorManager.handleError(e);
        }

        return false;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return null;
    }

    @Override
    public Faction getFactionByName(String name) {
        Object nativeObject = SCInstance.getClanManager().getClan(name);
        return nativeObject != null ? new SimpleClan(nativeObject) : null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        Object nativeObject = SCInstance.getClanManager().getClanPlayer(player).getClan();
        return nativeObject != null ? new SimpleClan(nativeObject) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new SimpleClan(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        if (SCInstance.getClanManager().getClanPlayer(player).isLeader())
            return Rank.LEADER;
        return Rank.MEMBER;
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return false;
    }

    @Override
    public String getVersionString() {
        return "SC";
    }
}
