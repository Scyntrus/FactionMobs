package com.gmail.scyntrus.ifactions.f2;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.Rank;

public class Factions2 implements Factions{

    private static Factions2 instance;
    private Factions2() {

    }
    public static Factions get() {
        if (instance == null) {
            instance = new Factions2();
        }
        return instance;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        return new Faction2(com.massivecraft.factions.entity.BoardColl.get().getFactionAt(com.massivecraft.massivecore.ps.PS.valueOf(loc)));
    }

    @Override
    public Faction getFactionByName(String name) {
        return new Faction2(com.massivecraft.factions.entity.FactionColl.get().getByName(name));
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        return new Faction2(com.massivecraft.factions.entity.MPlayer.get(player).getFaction());
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new Faction2(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        com.massivecraft.factions.Rel rel = com.massivecraft.factions.entity.MPlayer.get(player).getRole();
        return Rank.getByName(rel.name());
    }
    
    @Override
    public boolean supportsLandOwnership() {
        return true;
    }
}
