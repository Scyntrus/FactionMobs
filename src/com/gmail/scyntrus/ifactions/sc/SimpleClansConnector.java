package com.gmail.scyntrus.ifactions.sc;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SimpleClansConnector implements Factions {
    
    private SimpleClans SCInstance;

    private static SimpleClansConnector instance;
    
    private SimpleClansConnector(Plugin plugin) {
        instance = this;
        try {
            for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
                if (p instanceof SimpleClans) {
                    this.SCInstance = (SimpleClans) p;
                    plugin.getServer().getPluginManager().registerEvents(new SimpleClansListener(), plugin);
                    return;
                }
            }
        } catch (NoClassDefFoundError e) {
            ErrorManager.handleError(e);
        }
        instance = null;
    }


    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) {
            return instance;
        }
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("net.sacredlabyrinth.phaed.simpleclans.SimpleClans")) {
            log.append("FOUND net.sacredlabyrinth.phaed.simpleclans.SimpleClans\n");
            System.out.println("["+pluginName+"] SimpleClans detected.");
            new SimpleClansConnector(plugin);
        }
        return instance;
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
        ClanPlayer cPlayer = SCInstance.getClanManager().getClanPlayer(player);
        if (cPlayer == null) return null;
        Object nativeObject = cPlayer.getClan();
        return nativeObject != null ? new SimpleClan(nativeObject) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new SimpleClan(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        ClanPlayer cPlayer = SCInstance.getClanManager().getClanPlayer(player);
        if (cPlayer == null) return null;
        if (cPlayer.getClan() == null) return Rank.UNKNOWN;
        if (cPlayer.isLeader()) return Rank.LEADER;
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
