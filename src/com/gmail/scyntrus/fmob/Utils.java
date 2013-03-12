package com.gmail.scyntrus.fmob;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityZombie;
import net.minecraft.server.v1_4_R1.Item;
import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.NBTTagCompound;

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
	
	public static void giveColorArmor(FactionMob entity, FactionMobs plugin) {
		int color = -1;
		if (plugin.factionColors.containsKey(entity.getFaction().getTag())) {
			color = plugin.factionColors.get(entity.getFaction().getTag());
		} else {
			plugin.factionColors.put(entity.getFaction().getTag(), 10511680);
		}
		
		if (color == -1) {
			entity.setEquipment(3, new ItemStack(Item.LEATHER_CHESTPLATE, 1, (short) 80));
			return;
		}
		
		ItemStack itemStack = new ItemStack(Item.LEATHER_CHESTPLATE);

	    NBTTagCompound localNBTTagCompound1 = itemStack.getTag();

	    if (localNBTTagCompound1 == null) {
	      localNBTTagCompound1 = new NBTTagCompound();
	      itemStack.setTag(localNBTTagCompound1);
	    }
	    NBTTagCompound localNBTTagCompound2 = localNBTTagCompound1.getCompound("display");
	    if (!localNBTTagCompound1.hasKey("display")) localNBTTagCompound1.setCompound("display", localNBTTagCompound2);

	    localNBTTagCompound2.setInt("color", color);
        entity.setEquipment(3, itemStack);
        return;
	}
}
