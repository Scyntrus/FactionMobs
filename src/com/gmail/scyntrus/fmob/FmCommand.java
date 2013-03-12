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
			if (split.length == 0) {
				return false;
			} else if (split[0].equalsIgnoreCase("spawn")) {
				Player player = (Player) sender;
				if (!player.hasPermission("fmob.spawn")) {
					player.sendMessage("You do not have permission");
					return true;
				}
				Location loc = player.getLocation();
				FPlayer fplayer = FPlayers.i.get(player);
				Faction playerfaction = fplayer.getFaction();
				if (playerfaction.isNone()) {
					player.sendMessage("You must be in a faction");
					return true;
				}
				Faction areafaction = Board.getFactionAt(new FLocation(loc));
				if (!playerfaction.getTag().equals(areafaction.getTag())) {
					player.sendMessage("You may only spawn mobs in your territory");
					return true;
				}
				if (plugin.mobList.size() >= FactionMobs.spawnLimit) {
					player.sendMessage("There are too many faction mobs");
					return true;
				}
				net.minecraft.server.v1_4_R1.World world = ((CraftWorld)player.getWorld()).getHandle();
				FactionMob newMob = null;
				if (split.length == 1) {
					player.sendMessage("You must specify a mob");
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
					player.sendMessage("Unrecognized mob name");
					return true;
				}
				if (!newMob.getEnabled()) {
					player.sendMessage("Spawning " + newMob.getTypeName() + " has been disabled");
					newMob.die();
					return true;
				}
				
				if (plugin.vaultEnabled) {
					if (plugin.econ.has(player.getName(), newMob.getMoneyCost())) {
			            EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), newMob.getMoneyCost());
			            if(r.transactionSuccess()) {
			            	player.sendMessage(String.format("You paid %s and now have %s", plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
			            } else {
			            	player.sendMessage(String.format("An error occured: %s", r.errorMessage));
			                return true;
			            }
					} else {
		            	player.sendMessage(String.format("You don't have enough money"));
		                return true;
					}
				}
				
				if (newMob.getPowerCost() > 0) {
					if (fplayer.getPower() > newMob.getPowerCost()) {
						try{ 
					    	Method method = FPlayer.class.getDeclaredMethod("alterPower", new Class[] {double.class});
					    	method.setAccessible(true);
					    	method.invoke(fplayer, -newMob.getPowerCost());
					    	player.sendMessage(String.format("You spent %s power and now have", newMob.getPowerCost(), fplayer.getPower()));
						} catch (Exception e) {
			            	player.sendMessage(String.format("Failed to deduct power"));
			                return true;
						}
					} else {
		            	player.sendMessage(String.format("You don't have enough power"));
		                return true;
					}
				}
				
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				Utils.giveColorArmor(newMob);
				world.addEntity((Entity) newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned a " + newMob.getTypeName());
			} else if (split[0].equalsIgnoreCase("color")) {
				Player player = (Player) sender;
				if (!player.hasPermission("fmob.spawn")) {
					player.sendMessage("You do not have permission");
					return true;
				}
				FPlayer fplayer = FPlayers.i.get(player);
				Faction playerfaction = fplayer.getFaction();
				if (playerfaction.isNone()) {
					player.sendMessage("You must be in a faction");
					return true;
				}
				if (!playerfaction.getFPlayerAdmin().equals(fplayer)) {
					player.sendMessage("You must be the faction admin");
					return true;
				}
				if (split.length == 1) {
					player.sendMessage("You must specify a color in RRGGBB format");
					return true;
				} else {
					try {
						int myColor = Integer.parseInt(split[1], 16);
						FactionMobs.factionColors.put(fplayer.getFaction().getTag(), myColor);
						player.sendMessage("Set your faction color to " + myColor);
						plugin.updateList();
					} catch (NumberFormatException e) {
						player.sendMessage("Invalid number");
						return true;
					}
				}
			}
		} else {
			sender.sendMessage("You must be a player");
			return false;
		}
		return true;
	}
}
