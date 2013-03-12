package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityCreature;
import net.minecraft.server.v1_4_R1.EntityLiving;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityListener implements Listener{
	
	FactionMobs plugin;
	
	public EntityListener(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		Entity entity = ((CraftEntity) e.getEntity()).getHandle();
		if (entity != null && entity instanceof FactionMob) {
			FactionMob fmob = (FactionMob) entity;
			if (e.getTarget() != null) {
				Entity target = ((CraftEntity) e.getTarget()).getHandle();
				if (Utils.FactionCheck(target, ((FactionMob) entity).getFaction()) == -1) {
					((EntityLiving) entity).setGoalTarget((EntityLiving) target);
					((EntityCreature) entity).setTarget(target);
					e.setCancelled(true);
					return;
				}
			}
			Entity target = fmob.findTarget();
			if (target != null) {
				((EntityLiving) entity).setGoalTarget((EntityLiving) target);
				((EntityCreature) entity).setTarget(target);
				e.setCancelled(true);
				return;
			} else {
				((EntityLiving) entity).setGoalTarget(null);
				((EntityCreature) entity).setTarget(null);
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (((CraftEntity)e.getRightClicked()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity)e.getRightClicked()).getHandle();
			e.getPlayer().sendMessage("This " + fmob.getTypeName() + " belongs to faction " + fmob.getFaction().getTag());
			e.getPlayer().sendMessage("HP: " + fmob.getHealth());
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		plugin.updateList();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
//		if (((CraftEntity) e.Entity()).getHandle() instanceof FactionMob) {
//			Utils.giveColorArmor((FactionMob) ((CraftEntity) e.getEntity()).getHandle(), plugin);
//		}
		if ((((CraftEntity) e.getDamager()).getHandle() instanceof FactionMob) 
				&& (e.getEntity() instanceof Monster) 
				&& !(((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob)) {
			((Monster) e.getEntity()).setTarget((LivingEntity) e.getDamager());
		}
	}
}
