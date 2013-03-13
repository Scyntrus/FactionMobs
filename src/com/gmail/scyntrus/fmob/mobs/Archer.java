package com.gmail.scyntrus.fmob.mobs;

import net.minecraft.server.v1_4_R1.DamageSource;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntitySkeleton;
import net.minecraft.server.v1_4_R1.Item;
import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import com.gmail.scyntrus.fmob.Utils;
import com.massivecraft.factions.Faction;

public class Archer extends EntitySkeleton implements FactionMob{
	
	public Location spawnLoc = null;
	public Faction faction = null;
	public Entity attackedBy = null;
	public static String typeName = "Archer";
	public static int maxHp = 20;
	public static Boolean enabled = true;
	public static double powerCost = 0;
	public static double moneyCost = 0;
	public static double range = 10;
	
	public Archer(World world) {
		super(world);
	    this.setEquipment(0, new ItemStack(Item.BOW));
	    this.persistent = true;
	}

	@Override
	public void c() {
		int tmpFire = this.fireTicks;
		super.c();
		this.fireTicks = tmpFire;
		this.motX = this.motZ = 0;
		this.setPosition(spawnLoc.getX(), this.locY, spawnLoc.getZ());
		if (this.getGoalTarget() == null) {
			this.findTarget();
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
		double dist = 1000;
		Location thisLoc;
		double thisDist;
		for (org.bukkit.entity.Entity e : this.getBukkitEntity().getNearbyEntities(range, range, range)) {
			if (!e.isDead() && Utils.FactionCheck(((CraftEntity) e).getHandle(), faction) == -1) {
				thisLoc = e.getLocation();
				thisDist = Math.hypot(Math.hypot(this.locX - thisLoc.getX(), this.locY - thisLoc.getY()), this.locZ - thisLoc.getZ());
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
	protected String aY() {
	    return FactionMobs.sndBreath;
	}

	@Override
	protected String aZ() {
	    return FactionMobs.sndHurt;
	}

	@Override
	protected String ba() {
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
}
