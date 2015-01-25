package com.gmail.scyntrus.ifactions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Factions {
    public Faction getFactionAt(Location loc);
    public Faction getFactionByName(String name);
    public Faction getPlayerFaction(Player player);
    public Rank getPlayerRank(Player player);
    public boolean init();
}
