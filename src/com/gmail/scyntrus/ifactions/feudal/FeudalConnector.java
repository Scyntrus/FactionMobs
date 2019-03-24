package com.gmail.scyntrus.ifactions.feudal;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import de.browniecodez.feudal.main.Main;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.kingdoms.Land;
import us.forseth11.feudal.user.User;

import java.util.ArrayList;

public class FeudalConnector implements Factions {

    private static FeudalConnector instance;

    private FeudalConnector(Plugin plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(new FeudalListener(plugin), plugin);
        if (FactionsManager.classExists("me.forseth11.Turrets.TurretAttackMobEvent")) {
            System.out.println("[" + plugin.getName() + "] Turrets detected.");
            plugin.getServer().getPluginManager().registerEvents(new TurretsListener(plugin), plugin);
        }
    }

    public static Factions get(Plugin plugin, StringBuilder log) {
        if (instance != null) {
            return instance;
        }
        String pluginName = plugin.getName();
        if (FactionsManager.classExists("us.forseth11.feudal.core.Feudal")) {
            log.append("FOUND us.forseth11.feudal.core.Feudal\n");
            System.out.println("[" + pluginName + "] Feudal detected.");
            new FeudalConnector(plugin);
        }
        return instance;
    }

    @Override
    public Faction getFactionAt(Location loc) {
        Land l = new Land(loc);
        Kingdom nativeObject = Main.getLandKingdom(l);
        return new FeudalKingdom(nativeObject);
    }

    @Override
    public Faction getFactionByName(String name) {
        ArrayList<Kingdom> kingdoms = Main.getKingdoms();
        // horribly inefficient but I can't do anything about it for now
        for (Kingdom k : kingdoms) {
            if (k.getName().equals(name)) {
                return new FeudalKingdom(k);
            }
        }
        return null;
    }

    @Override
    public Faction getPlayerFaction(Player player) {
        User u = Main.getUser(player.getUniqueId().toString());
        if (u == null) return null;
        Object nativeObject = Main.getKingdom(u.getKingdomUUID());
        return nativeObject != null ? new FeudalKingdom(nativeObject) : null;
    }

    @Override
    public Faction getFactionFromNativeObject(Object nativeObject) {
        return new FeudalKingdom(nativeObject);
    }

    @Override
    public Rank getPlayerRank(Player player) {
        String uuid = player.getUniqueId().toString();
        User u = Main.getUser(uuid);
        if (u == null) return Rank.UNKNOWN;
        Kingdom k = Main.getKingdom(u.getKingdomUUID());
        if (k == null) return Rank.UNKNOWN;
        us.forseth11.feudal.kingdoms.Rank rank = k.getRank(uuid);
        switch (rank) {
            case LEADER:
                return Rank.LEADER;
            case EXECUTIVE:
                return Rank.OFFICER;
            case MEMBER:
                return Rank.MEMBER;
            case GUEST:
                return Rank.RECRUIT;
            default:
                return Rank.MEMBER;
        }
    }

    @Override
    public boolean supportsLandOwnership() {
        return true;
    }

    @Override
    public String getVersionString() {
        return "Fe";
    }
}
