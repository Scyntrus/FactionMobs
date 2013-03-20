package com.gmail.scyntrus.fmob.mobs;

import java.lang.reflect.Field;

import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityLiving;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.EntitySkeleton;
import net.minecraft.server.v1_5_R1.Item;
import net.minecraft.server.v1_5_R1.ItemStack;
import net.minecraft.server.v1_5_R1.Navigation;
import net.minecraft.server.v1_5_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftLivingEntity;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.Utils;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class Archer extends EntitySkeleton implements FactionMob {
	
	public Location spawnLoc = null;
	public Faction faction = null;
	public String factionName = null;
	public Entity attackedBy = null;
	public static String typeName = "Archer";
	public static int maxHp = 20;
	public static Boolean enabled = true;
	public static double powerCost = 0;
	public static double moneyCost = 0;
	public static double range = 16;
	public static int damage = 0;
	private int retargetTime = 0;
	
	public double poiX=0, poiY=0, poiZ=0;
	public String order = "poi";
	
	public Archer(World world) {
		super(world);
	    this.persistent = true;
	    this.fireProof = false;
	    this.canPickUpLoot = false;
	    this.bI = FactionMobs.mobSpeed;
	    this.getNavigation().a(false); // avoid water
	    this.getNavigation().b(false); // break door
	    this.getNavigation().c(true); // enter open door
	    this.getNavigation().d(false); // avoid sunlight
	    this.getNavigation().e(true); // swim
	    try {
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			field.setFloat(this.getNavigation(), FactionMobs.mobNavRange);
		} catch (Exception e) {
		}
	    this.setEquipment(0, new ItemStack(Item.BOW));
	}
	
	@Override
	public void c() {
		int tmpFire = this.fireTicks;
		double tmpMotY = this.motY;
		super.c();
		if (this.motY>tmpMotY) {
			this.motY += .01;
			if (this.ae) {
				this.motY += .01;
			}
		}
		this.fireTicks = tmpFire;
		if (this.getEquipment(4) != null) {
			this.getEquipment(4).setData(0);
		}
		if (--retargetTime < 0) {
			retargetTime = 10;
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
				if (this.order == null || this.order.equals("") || this.order.equals("home")) {
					this.getNavigation().a(this.spawnLoc.getX(), this.spawnLoc.getY(), this.spawnLoc.getZ(), FactionMobs.mobSpeed);
					this.order = "home";
					return;
				} else if (this.order.equals("poi")) {
					this.getNavigation().a(this.poiX, this.poiY, this.poiZ, FactionMobs.mobSpeed);
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
				}
			}
		}
		return;
	}
	
	@Override
	public void setSpawn(Location loc) {
		spawnLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public Entity findCloserTarget() {
		if (this.attackedBy != null
				&& this.attackedBy.isAlive()
				&& Utils.dist3D(this.locX, this.attackedBy.locX, this.locY, this.attackedBy.locY, this.locZ, this.attackedBy.locZ) < 16) {
			this.setTarget(this.attackedBy);
			return this.attackedBy;
		}
		Location thisLoc;
		double thisDist;
		for (org.bukkit.entity.Entity e : this.getBukkitEntity().getNearbyEntities(2, 2, 2)) {
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
	public boolean damageEntity(DamageSource damagesource, int i) {
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
				this.attackedBy = damagesource.getEntity();
				if (damagesource.getEntity() instanceof EntityLiving) {
					this.setTarget(damagesource.getEntity());
				} else {
					this.findTarget();
				}
				break;
			}
		}
		return out;
	}
	
	@Override
	public int getMaxHealth() {
        return maxHp;
    }
	
	@Override
	public boolean canSpawn() {
		return true;
	}

	@Override
	public Faction getFaction() {
		if (this.faction == null) {
			this.faction = Factions.i.getByTag(this.getFactionName());
		}
		if (this.faction == null) {
			this.die();
			System.out.println("[Error] Found and removed factionless faction mob");
		}
		return this.faction;
	}

	@Override
	public void setFaction(Faction faction) {
		this.faction = faction;
		this.factionName = new String(faction.getTag());
	}
	
	@Override
	public void setTarget(Entity entity) {
		this.target = entity;
		if (entity instanceof EntityLiving && entity.isAlive()) {
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
		if (this.faction == null) {
			this.faction = Factions.i.getByTag(this.factionName);
		}
		if (this.faction == null) {
			this.health = 0;
			this.die();
			FactionMobs.mobList.remove(this);
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
	protected String bb() {
	    return FactionMobs.sndBreath;
	}

	@Override
	protected String bc() {
	    return FactionMobs.sndHurt;
	}

	@Override
	protected String bd() {
	    return FactionMobs.sndDeath;
	}

	@Override
	protected void a(int i, int j, int k, int l) {
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
	public EntityLiving getEntity() {
		return this;
	}
	
	@Override
	public String getFactionName() {
		return this.factionName;
	}
	
	@Override
	public void setFactionName(String str) {
		this.factionName = str;
	}
	
	@Override
	public void die() {
		super.die();
	}
	
	@Override
	public void a(EntityLiving entityliving, float f) {
		if (damage>0) {
			super.a(entityliving, damage/2F);
		} else {
			super.a(entityliving, f);
		}
	}
}
