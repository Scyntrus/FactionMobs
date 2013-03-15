package com.gmail.scyntrus.fmob.mobs;

import java.lang.reflect.Field;

import net.minecraft.server.v1_4_R1.DamageSource;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityIronGolem;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.Navigation;
import net.minecraft.server.v1_4_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.Utils;
import com.massivecraft.factions.Faction;

public class Titan extends EntityIronGolem implements FactionMob {
	
	public Location spawnLoc = null;
	public Faction faction = null;
	public Entity attackedBy = null;
	public static String typeName = "Titan";
	public static int maxHp = 40;
	public static Boolean enabled = true;
	public static double powerCost = 0;
	public static double moneyCost = 1;
	public static double range = 12;
	
	public double poiX=0, poiY=0, poiZ=0;
	public String order = "poi";
	
	public Titan(World world) {
		super(world);
	    this.persistent = true;
	    this.fireProof = false;
	    this.canPickUpLoot = false;
	    this.bH = FactionMobs.mobSpeed;
	    this.getNavigation().b(false);
	    this.getNavigation().e(true);
	    try {
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			field.setFloat(this.getNavigation(), FactionMobs.mobNavRange);
		} catch (Exception e) {
		}
	}

	@Override
	public void c() {
		int tmpFire = this.fireTicks;
		super.c();
		this.fireTicks = tmpFire;
		if (this.getGoalTarget() == null) {
			this.findTarget();
		}
		if (this.getGoalTarget() == null) {
			if (this.order == null || this.order.equals("") || this.order.equals("home")) {
				this.getNavigation().a(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), this.bH);
				this.order = "home";
				return;
			} else if (this.order.equals("poi")) {
				this.getNavigation().a(this.poiX, this.poiY, this.poiZ, this.bH);
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
		return;
	}
	
	@Override
	public void setSpawn(Location loc) {
		spawnLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
	}
	
	@Override
	public Entity findTarget() {
		Entity found = super.findTarget();
		if (found != null) {
			switch (Utils.FactionCheck(found, this.faction)) {
			case -1:
				this.setTarget(found);
				return found;
			case 0:
				if (attackedBy != null && found.equals(attackedBy)) {
					this.setTarget(found);
					return found;
				}
			case 1:
				this.setTarget(null);
				found = null;
				break;
			}
		}
		double dist = range;
		Location thisLoc;
		double thisDist;
		for (org.bukkit.entity.Entity e : this.getBukkitEntity().getNearbyEntities(range, range, range)) {
			if (!e.isDead() && Utils.FactionCheck(((CraftEntity) e).getHandle(), faction) == -1) {
				thisLoc = e.getLocation();
				thisDist = Math.sqrt(Math.pow(this.locX-thisLoc.getX(),2) + Math.pow(this.locY-thisLoc.getY(),2) + Math.pow(this.locZ-thisLoc.getZ(),2));
				if (thisDist < dist) {
					found = ((CraftEntity) e).getHandle();
					dist = thisDist;
					if (dist < 1.5) {
						this.setTarget(found);
						return found;
					}
				}
			} else if (!e.isDead() && (Utils.FactionCheck(((CraftEntity) e).getHandle(), faction) == 0) && 
					((CraftEntity) e).getHandle().equals(attackedBy)) {
				found = ((CraftEntity) e).getHandle();
				this.setTarget(found);
				return found;
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
		return faction;
	}

	@Override
	public void setFaction(Faction faction) {
		this.faction = faction;
		
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
}
