package com.gmail.scyntrus.ifactions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Factions {
    Faction getFactionAt(Location loc);
    Faction getFactionByName(String name);
    Faction getPlayerFaction(Player player);
    Rank getPlayerRank(Player player);
    Faction getFactionFromNativeObject(Object nativeObject);
    boolean supportsLandOwnership();
    String getVersionString();
}
