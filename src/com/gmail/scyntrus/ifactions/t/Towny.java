package com.gmail.scyntrus.ifactions.t;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Towny implements Factions {

    private static Towny instance;
    
    private Towny(Plugin plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(new TownyListener(), plugin);
    }

    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) {
            return instance;
        }
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("com.palmergames.bukkit.towny.Towny")) {
            log.append("FOUND com.palmergames.bukkit.towny.Towny\n");
            System.out.println("["+pluginName+"] Towny detected.");
            new Towny(plugin);
        }
        return instance;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        try {
            String townName = TownyUniverse.getTownName(loc);
            if (townName == null)
                return null;
            return new Town(TownyUniverse.getDataSource().getTown(townName));
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Faction getFactionByName(String name) {
        try {
            return new Town(TownyUniverse.getDataSource().getTown(name));
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            return resident != null ? new Town(resident.getTown()) : null;
        } catch (Exception ignored) {
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
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            if (resident == null) return Rank.UNKNOWN;
            if (!resident.hasTown()) return Rank.UNKNOWN;
            if (resident.isMayor()) return Rank.LEADER;
            return Rank.MEMBER;
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
        return "T";
    }
}
