package com.gmail.scyntrus.ifactions.k;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kingdoms.constants.land.SimpleChunkLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

public class KingdomsConnector implements Factions {

    private static KingdomsConnector instance;
    
    private KingdomsConnector(Plugin plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(new KingdomsListener(), plugin);
    }

    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) {
            return instance;
        }
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("org.kingdoms.constants.kingdom.Kingdom")) {
            log.append("FOUND org.kingdoms.constants.kingdom.Kingdom\n");
            System.out.println("["+pluginName+"] Kingdoms detected.");
            new KingdomsConnector(plugin);
        }
        return instance;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        String name = GameManagement.getLandManager().getOrLoadLand(new SimpleChunkLocation(loc.getChunk())).getOwner();
        return this.getFactionByName(name);
    }

    @Override
    public Faction getFactionByName(String name) {
        Object nativeObject = GameManagement.getKingdomManager().getOrLoadKingdom(name);
        return nativeObject != null ? new Kingdom(nativeObject) : null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        KingdomPlayer kPlayer = GameManagement.getPlayerManager().getSession(player);
        if (kPlayer == null) return null;
        Object nativeObject = kPlayer.getKingdom();
        return nativeObject != null ? new Kingdom(nativeObject) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Kingdom(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        KingdomPlayer kPlayer = GameManagement.getPlayerManager().getSession(player);
        if (kPlayer == null) return null;
        org.kingdoms.constants.Rank rank = kPlayer.getRank();
        switch (rank) {
            case KING:
                return Rank.LEADER;
            case MODS:
            case GENERALS:
                return Rank.OFFICER;
            case ALL:
                return Rank.MEMBER;
            default:
                return Rank.UNKNOWN;
        }
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

    @Override
    public String getVersionString() {
        return "K";
    }
}
