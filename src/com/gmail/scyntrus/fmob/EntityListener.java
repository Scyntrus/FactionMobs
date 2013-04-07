package com.gmail.scyntrus.fmob;

import java.util.ArrayList;

import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityWolf;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
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
import org.bukkit.event.world.ChunkLoadEvent;

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
		if (((CraftEntity)e.getEntity()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getEntity()).getHandle();
			fmob.getEntity().setEquipment(1, null);
			fmob.getEntity().setEquipment(2, null);
			fmob.getEntity().setEquipment(3, null);
			fmob.getEntity().setEquipment(4, null);
			e.getDrops().clear();
			FactionMobs.mobList.remove(fmob);
		}
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        	public void run() {
    			plugin.updateList();
        	}
        }, 1L);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (((CraftEntity) e.getDamager()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getDamager()).getHandle();
			if (Utils.FactionCheck(((CraftEntity) e.getEntity()).getHandle(), fmob.getFaction()) < 1) {
				((CraftLivingEntity) e.getEntity()).getHandle().setGoalTarget(((CraftLivingEntity) e.getDamager()).getHandle());
				if (e.getEntity() instanceof CraftCreature) {
					((CraftCreature) e.getEntity()).getHandle().setTarget(((CraftLivingEntity) e.getDamager()).getHandle());
				}
				return;
			} else if (FactionMobs.noFriendlyFire) {
				e.setCancelled(true);
				return;
			}
		} else if (e.getDamager() instanceof Projectile) {
			Projectile arrow = (Projectile) e.getDamager();
			if (arrow.getShooter() == null) {
				return;
			}
			if (((CraftLivingEntity) arrow.getShooter()).getHandle() instanceof FactionMob) {
				FactionMob fmob = (FactionMob) ((CraftLivingEntity) arrow.getShooter()).getHandle();
				if (Utils.FactionCheck(((CraftEntity) e.getEntity()).getHandle(), fmob.getFaction()) < 1) {
					((CraftLivingEntity) e.getEntity()).getHandle().setGoalTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
					if (e.getEntity() instanceof CraftCreature) {
						((CraftCreature) e.getEntity()).getHandle().setTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
					}
					return;
				} else if (FactionMobs.noFriendlyFire) {
					e.setCancelled(true);
					return;
				}
			}
		}
		if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getEntity()).getHandle();
			if (fmob.getFaction() == null) {
				return;
			}
			if (e.getDamager() instanceof Player) {
				Player player = (Player) e.getDamager();
				if (Utils.FactionCheck((Entity) fmob, FPlayers.i.get(player).getFaction()) >= 1) {
					if (fmob.getFaction().equals(FPlayers.i.get(player).getFaction())) {
						player.sendMessage(String.format("%sYou hit a friendly %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
						return;
					} else {
						player.sendMessage(String.format("%sYou cannot hit %s%s%s's %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getFactionName(), ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
						e.setCancelled(true);
						return;
					}
				}
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
//	
//	@EventHandler
//	public void onPlayerQuit(PlayerQuitEvent e) {
//		Player player = e.getPlayer();
//		if (plugin.playerSelections.containsKey(player.getName())) {
//			plugin.playerSelections.get(player.getName()).clear();
//			plugin.playerSelections.remove(player.getName());
//		}
//	}
	
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
		if (((CraftEntity) e.getPotion().getShooter()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getPotion().getShooter()).getHandle();
			for (LivingEntity entity : e.getAffectedEntities()) {
				if (Utils.FactionCheck(((CraftEntity) entity).getHandle(), fmob.getFaction()) < 1) {
					((CraftLivingEntity) entity).getHandle().setGoalTarget(((CraftLivingEntity) e.getPotion().getShooter()).getHandle());
					if (entity instanceof CraftCreature) {
						((CraftCreature) entity).getHandle().setTarget(((CraftLivingEntity) e.getPotion().getShooter()).getHandle());
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
		if (!e.isNewChunk()) {
			Chunk chunk = e.getChunk();
			double minX = chunk.getX() * 16 - 16;
			double maxX = chunk.getX() * 16;
			double minZ = chunk.getZ() * 16 - 16;
			double maxZ = chunk.getZ() * 16;
			for (FactionMob fmob : FactionMobs.mobList) {
				if (fmob.getEntity().world.worldData.getName().equals(chunk.getWorld().getName()) 
						&& minX <= fmob.getlocX() && fmob.getlocX() <= maxX
						&& minZ <= fmob.getlocZ() && fmob.getlocZ() <= maxZ) {
					if (!fmob.getEntity().world.entityList.contains(fmob.getEntity())) {
						fmob.getEntity().world.addEntity(fmob.getEntity());
					}
				}
			}
		}
	}
}
