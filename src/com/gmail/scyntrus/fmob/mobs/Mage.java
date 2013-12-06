package com.gmail.scyntrus.fmob.mobs;

import net.minecraft.server.v1_7_R1.AttributeInstance;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.DamageSource;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityCreature;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityProjectile;
import net.minecraft.server.v1_7_R1.EntityWitch;
import net.minecraft.server.v1_7_R1.EnumMonsterType;
import net.minecraft.server.v1_7_R1.GenericAttributes;
import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.PathfinderGoal;
import net.minecraft.server.v1_7_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_7_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.util.UnsafeList;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.ReflectionManager;
import com.gmail.scyntrus.fmob.Utils;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.Factions;

public class Mage extends EntityWitch implements FactionMob {
	
	public Location spawnLoc = null;
	public Faction faction = null;
	public String factionName = "";
	public Entity attackedBy = null;
	public static String typeName = "Mage";
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
		this.die();
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
	    getAttributeInstance(GenericAttributes.d).setValue(this.moveSpeed);
	    getAttributeInstance(GenericAttributes.a).setValue(maxHp);
	    this.setHealth(maxHp);
	    this.X = 1.5F;
	    this.getNavigation().a(false);
	    this.getNavigation().b(false);
	    this.getNavigation().c(true);
	    this.getNavigation().d(false);
	    this.getNavigation().e(true);
	    this.setHealth(maxHp);
		this.setEquipment(0, new ItemStack((Item)Item.REGISTRY.a("potion"), 1, 8204));
	    
	    if (ReflectionManager.goodNavigationE) {
		    try {
				AttributeInstance e = (AttributeInstance) ReflectionManager.navigationE.get(this.getNavigation());
				e.setValue(FactionMobs.mobNavRange);
			} catch (Exception e) {}
	    }
	    if (ReflectionManager.goodPathfinderGoalSelectorA) {
		    try {
		    	ReflectionManager.pathfinderGoalSelectorA.set(this.goalSelector, new UnsafeList<PathfinderGoal>());
		    	ReflectionManager.pathfinderGoalSelectorA.set(this.targetSelector, new UnsafeList<PathfinderGoal>());
		    } catch (Exception e) {}
	    }
	    
