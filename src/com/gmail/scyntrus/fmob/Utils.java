package com.gmail.scyntrus.fmob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import net.minecraft.server.v1_10_R1.Chunk;
import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityAnimal;
import net.minecraft.server.v1_10_R1.EntityCreeper;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EntitySlime;
import net.minecraft.server.v1_10_R1.EntityWolf;
import net.minecraft.server.v1_10_R1.EnumItemSlot;
import net.minecraft.server.v1_10_R1.IMonster;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.Items;
import net.minecraft.server.v1_10_R1.MathHelper;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.WorldServer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;

public class Utils {
    public static int FactionCheck(EntityLiving entity, Faction faction) {
        if (entity == null || faction == null || faction.isNone()) {
            return 0;
        }
        if (entity.getBukkitEntity().hasMetadata("MyPet"))
            return 0;
        if (entity instanceof EntityPlayer) {
            Player player = ((EntityPlayer)entity).getBukkitEntity();
            if (player.getGameMode() == GameMode.CREATIVE) return 1;
            return FactionsManager.getPlayerFaction(player).getRelationTo(faction);
        } else if (entity instanceof FactionMob) {
            FactionMob fmob = (FactionMob) entity;
            if (fmob.getFaction() == null) {
                return 0;
            }
            return fmob.getFaction().getRelationTo(faction);
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
        } else if (entity instanceof EntitySlime) {
            EntitySlime slime = (EntitySlime) entity;
            if (slime.getSize() > 1) {
                return -1;
            } else {
                return 0;
            }
        } else if (entity instanceof IMonster) {
            return -1;
        }
        return 1;
    }

