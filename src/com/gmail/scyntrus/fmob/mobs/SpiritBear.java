package com.gmail.scyntrus.fmob.mobs;

import com.gmail.scyntrus.fmob.ErrorManager;
import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.Messages;
import com.gmail.scyntrus.fmob.Option;
import com.gmail.scyntrus.fmob.PathHelpEntity;
import com.gmail.scyntrus.fmob.PathfinderGoalFmobCommand;
import com.gmail.scyntrus.fmob.ReflectionManager;
import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityAgeable;
import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityPolarBear;
import net.minecraft.server.v1_14_R1.EntityProjectile;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.EnumMonsterType;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IWorldReader;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_14_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_14_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Set;

public class SpiritBear extends EntityPolarBear implements FactionMob {

    public static final String typeName = "SpiritBear";
    public static String localizedName = typeName;

    public static final double range = 16;
    @Option(key = "SpiritBear.maxHp", min = 1)
    public static float maxHp = 30;
    @Option(key = "SpiritBear.enabled")
    public static Boolean enabled = true;
    @Option(key = "SpiritBear.powerCost", min = 0)
    public static double powerCost = 0;
    @Option(key = "SpiritBear.moneyCost", min = 0)
    public static double moneyCost = 0;
    @Option(key = "SpiritBear.damage", min = 0)
    public static double damage = 0;
    @Option(key = "SpiritBear.drops")
    public static Material drops = null;

    public Faction faction = null;
    public String factionName = "";
    public Location spawnLoc = null;
    private boolean attackAll = false;

    public EntityLiving attackedBy = null;
    public EntityLiving target = null;
    private int retargetTime = 0;
    public double poiX = 0, poiY = 0, poiZ = 0;
    public Command command = Command.poi;

    private static final PathHelpEntity p = new PathHelpEntity();

    public SpiritBear(EntityTypes<? extends Entity> type, World world) {
        super(EntityTypes.POLAR_BEAR, world);
        this.forceDie();
    }

