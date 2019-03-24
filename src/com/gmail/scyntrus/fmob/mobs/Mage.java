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
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EntityPotion;
import net.minecraft.server.v1_13_R2.EntityProjectile;
import net.minecraft.server.v1_13_R2.EntityWitch;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.EnumMonsterType;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.IRegistry;
import net.minecraft.server.v1_13_R2.IWorldReader;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.Items;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.MobEffects;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_13_R2.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_13_R2.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_13_R2.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_13_R2.PotionRegistry;
import net.minecraft.server.v1_13_R2.PotionUtil;
import net.minecraft.server.v1_13_R2.Potions;
import net.minecraft.server.v1_13_R2.SoundCategory;
import net.minecraft.server.v1_13_R2.SoundEffects;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Set;

public class Mage extends EntityWitch implements FactionMob {

    public static final String typeName = "Mage";
    public static String localizedName = typeName;

    private static final PathHelpEntity p = new PathHelpEntity();
    private static final PotionRegistry HARMING_POTION = IRegistry.POTION.get(new MinecraftKey("harming"));
    private static final PotionRegistry SLOWNESS_POTION = IRegistry.POTION.get(new MinecraftKey("slowness"));
    private static final PotionRegistry POISON_POTION = IRegistry.POTION.get(new MinecraftKey("poison"));
    private static final PotionRegistry WEAKNESS_POTION = IRegistry.POTION.get(new MinecraftKey("weakness"));
    private static final PotionRegistry HEALING_POTION = IRegistry.POTION.get(new MinecraftKey("healing"));

    public static final double range = 16;
    @Option(key = "Mage.maxHp", min = 1)
    public static float maxHp = 20;
    @Option(key = "Mage.enabled")
    public static Boolean enabled = true;
    @Option(key = "Mage.powerCost", min = 0)
    public static double powerCost = 0;
    @Option(key = "Mage.moneyCost", min = 0)
    public static double moneyCost = 0;
    @Option(key = "Mage.drops")
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

    public Mage(World world) {
        super(world);
        this.forceDie();
    }

    public Mage(Location spawnLoc, Faction faction) {
        super(((CraftWorld) spawnLoc.getWorld()).getHandle());
        this.setSpawn(spawnLoc);
        this.setFaction(faction);
        this.persistent = true;
        this.fireProof = false;
        this.canPickUpLoot = false;
        this.setHealth(maxHp);
        this.Q = 1.5F; // TODO: Update name on version change (E: Entity.stepHeight)
        this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), HARMING_POTION));
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
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 12.0F));
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
        getAttributeInstance(GenericAttributes.maxHealth).setValue(maxHp);
    }

    @Override
    public void k() { //TODO: Update name on version change (E: EntityLiving.onLivingUpdate)
        super.k();
        if (--retargetTime < 0) {
            retargetTime = FactionMobs.responseTime;
            if (this.getGoalTarget() == null || !this.getGoalTarget().isAlive()) {
                this.findTarget();
            } else {
                double dist = Utils.dist3D(this.locX, this.getGoalTarget().locX, this.locY, this.getGoalTarget().locY, this.locZ, this.getGoalTarget().locZ);
                if (dist > range) {
                    this.findTarget();
                } else if (dist > 4) {
                    this.findCloserTarget();
                }
            }
            if (this.getGoalTarget() == null) {
                if (this.command == Command.home) {
                    this.getNavigation().a(p.set(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ()), 1.0);
                } else if (this.command == Command.poi) {
                    this.getNavigation().a(p.set(this.poiX, this.poiY, this.poiZ), 1.0);
                } else if (this.command == Command.wander) {
                    // intentionally empty
                } else if (this.command == Command.phome) {
                    this.getNavigation().a(p.set(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ()), FactionMobs.mobPatrolSpeed);
                    if (Utils.dist3D(this.locX, this.spawnLoc.getX(), this.locY, this.spawnLoc.getY(), this.locZ, this.spawnLoc.getZ()) < 1) {
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
        this.an = false; //TODO: Update name on version change (E: Entity.inPortal)
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
                this.setCustomName(CraftChatMessage.fromStringOrNull(Messages.get(Messages.Message.NAMETAG, factionName, localizedName)));
            }
            this.setCustomNameVisible(true);
        } else {
            this.setCustomName(CraftChatMessage.fromStringOrNull(localizedName));
        }
        if (FactionMobs.disguiseEnabled) {
            com.gmail.scyntrus.fmob.DisguiseConnector.disguiseAsPlayer(this);
        }
    }

    @Override
    public void a(EntityLiving paramEntityLiving, float paramFloat) {  //TODO: Update name on version change (E: EntityWitch.attackEntityWithRangedAttack)
        if (l()) { //TODO: Update name on version change (E: EntityWitch.isDrinkingPotion)
            return;
        }

        double d1 = paramEntityLiving.locY + paramEntityLiving.getHeadHeight() - 1.1D;
        double d2 = paramEntityLiving.locX + paramEntityLiving.motX - this.locX;
        double d3 = d1 - this.locY;
        double d4 = paramEntityLiving.locZ + paramEntityLiving.motZ - this.locZ;
        float f = MathHelper.sqrt(d2 * d2 + d4 * d4);

        PotionRegistry localPotionRegistry = HARMING_POTION;
        if ((f >= 8.0F) && (!paramEntityLiving.hasEffect(MobEffects.SLOWER_MOVEMENT))) {
            localPotionRegistry = SLOWNESS_POTION;
        } else if ((paramEntityLiving.getHealth() >= 8.0F) && (!paramEntityLiving.hasEffect(MobEffects.POISON))
                && (paramEntityLiving.getMonsterType() != EnumMonsterType.UNDEAD)
                && (paramEntityLiving.getMonsterType() != EnumMonsterType.ARTHROPOD)) {
            localPotionRegistry = POISON_POTION;
        } else if ((f <= 3.0F) && (!paramEntityLiving.hasEffect(MobEffects.WEAKNESS)) && (this.random.nextFloat() < 0.25F)) {
            localPotionRegistry = WEAKNESS_POTION;
        } else if (paramEntityLiving.getMonsterType() == EnumMonsterType.UNDEAD) {
            localPotionRegistry = HEALING_POTION;
        }

        this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a( //TODO: Update name on version change (E: addPotionToItemStack)
                new ItemStack(Items.SPLASH_POTION), localPotionRegistry));

        EntityPotion localEntityPotion = new EntityPotion(this.world, this, PotionUtil.a( //TODO: Update name on version change (E: addPotionToItemStack)
                new ItemStack(Items.SPLASH_POTION), localPotionRegistry));
        localEntityPotion.pitch -= -20.0F;
        localEntityPotion.shoot(d2, d3 + f * 0.2F, d4, 0.75F, 8.0F);
        this.world.a( // TODO: Update name on version change (E: World.playSound)
                null, this.locX, this.locY, this.locZ,
                SoundEffects.ENTITY_WITCH_THROW,
                SoundCategory.HOSTILE,
                1.0F, 0.8F + this.random.nextFloat() * 0.4F);

        this.world.addEntity(localEntityPotion);
    }
}
