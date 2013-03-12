package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityCreature;
import net.minecraft.server.v1_4_R1.EntityLiving;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftMonster;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
			e.getPlayer().sendMessage(ChatColor.GREEN + "This " + ChatColor.RED + fmob.getTypeName() + ChatColor.GREEN + " belongs to faction " + ChatColor.RED + fmob.getFaction().getTag());
			e.getPlayer().sendMessage("HP: " + fmob.getHealth());
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		plugin.updateList();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if ((((CraftEntity) e.getDamager()).getHandle() instanceof FactionMob) 
				&& (e.getEntity() instanceof Monster) 
				&& !(((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob)) {
			((CraftMonster) e.getEntity()).getHandle().setGoalTarget(((CraftLivingEntity) e.getDamager()).getHandle());
			((CraftMonster) e.getEntity()).getHandle().setTarget(((CraftLivingEntity) e.getDamager()).getHandle());
			return;
		}
		if (e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();
			if (arrow.getShooter() == null) {
				return;
			}
			if ((((CraftLivingEntity) arrow.getShooter()).getHandle() instanceof FactionMob) 
					&& (e.getEntity() instanceof Monster) 
					&& !(((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob)) {
				((CraftMonster) e.getEntity()).getHandle().setGoalTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
				((CraftMonster) e.getEntity()).getHandle().setTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		EntityDamageEvent lastDamage = e.getEntity().getLastDamageCause();
		if (lastDamage instanceof EntityDamageByEntityEvent) {
			org.bukkit.entity.Entity entity = ((EntityDamageByEntityEvent) lastDamage).getDamager();
			if (entity != null) {
				if (((CraftEntity) entity).getHandle() instanceof FactionMob) {
					FactionMob fmob = (FactionMob) ((CraftEntity) entity).getHandle();
					e.setDeathMessage(e.getEntity().getDisplayName() + " was killed by " + ChatColor.RED + fmob.getFaction().getTag() + ChatColor.RESET + "'s " + ChatColor.RED + fmob.getTypeName());
				} else if (entity instanceof Arrow){
					Arrow arrow = (Arrow) entity;
					if (((CraftLivingEntity) arrow.getShooter()).getHandle() instanceof FactionMob) {
						FactionMob fmob = (FactionMob) ((CraftLivingEntity) arrow.getShooter()).getHandle();
						e.setDeathMessage(e.getEntity().getDisplayName() + " was shot by " + ChatColor.RED + fmob.getFaction().getTag() + ChatColor.RESET + "'s " + ChatColor.RED + fmob.getTypeName());
					}
				}
			}
		}
	}
}