    public SpiritBear(Location spawnLoc, Faction faction) {
        super(EntityTypes.POLAR_BEAR, ((CraftWorld) spawnLoc.getWorld()).getHandle());
        this.setSpawn(spawnLoc);
        this.setFaction(faction);
        this.persistent = true;
        this.canPickUpLoot = false;
        this.setHealth(maxHp);
        this.Q = 1.5F; // TODO: Update name on version change (E: Entity.stepHeight)
        this.retargetTime = FactionMobs.random.nextInt(40);

        if (ReflectionManager.good_PathfinderGoalSelector_GoalSet) {
            try {
                @SuppressWarnings("rawtypes")
                Set tempSet1 = (Set) ReflectionManager.pathfinderGoalSelector_GoalSet.get(this.goalSelector);
                tempSet1.clear();
                @SuppressWarnings("rawtypes")
                Set tempSet2 = (Set) ReflectionManager.pathfinderGoalSelector_GoalSet.get(this.targetSelector);
                tempSet2.clear();
            } catch (Exception e) {
                ErrorManager.handleError(e);
            }
        }

        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.goalSelector.a(3, new PathfinderGoalMoveTowardsTarget(this, 1.0, (float) range));
        this.goalSelector.a(4, new PathfinderGoalFmobCommand(this));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.getBukkitEntity().setMetadata("CustomEntity", new FixedMetadataValue(FactionMobs.instance, true));
        this.getBukkitEntity().setMetadata("FactionMob", new FixedMetadataValue(FactionMobs.instance, true));
        faction.processMob(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(FactionMobs.mobNavRange);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(FactionMobs.mobSpeed);
        getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(maxHp);
        if (damage > 0) getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
    }

    @Override
    public void movementTick() { //TODO: Update name on version change (E: EntityLiving.onLivingUpdate)
        super.movementTick();
        if (--retargetTime < 0) {
            retargetTime = FactionMobs.responseTime;
            if (this.getGoalTarget() == null || !this.getGoalTarget().isAlive()) {
                this.findTarget();
            } else {
                double dist = Utils.dist3D(this.locX, this.getGoalTarget().locX, this.locY, this
                        .getGoalTarget().locY, this.locZ, this.getGoalTarget().locZ);
                if (dist > range) {
                    this.findTarget();
                } else if (dist > 4) {
                    this.findCloserTarget();
                }
            }
            if (this.getGoalTarget() == null) {
                if (this.command == Command.home) {
                    this.getNavigation()
                            .a(p.set(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ()), 1.0);
                } else if (this.command == Command.poi) {
                    this.getNavigation().a(p.set(this.poiX, this.poiY, this.poiZ), 1.0);
                } else if (this.command == Command.wander) {
                    // intentionally empty
                } else if (this.command == Command.phome) {
                    this.getNavigation().a(p.set(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc
                            .getZ()), FactionMobs.mobPatrolSpeed);
                    if (Utils.dist3D(this.locX, this.spawnLoc.getX(), this.locY, this.spawnLoc
                            .getY(), this.locZ, this.spawnLoc.getZ()) < 1) {
                        this.command = Command.ppoi;
                    }
                } else if (this.command == Command.ppoi) {
                    this.getNavigation().a(p.set(poiX, poiY, poiZ), FactionMobs.mobPatrolSpeed);
                    if (Utils.dist3D(this.locX, this.poiX, this.locY, this.poiY, this.locZ, this.poiZ) < 1) {
                        this.command = Command.phome;
                    }
                } else if (this.command == Command.path) {
                    this.getNavigation().a(p.set(poiX, poiY, poiZ), 1.0);
                    if (Utils.dist3D(this.locX, this.poiX, this.locY, this.poiY, this.locZ, this.poiZ) < 1) {
                        this.command = Command.home;
                    }
                }
            }
        }
    }

    private void setSpawn(Location loc) {
        spawnLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setPoi(loc.getX(), loc.getY(), loc.getZ());
        this.command = Command.home;
    }

    @Override
    public EntityLiving findCloserTarget() {
        if (this.attackedBy != null) {
            if (this.attackedBy.isAlive()
                    && this.attackedBy.world.getWorldData().getName().equals(this.world.getWorldData().getName())
                    && Utils.FactionCheck(this.attackedBy, this.faction, this.attackAll) < 1) {
                double dist = Utils
                        .dist3D(this.locX, this.attackedBy.locX, this.locY, this.attackedBy.locY, this.locZ, this.attackedBy.locZ);
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
        if (e != null) {
            this.setTarget(e);
        }
        return e;
    }

    @Override
    public String getLocalizedName() {
        return localizedName;
    }

    @Override
    public void findTarget() {
        this.setTarget(Utils.optimizedTargetSearch(this, range));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float i) {
        boolean out = super.damageEntity(damagesource, i);
        if (!out) {
            return false;
        }
        EntityLiving damager;
        if (damagesource.getEntity() instanceof EntityLiving) {
            damager = (EntityLiving) damagesource.getEntity();
        } else if (damagesource.getEntity() instanceof EntityProjectile) {
            damager = ((EntityProjectile) damagesource.getEntity()).getShooter();
        } else {
            return true;
        }
        switch (Utils.FactionCheck(damager, this.faction, this.attackAll)) {
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
        return true;
    }

    @Override
    public boolean a(IWorldReader iworldreader) { //TODO: Update name on version change (E: EntityInsentient.canSpawn)
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
        this.getBukkitEntity().setMetadata("faction", new FixedMetadataValue(FactionMobs.instance, this.factionName));
        this.updateNameTag();
    }

    @Override
    public void setTarget(EntityLiving entity) {
        this.target = entity;
        if (entity != null) {
            this.setGoalTarget(entity);
        } else {
            this.setGoalTarget(null);
        }
        if (this.getGoalTarget() != null && !this.getGoalTarget().isAlive()) {
            this.setGoalTarget(null);
        }
    }

    @Override
    public boolean setGoalTarget(EntityLiving entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (this.target != null && this.target.isAlive()) {
            super.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        } else {
            super.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, false);
        }
        return true;
    }

    @Override
    public void updateMob() {
        this.setFaction(this.faction);
        if (this.faction == null || this.faction.isNone()) {
            this.setFaction(FactionsManager.getFactionByName(factionName));
            if (this.faction == null || this.faction.isNone()) {
                this.forceDie();
                return;
            }
        }
        if (this.target != null && this.target.isAlive()) {
            this.setGoalTarget(this.target);
        } else {
            this.findTarget();
        }
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
    public boolean isTypeNotPersistent(double d) {
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
    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public void setPoi(double x, double y, double z) {
        this.poiX = x;
        this.poiY = y;
        this.poiZ = z;
    }

    @Override
    public Command getCommand() {
        return this.command;
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
            this.setSlot(EnumItemSlot.CHEST, ItemStack.a);
            this.setSlot(EnumItemSlot.FEET, ItemStack.a);
            this.setSlot(EnumItemSlot.HEAD, ItemStack.a);
            this.setSlot(EnumItemSlot.LEGS, ItemStack.a);
            this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
            this.setSlot(EnumItemSlot.OFFHAND, ItemStack.a);
        }
    }

    @Override
    public void forceDie() {
        this.setHealth(0);
        this.die();
    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) { //TODO: Update name on version change (E: Entity.writeToNBTAtomically)
        return false;
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) { //TODO: Update name on version change (E: Entity.writeToNBTOptional)
        return false;
    }

    @Override
    public void f(NBTTagCompound nbttagcompound) { //TODO: Update name on version change (E: Entity.readFromNBT)
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
    public Material getDrops() {
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
        this.datawatcher.set(HEALTH, MathHelper.a(f, 0.0F, maxHp));
    }

    @Override
    public void tick() {
        if (this.getHealth() > 0) {
            this.dead = false;
        }
        this.ai = false; //TODO: Update name on version change (E: Entity.inPortal)
        super.tick();
    }

    @Override
    public void setAttackAll(boolean value) {
        this.attackAll = value;
        this.updateNameTag();
    }

    @Override
    public boolean getAttackAll() {
        return this.attackAll;
    }

    @Override
    public void updateNameTag() {
        if (FactionMobs.displayMobFaction) {
            if (this.attackAll) {
                this.setCustomName(CraftChatMessage.fromStringOrNull(
                        Messages.get(Messages.Message.NAMETAG_RED, factionName, localizedName)));
            } else {
                this.setCustomName(CraftChatMessage
                        .fromStringOrNull(Messages.get(Messages.Message.NAMETAG, factionName, localizedName)));
            }
            this.setCustomNameVisible(true);
        } else {
            this.setCustomName(CraftChatMessage.fromStringOrNull(localizedName));
        }
        if (FactionMobs.disguiseEnabled) {
            com.gmail.scyntrus.fmob.DisguiseConnector.disguiseAsPolarBear(this);
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable var1) {
        return null;
    }
}
