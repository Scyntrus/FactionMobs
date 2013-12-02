package com.gmail.scyntrus.ifactions;

import org.bukkit.entity.Player;

import com.gmail.scyntrus.fmob.FactionMobs;

public class UPlayer {
	public static Faction getPlayerFaction(Player player) {
		if (FactionMobs.factionsVersion == 2) {
			return new Faction(com.massivecraft.factions.entity.UPlayer.get(player).getFaction());
		} else if (FactionMobs.factionsVersion == 6) {
			return new Faction(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
		} else if (FactionMobs.factionsVersion == 8) {
			return new Faction(com.massivecraft.factions.FPlayers.i.get(player).getFaction());
		}
		return null;
	}
}
