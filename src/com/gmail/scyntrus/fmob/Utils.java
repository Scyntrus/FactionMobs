package com.gmail.scyntrus.fmob;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityAnimal;
import net.minecraft.server.v1_5_R3.EntityCreeper;
import net.minecraft.server.v1_5_R3.EntityEnderDragon;
import net.minecraft.server.v1_5_R3.EntityGhast;
import net.minecraft.server.v1_5_R3.EntityMonster;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.EntitySlime;
import net.minecraft.server.v1_5_R3.EntityTracker;
import net.minecraft.server.v1_5_R3.EntityTrackerEntry;
import net.minecraft.server.v1_5_R3.EntityWither;
import net.minecraft.server.v1_5_R3.EntityWolf;
import net.minecraft.server.v1_5_R3.EntityZombie;
import net.minecraft.server.v1_5_R3.Item;
import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.World;
import net.minecraft.server.v1_5_R3.WorldServer;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class Utils {
	public static int FactionCheck(Entity entity, Faction faction) {
		if (faction == null) {
			return 0;
		}
		if (entity instanceof EntityPlayer) {
			Player player = ((EntityPlayer)entity).getBukkitEntity();
			if (player.getGameMode() == GameMode.CREATIVE) return 1;
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
			if (fmob.getFaction() == null) {
				return 0;
			}
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
		} else if (!FactionMobs.attackMobs) {
			return 0;
		} else if (entity instanceof EntityAnimal) {
			return 0;
		} else if (entity instanceof EntityZombie) {
			if (FactionMobs.attackZombies) {
				return -1;
			}
			return 0;
		} else if (entity instanceof EntityMonster
				|| entity instanceof EntityGhast
				|| entity instanceof EntityEnderDragon
				|| entity instanceof EntityWither) {
			return -1;
		} else if (entity instanceof EntitySlime) {
			EntitySlime slime = (EntitySlime) entity;
			if (slime.getSize() > 1) {
				return -1;
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	public static void giveColorArmor(FactionMob entity) {
		int color = -1;
		if (entity.getFaction() == null) {
			return;
		} else if (FactionMobs.factionColors.containsKey(entity.getFaction().getTag())) {
			color = FactionMobs.factionColors.get(entity.getFaction().getTag());
		} else {
			FactionMobs.factionColors.put(entity.getFaction().getTag(), 10511680);
		}
		
		if (color == -1 || color == 10511680) {
			entity.setEquipment(1, new ItemStack(Item.LEATHER_BOOTS));
			entity.setEquipment(2, new ItemStack(Item.LEATHER_LEGGINGS));
			entity.setEquipment(3, new ItemStack(Item.LEATHER_CHESTPLATE));
			entity.setEquipment(4, new ItemStack(Item.LEATHER_HELMET));
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
	
	public FactionMob mobCreate() { // not implemented yet
		return null;
	}
	
	public static double dist3D(double x1, double x2, double y1, double y2, double z1, double z2) {
		return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2) + Math.pow(z1-z2,2));
	}
	
	public static double countMobPowerInFaction(Faction faction) {
		double power = 0;
		for (FactionMob fmob : FactionMobs.mobList) {
			if (fmob.getFactionName().equals(faction.getTag())) {
				power += fmob.getPowerCost();
			}
		}
		return power;
	}
	
	public static int countMobsInFaction(Faction faction) {
		int count = 0;
		for (FactionMob fmob : FactionMobs.mobList) {
			if (fmob.getFactionName().equals(faction.getTag())) {
				count++;
			}
		}
		return count;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean addEntity(World world, Entity entity) {
	    if (entity == null) return false;

	    int i = (int) Math.floor(entity.locX / 16.0D);
	    int j = (int) Math.floor(entity.locZ / 16.0D);
	    
	    if (!world.chunkProvider.isChunkLoaded(i, j) || entity.dead) return false;
	    
	    world.getChunkAt(i, j).a(entity);
	    if (!world.entityList.contains(entity)) world.entityList.add(entity);
	    
	    EntityTracker tracker = ((WorldServer) world).tracker;
	      
	    i = 80;
	    
	    int d = 0;
	    
	    try {
			Field field = EntityTracker.class.getDeclaredField("d"); //TODO: Update name on version change
			field.setAccessible(true);
			d = field.getInt(tracker);
		} catch (Exception e) {
		}
	    
	    if (i > d) i = d;
	    
	    if (tracker.trackedEntities.b(entity.id)) return false;

	    EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, 3, true);

	    try {
			Field field = EntityTracker.class.getDeclaredField("b"); //TODO: Update name on version change
			field.setAccessible(true);
			((Set) field.get(tracker)).add(entitytrackerentry);
		} catch (Exception e) {
		}
	    
	    tracker.trackedEntities.a(entity.id, entitytrackerentry);
	    entitytrackerentry.scanPlayers(world.players);
	    
	    return true;
	}
}
