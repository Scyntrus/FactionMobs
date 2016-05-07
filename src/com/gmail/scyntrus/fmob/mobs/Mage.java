package com.gmail.scyntrus.fmob.mobs;

import java.util.LinkedHashSet;

import net.minecraft.server.v1_9_R1.AttributeInstance;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EntityPotion;
import net.minecraft.server.v1_9_R1.EntityProjectile;
import net.minecraft.server.v1_9_R1.EntityWitch;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.EnumMonsterType;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.MobEffects;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PotionRegistry;
import net.minecraft.server.v1_9_R1.PotionUtil;
import net.minecraft.server.v1_9_R1.Potions;
import net.minecraft.server.v1_9_R1.SoundEffects;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.ReflectionManager;
import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;

public class Mage extends EntityWitch implements FactionMob {

    public static final String typeName = "Mage";
    public Location spawnLoc = null;
    public Faction faction = null;
    public String factionName = "";
    public EntityLiving attackedBy = null;
    public EntityLiving target = null;
    public static float maxHp = 20;
    public static Boolean enabled = true;
    public static double powerCost = 0;
    public static double moneyCost = 0;
    public static double range = 16;
    public static int drops = 0;
    private int retargetTime = 0;
    private double moveSpeed;

    public double poiX=0, poiY=0, poiZ=0;
    public String order = "poi";

    public Mage(World world) {
        super(world);
        this.forceDie();
    }

