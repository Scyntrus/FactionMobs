package com.gmail.scyntrus.fmob;

import java.lang.reflect.Method;

import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_4_R1.Entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class FmCommand  implements CommandExecutor{

	FactionMobs plugin;
	
	public FmCommand(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (split.length == 0) {
				return false;
			} else if (split[0].equalsIgnoreCase("help")) {
				//Basic help info here.
				player.sendMessage("/fm spawn [mob]");
				player.sendMessage("Mobs: Archer, Swordsman, Titan, Mage");
				player.sendMessage("/fm color [color]");
				player.sendMessage("Color is in RRGGBB format");
				player.sendMessage("/fm order [order]");
				player.sendMessage("Orders: gohome, follow, stop, patrolHere, wander, tpHome, tpHere");
				player.sendMessage("Before giving orders, you must select mobs by right-clicking them");
			} else if (split[0].equalsIgnoreCase("deselect")) {
				if (plugin.playerSelections.containsKey(player.getName())) {
					plugin.playerSelections.get(player.getName()).clear();
					plugin.playerSelections.remove(player.getName());
					player.sendMessage("Selection cleared");
					return true;
				}
				player.sendMessage("You have not selected any mob");
				return true;
			} else if (split[0].equalsIgnoreCase("selection")) {
				if (plugin.playerSelections.containsKey(player.getName())) {
					player.sendMessage(ChatColor.GREEN + "== Selection: ==");
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						player.sendMessage(ChatColor.RED + fmob.getTypeName());
					}
					player.sendMessage(ChatColor.GREEN + "================");
					return true;
				}
				return true;
			} else if (split[0].equalsIgnoreCase("spawn")) {
				if (!player.hasPermission("fmob.spawn")) {
					player.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				Location loc = player.getLocation();
				FPlayer fplayer = FPlayers.i.get(player);
				Faction playerfaction = fplayer.getFaction();
				if (playerfaction == null || playerfaction.isNone()) {
					player.sendMessage(ChatColor.RED + "You must be in a faction");
					return true;
				}
				Faction areafaction = Board.getFactionAt(new FLocation(loc));
				if (!playerfaction.getTag().equals(areafaction.getTag())) {
					player.sendMessage(ChatColor.RED + "You may only spawn mobs in your territory");
					return true;
				}
				if (plugin.mobList.size() >= plugin.spawnLimit) {
					player.sendMessage(ChatColor.RED + "There are too many faction mobs");
					return true;
				}
				net.minecraft.server.v1_4_R1.World world = ((CraftWorld)player.getWorld()).getHandle();
				FactionMob newMob = null;
				if (split.length == 1) {
					player.sendMessage(ChatColor.RED + "You must specify a mob");
					return true;
				} else if (split[1].equalsIgnoreCase("Archer") || split[1].equalsIgnoreCase("Ranger")) {
					newMob = new Archer(world);
				} else if (split[1].equalsIgnoreCase("Swordsman")) {
					newMob = new Swordsman(world);
				} else if (split[1].equalsIgnoreCase("Titan")) {
					newMob = new Titan(world);
				} else if (split[1].equalsIgnoreCase("Mage")) {
					newMob = new Mage(world);
				} else {
					player.sendMessage(ChatColor.RED + "Unrecognized mob name");
					return true;
				}
				if (!newMob.getEnabled()) {
					player.sendMessage(String.format("%sSpawning %s has been disabled", ChatColor.RED, newMob.getTypeName()));
					newMob.die();
					return true;
				}
				
				if (newMob.getPowerCost() > 0) {
					if (fplayer.getPower() > newMob.getPowerCost()) {
						if (plugin.vaultEnabled) {
							if (plugin.econ.has(player.getName(), newMob.getMoneyCost())) {
					            EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), newMob.getMoneyCost());
					            if(r.transactionSuccess()) {
					            	player.sendMessage(String.format("You paid %s and now have %s", plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
					            } else {
					            	player.sendMessage(String.format("%sAn error occured: %s", ChatColor.RED, r.errorMessage));
					            	plugin.getLogger().severe(String.format("Unable to deduct money from %s", player.getName()));
					                return true;
					            }
							} else {
				            	player.sendMessage(ChatColor.RED + "You don't have enough money");
				                return true;
							}
						}
						try { 
					    	Method method = FPlayer.class.getDeclaredMethod("alterPower", new Class[] {double.class});
					    	method.setAccessible(true);
					    	method.invoke(fplayer, -newMob.getPowerCost());
					    	player.sendMessage(String.format("You spent %s power and now have %s", newMob.getPowerCost(), fplayer.getPower()));
						} catch (Exception e) {
			            	player.sendMessage(ChatColor.RED + "Failed to deduct power");
			            	plugin.getLogger().severe(String.format("Unable to deduct power from %s", player.getName()));
			                return true;
						}
					} else {
		            	player.sendMessage(ChatColor.RED + "You don't have enough power");
		                return true;
					}
				} else {
					if (plugin.vaultEnabled) {
						if (plugin.econ.has(player.getName(), newMob.getMoneyCost())) {
				            EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), newMob.getMoneyCost());
				            if(r.transactionSuccess()) {
				            	player.sendMessage(String.format("You paid %s and now have %s", plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
				            } else {
				            	player.sendMessage(String.format("An error occured: %s", r.errorMessage));
				            	plugin.getLogger().severe(String.format("Unable to deduct money from %s", player.getName()));
				                return true;
				            }
						} else {
			            	player.sendMessage(ChatColor.RED + "You don't have enough money");
			                return true;
						}
					}
				}
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				Utils.giveColorArmor(newMob);
				newMob.setPoi(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
				newMob.setOrder("home");
				world.addEntity((Entity) newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage(String.format("You have spawned a %s", newMob.getTypeName()));
			} else if (split[0].equalsIgnoreCase("color")) {
				if (!player.hasPermission("fmob.color")) {
					player.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				FPlayer fplayer = FPlayers.i.get(player);
				Faction playerfaction = fplayer.getFaction();
				if (playerfaction.isNone()) {
					player.sendMessage(ChatColor.RED + "You must be in a faction");
					return true;
				}
				if (split.length == 1) {
					player.sendMessage(ChatColor.RED + "You must specify a color in RRGGBB format");
					return true;
				} else {
					try {
						int myColor = Integer.parseInt(split[1], 16);
						FactionMobs.factionColors.put(fplayer.getFaction().getTag(), myColor);
						player.sendMessage(String.format("Set your faction color to %s", Integer.toHexString(myColor)));
						plugin.updateList();
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Invalid number");
						return true;
					}
				}
			} else if (split[0].equalsIgnoreCase("u")) {
				if (player.isOp()) {
					plugin.updateList();
					player.sendMessage("Faction Mobs refreshed");
				}
			} else if (split[0].equalsIgnoreCase("s")) {
				if (player.isOp()) {
					plugin.saveMobList();
					player.sendMessage("Faction Mobs data saved");
					System.out.println("Faction Mobs data saved via command");
				}
			} else if (split[0].equalsIgnoreCase("order")) {
				if (!player.hasPermission("fmob.order")) {
					player.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				if (split.length < 2) {
					player.sendMessage(ChatColor.RED + "You must specify an order");
					player.sendMessage("Orders: gohome, follow, stop, patrolHere, wander, tpHome, tpHere");
					return true;
				} else if (!plugin.playerSelections.containsKey(player.getName())) {
					player.sendMessage(ChatColor.RED + "No mobs selected");
					player.sendMessage("Before giving orders, you must select mobs by right-clicking them");
					return true;
				} else if (split[1].equalsIgnoreCase("gohome") || split[1].equalsIgnoreCase("home")) {
					plugin.mobLeader.remove(player.getName());
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						fmob.setOrder("home");
						Location loc = fmob.getSpawn();
						fmob.setPosition(loc.getX(), loc.getY(), loc.getZ());
					}
					player.sendMessage("You sent your mobs home");
					return true;
				} else if (split[1].equalsIgnoreCase("follow")) {
					plugin.mobLeader.put(player.getName(), true);
					Location loc = player.getLocation();
					int count = 0;
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						if (fmob.getSpawn().getWorld().getName().equals(loc.getWorld().getName())) {
							double tmpX = (1.5-(count%4))*1.5;
							double tmpZ = ((-1.) - Math.floor(count / 4.))*1.5;
							double tmpH = Math.hypot(tmpX, tmpZ);
							double angle = Math.atan2(tmpZ, tmpX) + (loc.getYaw() * Math.PI / 180.);
							fmob.setPoi(loc.getX() + tmpH*Math.cos(angle), loc.getY(), loc.getZ() + tmpH*Math.sin(angle));
							fmob.setOrder("poi");
							count += 1;
						}
					}
					player.sendMessage("Your mobs are now following you");
					return true;
				} else if (split[1].equalsIgnoreCase("stop")) {
					plugin.mobLeader.remove(player.getName());
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						fmob.setOrder("poi");
					}
					player.sendMessage("You told your mobs to stop");
					return true;
				} else if (split[1].equalsIgnoreCase("patrolHere") || split[1].equalsIgnoreCase("patrol")) {
					plugin.mobLeader.remove(player.getName());
					Location loc = player.getLocation();
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						if (fmob.getSpawn().getWorld().getName().equals(loc.getWorld().getName())) {
							fmob.setOrder("ppoi");
							fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
						} else {
							player.sendMessage(String.format("%s%s is on a different world", ChatColor.RED, fmob.getTypeName()));
						}
					}
					player.sendMessage("Your mobs will now patrol from their home to here");
					return true;
				} else if (split[1].equalsIgnoreCase("wander")) {
					plugin.mobLeader.remove(player.getName());
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						fmob.setOrder("wander");
					}
					player.sendMessage("Your mobs will now wander around");
					return true;
				} else if (split[1].equalsIgnoreCase("tpHome")) {
					plugin.mobLeader.remove(player.getName());
					if (!player.hasPermission("fmob.order.tp")) {
						player.sendMessage(ChatColor.RED + "You do not have permission");
						return true;
					}
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						fmob.setOrder("home");
						Location loc = fmob.getSpawn();
						fmob.setPosition(loc.getX(), loc.getY(), loc.getZ());
					}
					player.sendMessage("Your mobs are now back at their home");
					return true;
				} else if (split[1].equalsIgnoreCase("tpHere")) {
					plugin.mobLeader.put(player.getName(), true);
					if (!player.hasPermission("fmob.order.tp")) {
						player.sendMessage(ChatColor.RED + "You do not have permission");
						return true;
					}
					Location loc = player.getLocation();
					int count = 0;
					for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
						if (fmob.getSpawn().getWorld().equals(loc.getWorld())) {
							double tmpX = (1.5-(count%4))*1.5;
							double tmpZ = ((-1.) - Math.floor(count / 4.))*1.5;
							double tmpH = Math.hypot(tmpX, tmpZ);
							double angle = Math.atan2(tmpZ, tmpX) + (loc.getYaw() * Math.PI / 180.);
							tmpX = loc.getX() + tmpH*Math.cos(angle);
							tmpZ = loc.getZ() + tmpH*Math.sin(angle);
							fmob.setPoi(tmpX, loc.getY(), tmpZ);
							fmob.setPosition(tmpX, loc.getY(), tmpZ);
							fmob.setOrder("poi");
						} else {
							player.sendMessage(String.format("%s%s is on a different world", ChatColor.RED, fmob.getTypeName()));
						}
					}
					player.sendMessage("Your mobs are now with you");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "Unrecognized order");
					player.sendMessage("Orders: gohome, follow, stop, patrolHere, wander, tpHome, tpHere");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Unrecognized command");
				return false;
			}
		} else {
			sender.sendMessage("You must be a player");
			return false;
		}
		return true;
	}
}
