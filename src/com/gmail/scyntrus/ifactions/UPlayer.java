package com.gmail.scyntrus.ifactions;

import org.bukkit.entity.Player;

public class UPlayer {
	public static Faction getPlayerFaction(Player player) {
		if (Factions.factionsVersion == 2) {
			return new Faction(com.massivecraft.factions.entity.UPlayer.get(player).getFaction());
		} else if (Factions.factionsVersion == 6) {
			return new Faction(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
		} else if (Factions.factionsVersion == 8) {
			return new Faction(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
		}
		return null;
	}
}
