package com.gmail.scyntrus.ifactions.f2;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;

public class Factions2 implements Factions{

    private static Factions2 instance = null;
    
    private Factions2(Plugin plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(new FactionListener2(), plugin);
    }

    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) return instance;
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("com.massivecraft.factions.Rel")) {
            log.append("FOUND com.massivecraft.factions.Rel\n");
            System.out.println("["+pluginName+"] Factions 2 detected");
            new Factions2(plugin);
        }
        return instance;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return new Faction2(BoardColl.get().getFactionAt(PS.valueOf(loc)));
    }

    @Override
    public Faction getFactionByName(String name) {
        return new Faction2(FactionColl.get().getByName(name));
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        MPlayer mPlayer = MPlayer.get(player);
        return mPlayer != null ? new Faction2(MPlayer.get(player).getFaction()) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Faction2(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        MPlayer mPlayer = MPlayer.get(player);
        if (mPlayer == null) return Rank.UNKNOWN;
        Rel rel = mPlayer.getRole();
        return Rank.getByName(rel.name());
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

    @Override
    public String getVersionString() {
        return "F2";
    }
}