    public Mage(Location spawnLoc, Faction faction) {
        super(((CraftWorld) spawnLoc.getWorld()).getHandle());
        this.setSpawn(spawnLoc);
        this.setFaction(faction);
        Utils.giveColorArmor(this);
        this.persistent = true;
        this.fireProof = false;
        this.canPickUpLoot = false;
        this.moveSpeed = FactionMobs.mobSpeed;
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.moveSpeed);
        getAttributeInstance(GenericAttributes.maxHealth).setValue(maxHp);
        this.setHealth(maxHp);
        this.P = 1.5F;
        this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), Potions.x)); // TODO: Update name on version change
        this.retargetTime = FactionMobs.random.nextInt(40);

        if (ReflectionManager.good_Navigation_Distance) {
            try {
                AttributeInstance e = (AttributeInstance) ReflectionManager.navigation_Distance.get(this.getNavigation());
                e.setValue(FactionMobs.mobNavRange);
            } catch (Exception e) {
                ErrorManager.handleError(e);
            }
        }
        if (ReflectionManager.good_PathfinderGoalSelector_GoalSet) {
            try {
                @SuppressWarnings("rawtypes")
                LinkedHashSet tempSet1 = (LinkedHashSet) ReflectionManager.pathfinderGoalSelector_GoalSet.get(this.goalSelector);
                tempSet1.clear();
                @SuppressWarnings("rawtypes")
                LinkedHashSet tempSet2 = (LinkedHashSet) ReflectionManager.pathfinderGoalSelector_GoalSet.get(this.targetSelector);
                tempSet2.clear();
            } catch (Exception e) {
                ErrorManager.handleError(e);
            }
        }

        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.getBukkitEntity().setMetadata("CustomEntity", new FixedMetadataValue(FactionMobs.instance, true));
        this.getBukkitEntity().setMetadata("FactionMob", new FixedMetadataValue(FactionMobs.instance, true));
    }

    @Override
    public void n() {
        super.n();
        if (--retargetTime < 0) {
            retargetTime = FactionMobs.responseTime;
            if (this.getGoalTarget() == null || !this.getGoalTarget().isAlive()) {
                this.findTarget();
            } else {
                double dist = Utils.dist3D(this.locX, this.getGoalTarget().locX, this.locY, this.getGoalTarget().locY, this.locZ, this.getGoalTarget().locZ);
                if (dist > range) {
                    this.findTarget();
                } else if (dist > 5) {
                    this.findCloserTarget();
                }
            }
            if (this.getGoalTarget() == null) {
                if (this.order.equals("home") || this.order == null || this.order.equals("")) {
                    this.getNavigation().a(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ(), 1.0);
                    this.order = "home";
                    return;
                } else if (this.order.equals("poi")) {
                    this.getNavigation().a(this.poiX, this.poiY, this.poiZ, 1.0);
                    return;
                } else if (this.order.equals("wander")) {
                    return;
                } else if (this.order.equals("phome")) {
                    this.getNavigation().a(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ(), FactionMobs.mobPatrolSpeed);
                    if (Utils.dist3D(this.locX,this.spawnLoc.getX(),this.locY,this.spawnLoc.getY(),this.locZ,this.spawnLoc.getZ()) < 1) {
                        this.order = "ppoi";
                    }
                    return;
                } else if (this.order.equals("ppoi")) {
                    this.getNavigation().a(poiX, poiY, poiZ, FactionMobs.mobPatrolSpeed);
                    if (Utils.dist3D(this.locX,this.poiX,this.locY,this.poiY,this.locZ,this.poiZ) < 1) {
                        this.order = "phome";
                    }
                    return;
                } else if (this.order.equals("path")) {
                    this.getNavigation().a(poiX, poiY, poiZ, 1.0);
                    if (Utils.dist3D(this.locX,this.poiX,this.locY,this.poiY,this.locZ,this.poiZ) < 1) {
                        this.order = "home";
                    }
                    return;
                }
            }
        }
        return;
    }

    private void setSpawn(Location loc) {
        spawnLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setPoi(loc.getX(),loc.getY(),loc.getZ());
        this.order = "home";
    }

    @Override
    public EntityLiving findCloserTarget() {
        if (this.attackedBy != null) {
            if (this.attackedBy.isAlive()
                    && this.attackedBy.world.getWorldData().getName().equals(this.world.getWorldData().getName())
                    && Utils.FactionCheck(this.attackedBy, this.faction) < 1) {
                double dist = Utils.dist3D(this.locX, this.attackedBy.locX, this.locY, this.attackedBy.locY, this.locZ, this.attackedBy.locZ);
                if (dist < 16) {
                    this.setTarget(this.attackedBy);
                    return this.attackedBy;
                } else if (dist > 32) {
                    this.attackedBy = null;
                }
            } else {
                this.attackedBy = null;
            }
        }
        EntityLiving e = Utils.optimizedTargetSearch(this, Utils.closeEnough);
        if (e != null)
            this.setTarget(e);
        return null;
    }

    @Override
    public void findTarget() {
        this.setTarget(Utils.optimizedTargetSearch(this, range));
        return;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float i) {
        boolean out = super.damageEntity(damagesource, i);
        if (!out)
            return out;
        EntityLiving damager;
        if (damagesource.getEntity() instanceof EntityLiving) {
            damager = (EntityLiving) damagesource.getEntity();
        } else if (damagesource.getEntity() instanceof EntityProjectile) {
            damager = ((EntityProjectile) damagesource.getEntity()).getShooter();
        } else {
            return out;
        }
        switch (Utils.FactionCheck(damager, this.faction)) {
            case 1:
                this.findTarget();
                if (damager instanceof EntityPlayer) {
                    this.lastDamageByPlayerTime = 0;
                }
                break;
            case 0:
            case -1:
                if (damager instanceof EntityLiving) {
                    this.attackedBy = damager;
                    this.setTarget(damager);
                } else if (damagesource.getEntity() instanceof EntityProjectile) {
                    EntityProjectile p = (EntityProjectile) damagesource.getEntity();
                    this.attackedBy = p.getShooter();
                    this.setTarget(p.getShooter());
                } else {
                    this.findTarget();
                }
                break;
        }
        return out;
    }

    @Override
    public boolean canSpawn() {
        return true;
    }

    @Override
    public Faction getFaction() {
        if (this.faction == null) {
            this.setFaction(FactionsManager.getFactionByName(factionName));
        }
        if (this.faction == null) {
            this.forceDie();
            System.out.println("[Error] Found and removed factionless faction mob");
        }
        return this.faction;
    }

    @Override
    public void setFaction(Faction faction) {
        if (faction == null) return;
        this.faction = faction;
        if (faction.isNone()) this.forceDie();
        this.factionName = faction.getName();
        if (FactionMobs.displayMobFaction) {
            this.setCustomName(ChatColor.YELLOW + this.factionName + " " + typeName);
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void setTarget(EntityLiving entity) {
        this.target = entity;
        if (entity instanceof EntityLiving) {
            this.setGoalTarget(entity);
        } else if (entity == null) {
            this.setGoalTarget(null);
        }
        if (this.getGoalTarget() != null && !this.getGoalTarget().isAlive()) {
            this.setGoalTarget(null);
        }
    }

    @Override
    public boolean setGoalTarget(EntityLiving entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (this.target instanceof EntityLiving && this.target.isAlive()) {
            super.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        } else {
            super.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, false);
        }
        return true;
    }

    @Override
    public void updateMob() {
        this.setFaction(FactionsManager.getFactionByName(factionName));
        if (this.faction == null || this.faction.isNone()) {
            this.forceDie();
            return;
        }
        if (this.target instanceof EntityLiving && this.target.isAlive()) {
            this.setGoalTarget(this.target);
        } else {
            this.findTarget();
        }
        Utils.giveColorArmor(this);
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public Location getSpawn() {
        return this.spawnLoc;
    }

    @Override
    public double getlocX() {
        return this.locX;
    }

    @Override
    public double getlocY() {
        return this.locY;
    }

    @Override
    public double getlocZ() {
        return this.locZ;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public double getPowerCost() {
        return powerCost;
    }

    @Override
    public double getMoneyCost() {
        return moneyCost;
    }

    @Override
    public boolean isTypeNotPersistent() {
        return false;
    }

    @Override
    public double getPoiX() {
        return this.poiX;
    }

    @Override
    public double getPoiY() {
        return this.poiY;
    }

    @Override
    public double getPoiZ() {
        return this.poiZ;
    }

    @Override
    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public void setPoi(double x, double y, double z) {
        this.poiX = x;
        this.poiY = y;
        this.poiZ = z;
    }

    @Override
    public String getOrder() {
        return this.order;
    }

    @Override
    public EntityCreature getEntity() {
        return this;
    }

    @Override
    public String getFactionName() {
        if (this.factionName == null) this.factionName = "";
        return this.factionName;
    }

    @Override
    public void die() {
        if (this.getHealth() <= 0) {
            super.die();
            this.setHealth(0);
            this.setSlot(EnumItemSlot.CHEST, null);
            this.setSlot(EnumItemSlot.FEET, null);
            this.setSlot(EnumItemSlot.HEAD, null);
            this.setSlot(EnumItemSlot.LEGS, null);
            this.setSlot(EnumItemSlot.MAINHAND, null);
            this.setSlot(EnumItemSlot.OFFHAND, null);
            if (FactionMobs.mobList.contains(this)) {
                FactionMobs.mobList.remove(this);
            }
        }
    }

    @Override
    public void forceDie() {
        this.setHealth(0);
        this.die();
    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) {
        return false;
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    @Override
    public void f(NBTTagCompound nbttagcompound) {
        this.die();
    }

    @Override
    public void clearAttackedBy() {
        if (this.target == this.attackedBy) {
            this.setTarget(null);
        }
        this.attackedBy = null;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    @Override
    public int getDrops() {
        return drops;
    }

    @Override
    public boolean softAgro(EntityLiving entity) {
        if (this.attackedBy == null) {
            this.attackedBy = entity;
            this.setTarget(entity);
            return true;
        }
        return false;
    }

    @Override
    public void setHealth(float f) {
        this.datawatcher.set(HEALTH, Float.valueOf(MathHelper.a(f, 0.0F, maxHp)));
    }

    @Override
    public void m() {
        if (this.getHealth() > 0) {
            this.dead = false;
        }
        this.ak = false;
        super.m();
    }

    @Override
    public void a(EntityLiving paramEntityLiving, float paramFloat) {  //TODO: Update name on version change
        if (o()) { //TODO: Update name on version change
            return;
        }

        double d1 = paramEntityLiving.locY + paramEntityLiving.getHeadHeight() - 1.100000023841858D;
        double d2 = paramEntityLiving.locX + paramEntityLiving.motX - this.locX;
        double d3 = d1 - this.locY;
        double d4 = paramEntityLiving.locZ + paramEntityLiving.motZ - this.locZ;
        float f = MathHelper.sqrt(d2 * d2 + d4 * d4);

        PotionRegistry localPotionRegistry = Potions.x;
        if ((f >= 8.0F) && (!paramEntityLiving.hasEffect(MobEffects.SLOWER_MOVEMENT)))
            localPotionRegistry = Potions.r;
        else if ((paramEntityLiving.getHealth() >= 8.0F) && (!paramEntityLiving.hasEffect(MobEffects.POISON))
                && (paramEntityLiving.getMonsterType() != EnumMonsterType.UNDEAD)
                && (paramEntityLiving.getMonsterType() != EnumMonsterType.ARTHROPOD))
            localPotionRegistry = Potions.z;
        else if ((f <= 3.0F) && (!paramEntityLiving.hasEffect(MobEffects.WEAKNESS)) && (this.random.nextFloat() < 0.25F)) {
            localPotionRegistry = Potions.I;
        }
        else if (paramEntityLiving.getMonsterType() == EnumMonsterType.UNDEAD) {
            localPotionRegistry = Potions.v;
        }
        
        this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), localPotionRegistry));

        EntityPotion localEntityPotion = new EntityPotion(this.world, this, PotionUtil.a(new ItemStack(Items.SPLASH_POTION), localPotionRegistry));
        localEntityPotion.pitch -= -20.0F;
        localEntityPotion.shoot(d2, d3 + f * 0.2F, d4, 0.75F, 8.0F);
        this.world.a(null, this.locX, this.locY, this.locZ, SoundEffects.gD, bz(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);

        this.world.addEntity(localEntityPotion);
    }
}
