package com.gmail.scyntrus.fmob;

import java.util.ArrayList;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityWolf;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.scyntrus.fmob.mobs.Titan;
import com.massivecraft.factions.FPlayers;

public class EntityListener implements Listener {
	
	FactionMobs plugin;
	
	public EntityListener(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		Entity entity = ((CraftEntity) e.getEntity()).getHandle();
		if (entity != null && entity instanceof FactionMob) {
			e.setCancelled(true);
			FactionMob fmob = (FactionMob) entity;
			if (fmob instanceof Titan) {
				fmob.findTarget();
				return;
			}
			if (e.getTarget() != null) {
				Entity target = ((CraftEntity) e.getTarget()).getHandle();
				if (Utils.FactionCheck(target, fmob.getFaction()) == -1) {
					fmob.setTarget(target);
					return;
				}
			}
			fmob.findTarget();
			return;
		} else if (entity != null && entity instanceof EntityWolf) {
			if (e.getTarget() != null) {
				Entity target = ((CraftEntity) e.getTarget()).getHandle();
				if (target instanceof FactionMob) {
					EntityWolf wolf = (EntityWolf) entity;
					FactionMob fmob = (FactionMob) target;
					if (wolf.isAngry()) {
						return;
					} else if (wolf.isTamed()) {
						if (wolf.getOwner() != null) {
							if (fmob.getGoalTarget().equals(wolf.getOwner())) {
								return;
							}
							switch (Utils.FactionCheck(wolf.getOwner(), fmob.getFaction())) {
							case 1:
							case 0:
								e.setCancelled(true);
								return;
							case -1:
								return;
							}
						} else {
							e.setCancelled(true);
							return;
						}
					}
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (((CraftEntity)e.getRightClicked()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity)e.getRightClicked()).getHandle();
			if (fmob.getFaction() == null) {
				return;
			}
			Player player = e.getPlayer();
			player.sendMessage(String.format("%sThis %s%s %sbelongs to faction %s%s%s. HP: %s%s", 
					ChatColor.GREEN, ChatColor.RED, fmob.getTypeName(), ChatColor.GREEN, ChatColor.RED, 
					fmob.getFactionName(), ChatColor.GREEN, ChatColor.RED, fmob.getHealth()));
			if (player.hasPermission("fmob.order") && FPlayers.i.get(player).getFaction().equals(fmob.getFaction())) {
				if (!plugin.playerSelections.containsKey(player.getName())) {
					plugin.playerSelections.put(player.getName(), new ArrayList<FactionMob>());
				}
				if (plugin.playerSelections.get(player.getName()).contains(fmob)) {
					plugin.playerSelections.get(player.getName()).remove(fmob);
					player.sendMessage(String.format("%sYou have deselected this %s%s", ChatColor.GREEN, ChatColor.RED, fmob.getTypeName()));
					if (plugin.playerSelections.get(player.getName()).isEmpty()) {
						plugin.playerSelections.remove(player.getName());
						player.sendMessage(String.format("%sYou have no mobs selected", ChatColor.GREEN));
					}
				} else {
					plugin.playerSelections.get(player.getName()).add(fmob);
					player.sendMessage(String.format("%sYou have selected this %s%s", ChatColor.GREEN, ChatColor.RED, fmob.getTypeName()));
					fmob.setPoi(fmob.getlocX(), fmob.getlocY(), fmob.getlocZ());
					fmob.setOrder("poi");
				}
			}
			fmob.updateMob();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob) {
			((CraftEntity) e.getEntity()).getHandle().die();
			e.getDrops().clear();
		}
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        	public void run() {
    			plugin.updateList();
        	}
        }, 1L);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof CraftLivingEntity)) return;
		CraftLivingEntity entity = (CraftLivingEntity) e.getEntity();
		CraftEntity damager = (CraftEntity) e.getDamager();
		if (damager instanceof Projectile) damager = (CraftEntity) ((Projectile) damager).getShooter();
		if (damager == null) return;
		
		if (damager.getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) damager.getHandle();
			if (Utils.FactionCheck(entity.getHandle(), fmob.getFaction()) < 1) {
				entity.getHandle().setGoalTarget(((CraftLivingEntity) damager).getHandle());
				if (fmob.isAlive()) {
					if (entity instanceof CraftCreature) {
						((CraftCreature) entity).getHandle().setTarget(((CraftLivingEntity) damager).getHandle());
					}
					return;
				}
			} else if (FactionMobs.noFriendlyFire) {
				e.setCancelled(true);
				return;
			}
		} else if ((damager instanceof Player)
				&& (entity.getHandle() instanceof FactionMob)) {
			FactionMob fmob = (FactionMob) entity.getHandle();
			Player player = (Player) damager;
			if (Utils.FactionCheck((Entity) fmob, FPlayers.i.get(player).getFaction()) >= 1) {
				if (fmob.getFaction().equals(FPlayers.i.get(player).getFaction())) {
					player.sendMessage(String.format("%sYou hit a friendly %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
					entity.setMetadata("NPC", new FixedMetadataValue(plugin, true));
					return;
				} else {
					player.sendMessage(String.format("%sYou cannot hit %s%s%s's %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getFactionName(), ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDamageByEntity2(EntityDamageByEntityEvent e) {
		if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob
				&& e.getEntity().hasMetadata("NPC")) {
			e.getEntity().removeMetadata("NPC", plugin);
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
					if (fmob.getFaction() == null) {
						return;
					}
					e.setDeathMessage(e.getEntity().getDisplayName() + " was killed by " + ChatColor.RED + fmob.getFactionName() + ChatColor.RESET + "'s " + ChatColor.RED + fmob.getTypeName());
				} else if (entity instanceof Projectile){
					Projectile arrow = (Projectile) entity;
					if (((CraftLivingEntity) arrow.getShooter()).getHandle() instanceof FactionMob) {
						FactionMob fmob = (FactionMob) ((CraftLivingEntity) arrow.getShooter()).getHandle();
						if (fmob.getFaction() == null) {
							return;
						}
						e.setDeathMessage(e.getEntity().getDisplayName() + " was shot by " + ChatColor.RED + fmob.getFactionName() + ChatColor.RESET + "'s " + ChatColor.RED + fmob.getTypeName());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (plugin.playerSelections.containsKey(player.getName())) {
			plugin.playerSelections.get(player.getName()).clear();
			plugin.playerSelections.remove(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (plugin.mobLeader.containsKey(e.getPlayer().getName()) && plugin.playerSelections.containsKey(e.getPlayer().getName())) {
			if (e.getFrom().distance(e.getTo()) < 0.00001) {
				return;
			}
			Player player = e.getPlayer();
			Location loc = player.getLocation();
			int count = 0;
			for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
				if (fmob.getSpawn().getWorld().getName().equals(loc.getWorld().getName())) {
					double tmpX = (1.5-(count%4))*1.5;
					double tmpZ = ((-1.) - Math.floor(count / 4.))*1.5;
					double tmpH = Math.hypot(tmpX, tmpZ);
					double angle = Math.atan2(tmpZ, tmpX) + (loc.getYaw() * Math.PI / 180.);
					fmob.setPoi(loc.getX() + tmpH*Math.cos(angle), loc.getY(), loc.getZ() + tmpH*Math.sin(angle));
					count += 1;
				}
			}
		}
	}
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (e.getPotion().getShooter() == null) return;
		if (((CraftEntity) e.getPotion().getShooter()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getPotion().getShooter()).getHandle();
			for (LivingEntity entity : e.getAffectedEntities()) {
				if (Utils.FactionCheck(((CraftEntity) entity).getHandle(), fmob.getFaction()) < 1) {
					if (fmob.isAlive()) {
						((CraftLivingEntity) entity).getHandle().setGoalTarget(((CraftLivingEntity) e.getPotion().getShooter()).getHandle());
						if (entity instanceof CraftCreature) {
							((CraftCreature) entity).getHandle().setTarget(((CraftLivingEntity) e.getPotion().getShooter()).getHandle());
						}
					}
				} else if (FactionMobs.noFriendlyFire) {
					e.setIntensity(entity, -1);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityPortal(EntityPortalEvent e) {
		if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		for (FactionMob fmob : FactionMobs.mobList) {
			if (fmob.getEntity().world.worldData.getName().equals(e.getChunk().getWorld().getName()) 
					&& !fmob.getEntity().world.entityList.contains(fmob.getEntity())) {
				fmob.getEntity().world.addEntity(fmob.getEntity());
				fmob.getEntity().dead = false;
			}
		}
	}
}
