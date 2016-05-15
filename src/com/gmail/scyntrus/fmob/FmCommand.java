package com.gmail.scyntrus.fmob;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_9_R2.Entity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;

public class FmCommand implements CommandExecutor {

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
            } else if (split[0].equalsIgnoreCase("info")) {
                if (!player.hasPermission("fmob.spawn")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to spawn faction mobs.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "You have permission to spawn faction mobs");
                }
                if (!player.hasPermission("fmob.order")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to order faction mobs.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "You have permission to order faction mobs");
                }
                player.sendMessage(ChatColor.BLUE + "Archer:");
                if (!Archer.enabled) {
                    player.sendMessage(ChatColor.RED + "disabled");
                } else {
                    if (plugin.vaultEnabled) player.sendMessage("cost: " + Archer.moneyCost);
                    player.sendMessage("power: " + Archer.powerCost);
                }
                player.sendMessage(ChatColor.BLUE + "Swordsman:");
                if (!Swordsman.enabled) {
                    player.sendMessage(ChatColor.RED + "disabled");
                } else {
                    if (plugin.vaultEnabled) player.sendMessage("cost: " + Swordsman.moneyCost);
                    player.sendMessage("power: " + Swordsman.powerCost);
                }
                player.sendMessage(ChatColor.BLUE + "Mage:");
                if (!Mage.enabled) {
                    player.sendMessage(ChatColor.RED + "disabled");
                } else {
                    if (plugin.vaultEnabled) player.sendMessage("cost: " + Mage.moneyCost);
                    player.sendMessage("power: " + Mage.powerCost);
                }
                player.sendMessage(ChatColor.BLUE + "Titan:");
                if (!Titan.enabled) {
                    player.sendMessage(ChatColor.RED + "disabled");
                } else {
                    if (plugin.vaultEnabled) player.sendMessage("cost: " + Titan.moneyCost);
                    player.sendMessage("power: " + Titan.powerCost);
                }
            } else if (split[0].equalsIgnoreCase("deselect")) {
                if (plugin.playerSelections.containsKey(player.getName())) {
                    plugin.playerSelections.get(player.getName()).clear();
                    plugin.playerSelections.remove(player.getName());
                    player.sendMessage("Selection cleared");
                    return true;
                }
                player.sendMessage("You have not selected any mob");
                return true;
            } else if (split[0].equalsIgnoreCase("selectall")) {
                if (!player.hasPermission("fmob.selectall")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                }
                if (plugin.playerSelections.containsKey(player.getName())) {
                    plugin.playerSelections.get(player.getName()).clear();
                } else {
                    plugin.playerSelections.put(player.getName(), new ArrayList<FactionMob>());
                }
                for (FactionMob fmob : FactionMobs.mobList) {
                    Faction playerFaction = FactionsManager.getPlayerFaction(player);
                    if (playerFaction != null && fmob.getFaction().getName().equals(playerFaction.getName())) {
                        plugin.playerSelections.get(player.getName()).add(fmob);
                    }
                }
                player.sendMessage(String.format("%sYou have selected %s mobs", ChatColor.GREEN, plugin.playerSelections.get(player.getName()).size()));
                return true;
            } else if (split[0].equalsIgnoreCase("selection")) {
                if (plugin.playerSelections.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.GREEN + "== Selection: ==");
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getEntity().isAlive()) player.sendMessage(ChatColor.RED + fmob.getTypeName());
                    }
                    player.sendMessage(ChatColor.GREEN + "================");
                    return true;
                }
                player.sendMessage("You have not selected any mob");
                return true;
            } else if (split[0].equalsIgnoreCase("spawn")) {
                if ((!player.hasPermission("fmob.spawn")
                        && !player.hasPermission("fmob.spawn.archer")
                        && !player.hasPermission("fmob.spawn.mage")
                        && !player.hasPermission("fmob.spawn.swordsman")
                        && !player.hasPermission("fmob.spawn.titan"))) {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }

                Location loc = player.getLocation();
                Faction playerfaction = FactionsManager.getPlayerFaction(player);
                if (playerfaction == null || playerfaction.isNone()) {
                    player.sendMessage(ChatColor.RED + "You must be in a faction.");
                    return true;
                }

                if (!FactionsManager.getPlayerRank(player).isAtLeast(FactionMobs.minRankToSpawn)) {
                    player.sendMessage(ChatColor.RED + "Your rank is too low.");
                    return true;
                }

                if (!player.hasPermission("fmob.bypass")) {
                    Faction areafaction = FactionsManager.getFactionAt(loc);
                    if (FactionMobs.onlySpawnInTerritory && FactionsManager.supportsLandOwnership() &&
                            (areafaction == null || !playerfaction.getName().equals(areafaction.getName()))) {
                        player.sendMessage(ChatColor.RED + "You may only spawn mobs in your territory");
                        return true;
                    }
                    if (FactionMobs.mobList.size() >= FactionMobs.spawnLimit) {
                        player.sendMessage(ChatColor.RED + "There are too many faction mobs");
                        return true;
                    }
                    if (FactionMobs.mobsPerFaction > 0) {
                        if (Utils.countMobsInFaction(playerfaction) >= FactionMobs.mobsPerFaction) {
                            player.sendMessage(ChatColor.RED + "Your faction has too many faction mobs.");
                            return true;
                        }
                    }
                }
                net.minecraft.server.v1_9_R2.World world = ((CraftWorld)player.getWorld()).getHandle();
                FactionMob newMob = null;
                if (split.length == 1) {
                    player.sendMessage(ChatColor.RED + "You must specify a mob");
                    return true;
                } else if (split[1].equalsIgnoreCase("Archer") || split[1].equalsIgnoreCase("Ranger")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.archer")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to spawn this mob.");
                        return true;
                    }
                    newMob = new Archer(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase("Swordsman")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.swordsman")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to spawn this mob.");
                        return true;
                    }
                    newMob = new Swordsman(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase("Titan") || split[1].equalsIgnoreCase("Golem")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.titan")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to spawn this mob.");
                        return true;
                    }
                    newMob = new Titan(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase("Mage") || split[1].equalsIgnoreCase("Witch")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.mage")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to spawn this mob.");
                        return true;
                    }
                    newMob = new Mage(player.getLocation(), playerfaction);
                } else {
                    player.sendMessage(ChatColor.RED + "Unrecognized mob name");
                    return true;
                }
                if (!newMob.getEnabled()) {
                    player.sendMessage(String.format("%sSpawning %s has been disabled", ChatColor.RED, newMob.getTypeName()));
                    newMob.forceDie();
                    return true;
                }

                if (!player.hasPermission("fmob.bypass")) {
                    if (newMob.getPowerCost() > 0) {
                        double factionPowerUsage = Utils.countMobPowerInFaction(playerfaction);
                        if (playerfaction.getPower() >= (factionPowerUsage + newMob.getPowerCost())) {
                            player.sendMessage(String.format("%sYour faction is now using %.2f/%.2f power for faction mobs.",
                                    ChatColor.GREEN, factionPowerUsage + newMob.getPowerCost(), playerfaction.getPower()));
                        } else {
                            player.sendMessage(String.format("%sYour faction is using %.2f/%.2f power for faction mobs.",
                                    ChatColor.RED, factionPowerUsage, playerfaction.getPower()));
                            player.sendMessage(String.format("%sYou need %.2f more power.", ChatColor.RED, factionPowerUsage + newMob.getPowerCost() - playerfaction.getPower()));
                            return true;
                        }
                    }

                    if (plugin.vaultEnabled && newMob.getMoneyCost() > 0) {
                        if (plugin.econ.has(player, newMob.getMoneyCost())) {
                            EconomyResponse r = plugin.econ.withdrawPlayer(player, newMob.getMoneyCost());
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

                if (world.addEntity((Entity) newMob, SpawnReason.CUSTOM)) {
                    FactionMobs.mobList.add(newMob);
                    player.sendMessage(String.format("You have spawned a %s", newMob.getTypeName()));
                } else {
                    newMob.forceDie();
                    player.sendMessage(String.format("%sYou have failed to spawn a %s", ChatColor.RED, newMob.getTypeName()));
                    if (playerfaction.monstersNotAllowed()) {
                        player.sendMessage(String.format("%sYour faction has disabled monster spawning, set your faction's \"monsters\" flag to true.", ChatColor.RED));
                    }
                    if (!player.hasPermission("fmob.bypass")) {
                        if (plugin.vaultEnabled && newMob.getMoneyCost() > 0) {
                            EconomyResponse r = plugin.econ.depositPlayer(player, newMob.getMoneyCost());
                            if(r.transactionSuccess()) {
                                player.sendMessage(String.format("You have been refunded %s and now have %s", plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
                            } else {
                                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                                plugin.getLogger().severe(String.format("Unable to refund money to %s", player.getName()));
                                return true;
                            }
                        }
                    }
                }
            } else if (split[0].equalsIgnoreCase("color")) {
                if (!player.hasPermission("fmob.color")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission");
                    return true;
                }
                Faction playerfaction = FactionsManager.getPlayerFaction(player);
                if (playerfaction == null || playerfaction.isNone()) {
                    player.sendMessage(ChatColor.RED + "You must be in a faction");
                    return true;
                }
                if (split.length == 1) {
                    player.sendMessage(ChatColor.RED + "You must specify a color in RRGGBB format");
                    return true;
                } else {
                    try {
                        int myColor = Integer.parseInt(split[1], 16);
                        FactionMobs.factionColors.put(playerfaction.getName(), myColor);
                        player.sendMessage(String.format("Set your faction color to %s", StringUtils.leftPad(Integer.toHexString(myColor), 6, "0")));
                        plugin.updateList();
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number");
                        return true;
                    }
                }
            } else if (split[0].equalsIgnoreCase("u")) {
                if (player.isOp()) {
                    plugin.updateList();
                    player.sendMessage(ChatColor.GREEN + "Faction Mobs refreshed");
                }
            } else if (split[0].equalsIgnoreCase("s")) {
                if (player.isOp()) {
                    plugin.saveMobList();
                    player.sendMessage(ChatColor.GREEN + "Faction Mobs data saved");
                    System.out.println("Faction Mobs data saved via command");
                }
            } else if (split[0].equalsIgnoreCase("order")) {
                if (!player.hasPermission("fmob.order")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission");
                    return true;
                }
                if (split.length < 2) {
                    player.sendMessage(ChatColor.RED + "You must specify an order");
                    player.sendMessage("Orders: gohome, follow, stop, patrolHere, wander, setHome, tpHome, tpHere");
                    return true;
                } else if (!plugin.playerSelections.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + "No mobs selected");
                    player.sendMessage(ChatColor.RED + "Before giving orders, you must select mobs by right-clicking them");
                    return true;
                } else {
                    Faction playerFaction = FactionsManager.getPlayerFaction(player);
                    if (playerFaction == null || playerFaction.isNone()) {
                        plugin.playerSelections.remove(player.getName());
                        player.sendMessage(ChatColor.RED + "You must be in a faction");
                        return true;
                    }
                    String factionName = playerFaction.getName();
                    List<FactionMob> selection = plugin.playerSelections.get(player.getName());
                    for (int i = selection.size()-1; i >= 0; i--) {
                        if (!selection.get(i).getEntity().isAlive()
                                || !selection.get(i).getFactionName().equals(factionName)) {
                            selection.remove(i);
                        }
                    }
                    if (selection.isEmpty()) {
                        plugin.playerSelections.remove(player.getName());
                        player.sendMessage(ChatColor.RED + "No mobs selected");
                        return true;
                    }
                }

                if (split[1].equalsIgnoreCase("gohome") || split[1].equalsIgnoreCase("home")) {
                    plugin.mobLeader.remove(player.getName());
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setOrder("home");
                        Location loc = fmob.getSpawn();
                        fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
                    }
                    player.sendMessage(ChatColor.GREEN + "You sent your mobs home");
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
                    player.sendMessage(ChatColor.GREEN + "Your mobs are now following you");
                    return true;
                } else if (split[1].equalsIgnoreCase("stop")) {
                    plugin.mobLeader.remove(player.getName());
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setOrder("poi");
                    }
                    player.sendMessage(ChatColor.GREEN + "You told your mobs to stop");
                    return true;
                } else if (split[1].equalsIgnoreCase("moveToPoint") || split[1].equalsIgnoreCase("move") || split[1].equalsIgnoreCase("point")) {
                    if (!player.hasPermission("fmob.order.move")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission");
                        return true;
                    }
                    plugin.mobLeader.remove(player.getName());
                    Block block = player.getTargetBlock((Set<Material>) null, 64);
                    if (block == null) {
                        player.sendMessage(ChatColor.RED + "You must be pointing at a block");
                        return true;
                    }
                    Location loc = block.getLocation().add(0,1,0);
                    Location playerLoc = player.getLocation();
                    int count = 0;
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getSpawn().getWorld().getName().equals(playerLoc.getWorld().getName())) {
                            double tmpX = (1.5-(count%4))*1.5;
                            double tmpZ = ((-1.) - Math.floor(count / 4.))*1.5;
                            double tmpH = Math.hypot(tmpX, tmpZ);
                            double angle = Math.atan2(tmpZ, tmpX) + (playerLoc.getYaw() * Math.PI / 180.);
                            fmob.setPoi(loc.getX() + tmpH*Math.cos(angle), loc.getY(), loc.getZ() + tmpH*Math.sin(angle));
                            fmob.setOrder("poi");
                            count += 1;
                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "You told your mobs to move where you're pointing");
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
                    player.sendMessage(ChatColor.GREEN + "Your mobs will now patrol from their home to here");
                    return true;
                } else if (split[1].equalsIgnoreCase("wander")) {
                    plugin.mobLeader.remove(player.getName());
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setOrder("wander");
                    }
                    player.sendMessage(ChatColor.GREEN + "Your mobs will now wander around");
                    return true;
                } else if (split[1].equalsIgnoreCase("setHome")) {
                    plugin.mobLeader.put(player.getName(), true);
                    Location loc = player.getLocation();
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getSpawn().getWorld().equals(loc.getWorld())) {
                            fmob.setOrder("home");
                            Location spawnLoc = fmob.getSpawn();
                            spawnLoc.setX(loc.getX());
                            spawnLoc.setY(loc.getY());
                            spawnLoc.setZ(loc.getZ());
                            fmob.setPoi(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
                        } else {
                            player.sendMessage(String.format("%s%s is on a different world", ChatColor.RED, fmob.getTypeName()));
                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "You set your position as your mob's new home");
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
                        fmob.getEntity().setPosition(loc.getX(), loc.getY(), loc.getZ());
                        fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
                    }
                    player.sendMessage(ChatColor.GREEN + "Your mobs are now back at their home");
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
                            fmob.getEntity().setPosition(tmpX, loc.getY(), tmpZ);
                            fmob.setOrder("poi");
                            count++;
                        } else {
                            player.sendMessage(String.format("%s%s is on a different world", ChatColor.RED, fmob.getTypeName()));
                        }
                    }
                    player.sendMessage("Your mobs are now with you");
                    return true;
                } else if (split[1].equalsIgnoreCase("forgive")) {
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.clearAttackedBy();
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unrecognized order");
                    player.sendMessage("Orders: gohome, follow, stop, patrolHere, wander, setHome, tpHome, tpHere");
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
