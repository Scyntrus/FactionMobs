package com.gmail.scyntrus.fmob;

import java.lang.reflect.Method;

import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_4_R1.Entity;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import org.bukkit.ChatColor;
import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Ranger;
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
				if (!player.hasPermission("fmob.spawn")) {
					player.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				player.sendMessage("/fm spawn [mob]");
				player.sendMessage("Mobs: Archer, Swordsman, Ranger, Titan, Mage");
				player.sendMessage("/fm color [color]");
				player.sendMessage("[color] is in RRGGBB format");
				
						
			
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
				} else if (split[1].equalsIgnoreCase("Archer")) {
					newMob = new Archer(world);
				} else if (split[1].equalsIgnoreCase("Swordsman")) {
					newMob = new Swordsman(world);
				} else if (split[1].equalsIgnoreCase("Ranger")) {
					newMob = new Ranger(world);
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
				world.addEntity((Entity) newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage(String.format("You have spawned a %s", newMob.getTypeName()));
			} else if (split[0].equalsIgnoreCase("color")) {
				if (!player.hasPermission("fmob.spawn")) {
					player.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				FPlayer fplayer = FPlayers.i.get(player);
				Faction playerfaction = fplayer.getFaction();
				if (playerfaction.isNone()) {
					player.sendMessage(ChatColor.RED + "You must be in a faction");
					return true;
				}
				if (!playerfaction.getFPlayerAdmin().equals(fplayer)) {
					player.sendMessage(ChatColor.RED + "You must be the faction admin");
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
