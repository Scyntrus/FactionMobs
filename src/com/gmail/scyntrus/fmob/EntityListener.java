package com.gmail.scyntrus.fmob;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityCreature;
import net.minecraft.server.v1_4_R1.EntityLiving;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
		if (e.getRightClicked() instanceof Creature) {
			Entity t = ((CraftCreature)e.getRightClicked()).getHandle().getGoalTarget();
			if (t != null) {
				e.getPlayer().sendMessage("Currently targeting " + t.getLocalizedName());
			} else {
				e.getPlayer().sendMessage("Not targeting anything");
			}
			
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		for (FactionMob fmob : plugin.mobList) {
			try {
				if (fmob.isAlive()) {
					fmob.updateMob();
				} else {
					plugin.mobList.remove(fmob);
				}
			} catch (Exception ex) {
				
			}
		}
	}
}