	    this.goalSelector.a(1, new PathfinderGoalFloat(this));
	    this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 10.0F));
	    this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0));
	    this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
	    this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
	    this.getBukkitEntity().setMetadata("CustomEntity", new FixedMetadataValue(FactionMobs.instance, true));
	}

	@Override
	public void e() {
		super.e();
		if (--retargetTime < 0) {
			retargetTime = 20;
			if (this.getGoalTarget() == null || !this.getGoalTarget().isAlive()) {
				this.findTarget();
			} else {
				double dist = Utils.dist3D(this.locX, this.getGoalTarget().locX, this.locY, this.getGoalTarget().locY, this.locZ, this.getGoalTarget().locZ);
				if (dist > range) {
					this.findTarget();
				} else if (dist > 1.5) {
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
	
	public Entity findCloserTarget() {
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
		Location thisLoc;
		double thisDist;
		for (org.bukkit.entity.Entity e : this.getBukkitEntity().getNearbyEntities(1.5, 1.5, 1.5)) {
			if (!e.isDead() && e instanceof CraftLivingEntity && Utils.FactionCheck(((CraftEntity) e).getHandle(), faction) == -1) {
				thisLoc = e.getLocation();
				thisDist = Math.sqrt(Math.pow(this.locX-thisLoc.getX(),2) + Math.pow(this.locY-thisLoc.getY(),2) + Math.pow(this.locZ-thisLoc.getZ(),2));
				if (thisDist < 1.5) {
					if (((CraftLivingEntity) this.getBukkitEntity()).hasLineOfSight(e)) {
						this.setTarget(((CraftEntity) e).getHandle());
						return ((CraftEntity) e).getHandle();
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public Entity findTargetAlias() {
		return findTarget();
	}
	
	@Override
	public Entity findTarget() {
		Entity found = this.findCloserTarget();
		if (found != null) {
			return found;
		}
		double dist = range;
		Location thisLoc;
		double thisDist;
		for (org.bukkit.entity.Entity e : this.getBukkitEntity().getNearbyEntities(range, range, range)) {
			if (!e.isDead() && e instanceof CraftLivingEntity && Utils.FactionCheck(((CraftEntity) e).getHandle(), faction) == -1) {
				thisLoc = e.getLocation();
				thisDist = Math.sqrt(Math.pow(this.locX-thisLoc.getX(),2) + Math.pow(this.locY-thisLoc.getY(),2) + Math.pow(this.locZ-thisLoc.getZ(),2));
				if (thisDist < dist) {
					if (((CraftLivingEntity) this.getBukkitEntity()).hasLineOfSight(e)) {
						found = ((CraftEntity) e).getHandle();
						dist = thisDist;
					}
				}
			}
		}
		this.setTarget(found);
		return found;
	}
	
	@Override
	public boolean damageEntity(DamageSource damagesource, float i) {
		boolean out = super.damageEntity(damagesource, i);
		if (out) {
			switch (Utils.FactionCheck(damagesource.getEntity(), this.faction)) {
			case 1:
				this.findTarget();
				if (damagesource.getEntity() instanceof EntityPlayer) {
					this.lastDamageByPlayerTime = 0;
				}
				break;
			case 0:
			case -1:
				if (damagesource.getEntity() instanceof EntityLiving) {
					this.attackedBy = damagesource.getEntity();
					this.setTarget(damagesource.getEntity());
				} else if (damagesource.getEntity() instanceof EntityProjectile) {
					EntityProjectile p = (EntityProjectile) damagesource.getEntity();
					this.attackedBy = p.getShooter();
					this.setTarget(p.getShooter());
				} else {
					this.findTarget();
				}
				break;
			}
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
			this.setFaction(Factions.getFactionByName(this.world.getWorldData().getName(),factionName));
		}
		if (this.faction == null) {
			this.die();
			System.out.println("[Error] Found and removed factionless faction mob");
		}
		return this.faction;
	}

	public void setFaction(Faction faction) {
		if (faction == null) return;
		this.faction = faction;
		this.factionName = new String(faction.getName());
		if (faction.isNone()) die();
		if (FactionMobs.displayMobFaction) {
			this.setCustomName(ChatColor.YELLOW + this.factionName + " " + typeName);
			this.setCustomNameVisible(true);
		}
	}
	
	@Override
	public void setTarget(Entity entity) {
		this.target = entity;
		if (entity instanceof EntityLiving) {
			this.setGoalTarget((EntityLiving) entity);
		} else if (entity == null) {
			this.setGoalTarget(null);
		}
		if (this.getGoalTarget() != null && !this.getGoalTarget().isAlive()) {
			this.setGoalTarget(null);
		}
	}
	
	@Override
	public void setGoalTarget(EntityLiving target) {
		if (this.target instanceof EntityLiving && this.target.isAlive()) {
			super.setGoalTarget((EntityLiving) this.target);
		} else {
			super.setGoalTarget(null);
		}
	}
	
	@Override
	public void updateMob() {
		if (this.target instanceof EntityLiving && this.target.isAlive()) {
			super.setGoalTarget((EntityLiving) this.target);
		} else {
			this.findTarget();
		}
		this.setFaction(Factions.getFactionByName(this.world.getWorldData().getName(),factionName));
		if (this.faction == null) {
			this.die();
			return;
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
	protected String t() {
	    return FactionMobs.sndBreath;
	}

	@Override
	protected String aT() {
	    return FactionMobs.sndHurt;
	}

	@Override
	protected String aU() {
	    return FactionMobs.sndDeath;
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
	    makeSound(FactionMobs.sndStep, 0.15F, 1.0F);
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
		super.die();
		this.setHealth(0);
		this.setEquipment(0, null);
		this.setEquipment(1, null);
		this.setEquipment(2, null);
		this.setEquipment(3, null);
		this.setEquipment(4, null);
		if (FactionMobs.mobList.contains(this)) {
			FactionMobs.mobList.remove(this);
		}
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
	public boolean softAgro(Entity entity) {
		if (this.attackedBy == null 
				&& entity instanceof EntityLiving
				&& entity.isAlive()) {
			this.attackedBy = entity;
			this.setTarget(entity);
			return true;
		}
		return false;
	}
	
	@Override
	public void setHealth(float f) {
		this.datawatcher.watch(6, Float.valueOf(MathHelper.a(f, 0.0F, maxHp)));
	}
}
