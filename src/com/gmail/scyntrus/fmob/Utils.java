package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityCreeper;
import net.minecraft.server.v1_4_R1.EntityMonster;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityWolf;
import net.minecraft.server.v1_4_R1.Item;
import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.NBTTagCompound;

import org.bukkit.entity.Player;

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
		} else if (entity instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf) entity;
			if (wolf.isTamed()) {
				if (wolf.getOwner() != null) {
					return FactionCheck(wolf.getOwner(), faction);
				} else {
					return 0;
				}
			} else if (wolf.isAngry()) {
				return -1;
			} else {
				return 0;
			}
		} else if (entity instanceof EntityCreeper) {
			return 1;
		} else if (entity instanceof EntityMonster) {
			return -1;
		}
		return 0;
	}
	
	public static void giveColorArmor(FactionMob entity) {
		int color = -1;
		if (FactionMobs.factionColors.containsKey(entity.getFaction().getTag())) {
			color = FactionMobs.factionColors.get(entity.getFaction().getTag());
		} else {
			FactionMobs.factionColors.put(entity.getFaction().getTag(), 10511680);
		}
		
		if (color == -1) {
			entity.setEquipment(1, new ItemStack(Item.LEATHER_BOOTS, 1, (short) 80));
			entity.setEquipment(2, new ItemStack(Item.LEATHER_LEGGINGS, 1, (short) 80));
			entity.setEquipment(3, new ItemStack(Item.LEATHER_CHESTPLATE, 1, (short) 80));
			entity.setEquipment(4, new ItemStack(Item.LEATHER_HELMET, 1, (short) 80));
			return;
		}
		
		ItemStack[] itemStacks = {
				new ItemStack(Item.LEATHER_BOOTS), 
				new ItemStack(Item.LEATHER_LEGGINGS), 
				new ItemStack(Item.LEATHER_CHESTPLATE),
				new ItemStack(Item.LEATHER_HELMET)};

	    for (ItemStack i : itemStacks) {
	    	NBTTagCompound n = i.getTag();
		    if (n == null) {
		      n = new NBTTagCompound();
		      i.setTag(n);
		    }
		    NBTTagCompound n2 = n.getCompound("display");
		    if (!n.hasKey("display")) n.setCompound("display", n2);
		    n2.setInt("color", color);
	    }
	    
        entity.setEquipment(1, itemStacks[0]);
        entity.setEquipment(2, itemStacks[1]);
        entity.setEquipment(3, itemStacks[2]);
        entity.setEquipment(4, itemStacks[3]);
        return;
	}
}
