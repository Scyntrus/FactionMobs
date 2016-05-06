package com.gmail.scyntrus.ifactions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface Factions {
    public Faction getFactionAt(Location loc);
    public Faction getFactionByName(String name);
    public Faction getPlayerFaction(Player player);
    public Rank getPlayerRank(Player player);
    public boolean init(Plugin plugin);
    public Faction getFactionFromNativeObject(Object nativeObject);
    public boolean supportsLandOwnership();
    public abstract String getVersionString();
}
