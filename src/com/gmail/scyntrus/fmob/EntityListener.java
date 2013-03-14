package com.gmail.scyntrus.fmob;

import java.util.ArrayList;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityWolf;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
			e.getPlayer().sendMessage(String.format("%sThis %s%s %sbelongs to faction %s%s%s. HP: %s%s", 
					ChatColor.GREEN, ChatColor.RED, fmob.getTypeName(), ChatColor.GREEN, ChatColor.RED, 
					fmob.getFaction().getTag(), ChatColor.GREEN, ChatColor.RED, fmob.getHealth()));
			if (FPlayers.i.get(e.getPlayer()).getFaction().equals(fmob.getFaction())) {
				if (!plugin.playerSelections.containsKey(e.getPlayer().getName())) {
					plugin.playerSelections.put(e.getPlayer().getName(), new ArrayList<FactionMob>());
				}
				if (plugin.playerSelections.get(e.getPlayer().getName()).contains(fmob)) {
					plugin.playerSelections.get(e.getPlayer().getName()).remove(fmob);
					e.getPlayer().sendMessage(String.format("%sYou have deselected this %s%s", ChatColor.GREEN, ChatColor.RED, fmob.getTypeName()));
					if (plugin.playerSelections.get(e.getPlayer().getName()).isEmpty()) {
						plugin.playerSelections.remove(e.getPlayer().getName());
						e.getPlayer().sendMessage(String.format("%sYou have no mobs selected", ChatColor.GREEN));
					}
				} else {
					plugin.playerSelections.get(e.getPlayer().getName()).add(fmob);
					e.getPlayer().sendMessage(String.format("%sYou have selected this %s%s", ChatColor.GREEN, ChatColor.RED, fmob.getTypeName()));
					fmob.setPoi(fmob.getlocX(), fmob.getlocY(), fmob.getlocZ());
					fmob.setOrder("poi");
				}
			}
			fmob.updateMob();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		plugin.updateList();
		if (((CraftEntity)e.getEntity()).getHandle() instanceof FactionMob) {
			e.getDrops().clear();
		}
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
			} else if (plugin.noFriendlyFire) {
				e.setCancelled(true);
				return;
			}
		} else if (e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();
			if (arrow.getShooter() == null) {
				return;
			}
			if (((CraftLivingEntity) arrow.getShooter()).getHandle() instanceof FactionMob) {
				FactionMob fmob = (FactionMob) ((CraftLivingEntity) arrow.getShooter()).getHandle();
				if (Utils.FactionCheck(((CraftEntity) e.getEntity()).getHandle(), fmob.getFaction()) < 1) {
					((CraftLivingEntity) e.getEntity()).getHandle().setGoalTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
					((CraftCreature) e.getEntity()).getHandle().setTarget(((CraftLivingEntity) arrow.getShooter()).getHandle());
					return;
				} else if (plugin.noFriendlyFire) {
					e.setCancelled(true);
					return;
				}
			}
		}
		if (((CraftEntity) e.getEntity()).getHandle() instanceof FactionMob) {
			FactionMob fmob = (FactionMob) ((CraftEntity) e.getEntity()).getHandle();
			if (e.getDamager() instanceof Player) {
				Player player = (Player) e.getDamager();
				if (Utils.FactionCheck((Entity) fmob, FPlayers.i.get(player).getFaction()) >= 1) {
					if (fmob.getFaction().equals(FPlayers.i.get(player).getFaction())) {
						player.sendMessage(String.format("%sYou hit a friendly %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
						return;
					} else {
						player.sendMessage(String.format("%sYou cannot hit %s%s%s's %s%s", ChatColor.YELLOW, ChatColor.RED, fmob.getFaction(), ChatColor.YELLOW, ChatColor.RED, fmob.getTypeName()));
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
				} else if (plugin.noFriendlyFire) {
					e.setIntensity(entity, -1);
				}
			}
		}
	}
}