    public static void giveColorArmor(FactionMob entity) {
        int color = -1;
        if (entity.getFaction() == null) {
            return;
        } else if (FactionMobs.factionColors.containsKey(entity.getFaction().getName())) {
            color = FactionMobs.factionColors.get(entity.getFaction().getName());
        } else {
            FactionMobs.factionColors.put(entity.getFaction().getName(), 10511680);
        }

        if (color == -1 || color == 10511680) {
            entity.getEntity().setSlot(EnumItemSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
            entity.getEntity().setSlot(EnumItemSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
            entity.getEntity().setSlot(EnumItemSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
            entity.getEntity().setSlot(EnumItemSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            return;
        }

        ItemStack[] itemStacks = {
                new ItemStack(Items.LEATHER_BOOTS),
                new ItemStack(Items.LEATHER_LEGGINGS),
                new ItemStack(Items.LEATHER_CHESTPLATE),
                new ItemStack(Items.LEATHER_HELMET)};

        for (ItemStack i : itemStacks) {
            NBTTagCompound n = i.getTag();
            if (n == null) {
                n = new NBTTagCompound();
                i.setTag(n);
            }
            NBTTagCompound n2 = n.getCompound("display");
            if (!n.hasKey("display"))
                n.set("display", n2);
            n2.setInt("color", color);
        }

        entity.getEntity().setSlot(EnumItemSlot.FEET, itemStacks[0]);
        entity.getEntity().setSlot(EnumItemSlot.LEGS, itemStacks[1]);
        entity.getEntity().setSlot(EnumItemSlot.CHEST, itemStacks[2]);
        entity.getEntity().setSlot(EnumItemSlot.HEAD, itemStacks[3]);
        return;
    }

    public FactionMob mobCreate() { // not implemented yet
        return null;
    }

    public static double dist3D(double x1, double x2, double y1, double y2, double z1, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double countMobPowerInFaction(Faction faction) {
        double power = 0;
        for (FactionMob fmob : FactionMobs.mobList) {
            if (fmob.getFactionName().equals(faction.getName())) {
                power += fmob.getPowerCost();
            }
        }
        return power;
    }

    public static int countMobsInFaction(Faction faction) {
        int count = 0;
        for (FactionMob fmob : FactionMobs.mobList) {
            if (fmob.getFactionName().equals(faction.getName())) {
                count++;
            }
        }
        return count;
    }

    public static void copyDefaultConfig() {
        InputStream stream = Utils.class.getResourceAsStream("/config.yml");
        if (stream == null) {
            if (!FactionMobs.silentErrors)
                System.out.println("Unable to find default config.yml");
            return;
        }
        OutputStream resStreamOut = null;
        int readBytes;
        byte[] buffer = new byte[4096];
        try {
            resStreamOut = new FileOutputStream(new File(FactionMobs.instance.getDataFolder(), "configDefaults.yml"));
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception e) {
            ErrorManager.handleError("Unable to create configDefaults.yml. Check permissions or create it manually.", e);
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                ErrorManager.handleError("Unable to close config.yml resource.", e);
            }
            try {
                if (resStreamOut != null)
                    resStreamOut.close();
            } catch (Exception e) {
                ErrorManager.handleError("Unable to close configDefaults.yml.", e);
            }
        }
    }

    private static class EntityHolder {
        EntityLiving val = null;
    }
    
    public static final double closeEnough = 2.25;
    private static double optimizedEntitySearchSlice(Faction faction, EntityLiving entity, EntityHolder result, Collection<Entity> slice, double x, double y, double z, double range2) {
        for (Entity entity1 : slice) {
            if (entity1.isAlive() && entity1 instanceof EntityLiving) {
                if (Utils.FactionCheck((EntityLiving) entity1, faction) == -1) {
                    double tempRange2 = (entity1.locX - x) * (entity1.locX - x)
                            + (entity1.locY - y) * (entity1.locY - y)
                            + (entity1.locZ - z) * (entity1.locZ - z);
                    if (tempRange2 < range2 && entity.hasLineOfSight(entity1)) {
                        range2 = tempRange2;
                        result.val = (EntityLiving) entity1;
                        if (range2 < closeEnough)
                            return 0;
                    }
                }
            }
        }
        return range2;
    }

    private static double optimizedEntitySearchChunk(Faction faction, EntityLiving entity, EntityHolder result, Chunk chunk, int starty, int disty, double x, double y, double z, double range2) {
        try {
            @SuppressWarnings("unchecked")
            Collection<Entity>[] entitySlices = (Collection<Entity>[]) ReflectionManager.chunk_EntitySlices.get(chunk);

            if (!entitySlices[starty].isEmpty()) {
                range2 = optimizedEntitySearchSlice(faction, entity, result, entitySlices[starty], x, y, z, range2);
                if (range2 == 0)
                    return 0;
            }
            
            for (int dy = 1; dy <= disty; dy++) {
                if (starty-dy > 0 && !entitySlices[starty-dy].isEmpty()) {
                    range2 = optimizedEntitySearchSlice(faction, entity, result, entitySlices[starty-dy], x, y, z, range2);
                    if (range2 == 0)
                        return 0;
                }
                if (starty+dy < entitySlices.length-1 && !entitySlices[starty+dy].isEmpty()) {
                    range2 = optimizedEntitySearchSlice(faction, entity, result, entitySlices[starty+dy], x, y, z, range2);
                    if (range2 == 0)
                        return 0;
                }
                if (result.val != null) {
                    return range2;
                }
            }
        } catch (Exception e) {
            ErrorManager.handleError(e);
            return range2;
        }
        return range2;
    }
    
    public static EntityLiving optimizedTargetSearch(FactionMob mob, double range) {
        double x = mob.getlocX();
        double y = mob.getlocY();
        double z = mob.getlocZ();
        int i = MathHelper.floor((x - range) / 16.0D);
        int j = MathHelper.floor((x + range) / 16.0D);
        int k = MathHelper.floor((z - range) / 16.0D);
        int l = MathHelper.floor((z + range) / 16.0D);
        int starty = MathHelper.floor(y / 16.0D);
        int disty = Math.max(MathHelper.floor(y + range / 16.0D) - starty, starty - MathHelper.floor(y - range / 16.0D));
        int startx = MathHelper.floor(x / 16.0D);
        int startz = MathHelper.floor(z / 16.0D);
        double range2 = range * range;
        starty = MathHelper.clamp(starty, 0, 15);
        
        WorldServer world = (WorldServer) mob.getEntity().getWorld();

        Faction faction = mob.getFaction();
        EntityLiving entity = mob.getEntity();
        EntityHolder result = new EntityHolder();

        range2 = optimizedEntitySearchChunk(faction, entity, result, world.getChunkAt(startx, startz), starty, disty, x, y, z, range2);
        if (range2 == 0) {
            return result.val;
        }
        
        for (int i1 = i; i1 <= j; i1++) {
            for (int j1 = k; j1 <= l; j1++) {
                if (i1 == startx && j1 == startz)
                    continue;
                if (world.getChunkProviderServer().isLoaded(i1, j1)) {
                    range2 = optimizedEntitySearchChunk(faction, entity, result, world.getChunkAt(i1, j1), starty, disty, x, y, z, range2);
                    if (range2 == 0) {
                        return result.val;
                    }
                }
            }
        }
        
        return result.val;
    }
    
    
    private static void optimizedEntityAgroChunk(Faction faction, Chunk chunk, double x, double y, double z, double range2, int y1, int y2, EntityLiving damager) {
        try {
            @SuppressWarnings("unchecked")
            Collection<Entity>[] entitySlices = (Collection<Entity>[]) ReflectionManager.chunk_EntitySlices.get(chunk);
            for (int k = y1; k <= y2; k++) {
                if (!entitySlices[k].isEmpty()) {
                    for (Entity entity1 : entitySlices[k]) {
                        if (entity1.isAlive() && entity1 instanceof FactionMob) {
                            FactionMob fmob = (FactionMob) entity1;
                            Faction otherFaction = fmob.getFaction();
                            if (faction.getRelationTo(otherFaction) == 1 && Utils.FactionCheck(damager, otherFaction) < 1
                                    && (entity1.locX - x) * (entity1.locX - x) + (entity1.locY - y) * (entity1.locY - y)
                                    + (entity1.locZ - z) * (entity1.locZ - z) < range2) {
                                fmob.softAgro(damager);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorManager.handleError(e);
        }
    }
    
    public static void optimizedAoeAgro(Faction faction, Location loc, double range, EntityLiving damager) {
        if (faction == null || faction.isNone() || !damager.isAlive())
            return;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        int i = MathHelper.floor((x - range) / 16.0D);
        int j = MathHelper.floor((x + range) / 16.0D);
        int k = MathHelper.floor((z - range) / 16.0D);
        int l = MathHelper.floor((z + range) / 16.0D);
        int y1 = MathHelper.clamp(MathHelper.floor(y - range / 16.0D), 0, 15);
        int y2 = MathHelper.clamp(MathHelper.floor(y + range / 16.0D), 0, 15);
        double range2 = range * range;
        
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        
        for (int i1 = i; i1 <= j; i1++) {
            for (int j1 = k; j1 <= l; j1++) {
                if (world.getChunkProviderServer().isLoaded(i1, j1)) {
                    optimizedEntityAgroChunk(faction, world.getChunkAt(i1, j1), x, y, z, range2, y1, y2, damager);
                }
            }
        }
    }
}
