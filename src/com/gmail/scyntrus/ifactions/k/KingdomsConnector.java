package com.gmail.scyntrus.ifactions.k;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.land.SimpleChunkLocation;
import org.kingdoms.manager.game.GameManagement;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class KingdomsConnector implements Factions {

    private static KingdomsConnector instance;
    private KingdomsConnector() {
    }
    public static Factions get() {
        if (instance == null) {
            instance = new KingdomsConnector();
        }
        return instance;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        Object nativeObject = GameManagement.getLandManager().getOrLoadLand(new SimpleChunkLocation(loc.getChunk()));
        return nativeObject != null ? new Kingdom(nativeObject) : null;
    }

    @Override
    public Faction getFactionByName(String name) {
        Object nativeObject = GameManagement.getKingdomManager().getOrLoadKingdom(name);
        return nativeObject != null ? new Kingdom(nativeObject) : null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        Object nativeObject = GameManagement.getPlayerManager().getSession(player).getKingdom();
        return nativeObject != null ? new Kingdom(nativeObject) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Kingdom(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        org.kingdoms.constants.Rank rank = GameManagement.getPlayerManager().getSession(player).getRank();
        switch (rank) {
            case KING:
                return Rank.LEADER;
            case MODS:
                return Rank.OFFICER;
            case ALL:
                return Rank.MEMBER;
            default:
                return Rank.MEMBER;
        }
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

}
