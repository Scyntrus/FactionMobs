package com.gmail.scyntrus.fmob;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityZombie;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class Utils {
	public static int FactionCheck(Entity entity, Faction faction) {
		if (entity instanceof EntityPlayer) {
			Player player = ((EntityPlayer)entity).getBukkitEntity();
			switch (FPlayers.i.get(player).getFaction().getRelationTo(faction)) {
			case ENEMY:
				return -1;
			case NEUTRAL:
				return 0;
			case ALLY:
			case MEMBER:
				return 1;
			}
		} else if (entity instanceof FactionMob) {
			FactionMob fmob = (FactionMob) entity;
			switch (fmob.getFaction().getRelationTo(faction)) {
			case ENEMY:
				return -1;
			case NEUTRAL:
				return 0;
			case ALLY:
			case MEMBER:
				return 1;
			}
		} else {
			if (entity instanceof EntityZombie) {
				return -1;
			}
		}
		return 0;
	}
}
