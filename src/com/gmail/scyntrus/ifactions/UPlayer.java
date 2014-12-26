package com.gmail.scyntrus.ifactions;

import org.bukkit.entity.Player;

public class UPlayer {
	public static Faction getPlayerFaction(Player player) {
		if (Factions.factionsVersion == 2) {
			return new Faction2(com.massivecraft.factions.entity.MPlayer.get(player).getFaction());
		} else if (Factions.factionsVersion == 6) {
			try {
				return new Faction6((
						(com.massivecraft.factions.FPlayer)Factions.fPlayerGet.invoke(com.massivecraft.factions.FPlayers.i, player)
					).getFaction());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Factions.factionsVersion == 8) {
			return new Faction8(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
		}
		return null;
	}
	
	public static FRank getPlayerRank(Player player) {
        switch (Factions.factionsVersion) {
            case 6:
                try {
                    com.massivecraft.factions.FPlayer fPlayer = (com.massivecraft.factions.FPlayer) Factions.fPlayerGet.invoke(com.massivecraft.factions.FPlayers.i, player);
                    return FRank.getByName(fPlayer.getRole().name());
                } catch (Exception e) {
                    e.printStackTrace();
                    return FRank.MEMBER;
                }
            case 8:
                com.massivecraft.factions.struct.Role role = com.massivecraft.factions.FPlayers.i.get(player).getRole();
                return FRank.getByName(role.name());
            case 2:
                com.massivecraft.factions.Rel rel = com.massivecraft.factions.entity.MPlayer.get(player).getRole();
                return FRank.getByName(rel.name());
            default:
                return FRank.MEMBER;
        }
	}
}
