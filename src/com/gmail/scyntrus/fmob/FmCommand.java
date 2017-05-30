package com.gmail.scyntrus.fmob;

import com.gmail.scyntrus.fmob.Messages.Message;
import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.SpiritBear;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_12_R1.Entity;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

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
                player.sendMessage(Messages.get(Message.FM_HELP));
            } else if (split[0].equalsIgnoreCase("info")) {
                if (!player.hasPermission("fmob.spawn")) {
                    player.sendMessage(Messages.get(Message.FM_INFO_NOSPAWN));
                } else {
                    player.sendMessage(Messages.get(Message.FM_INFO_SPAWN));
                }
                if (!player.hasPermission("fmob.order")) {
                    player.sendMessage(Messages.get(Message.FM_INFO_NOCOMMAND));
                } else {
                    player.sendMessage(Messages.get(Message.FM_INFO_COMMAND));
                }
                player.sendMessage(Messages.get(Message.FM_INFO_MOB, Messages.get(Message.NAME_ARCHER)));
                if (!Archer.enabled) {
                    player.sendMessage(Messages.get(Message.FM_INFO_DISABLED));
                } else {
                    if (plugin.vaultEnabled)
                        player.sendMessage(Messages.get(Message.FM_INFO_COST, plugin.econ.format(Archer.moneyCost)));
                    player.sendMessage(Messages.get(Message.FM_INFO_POWER, Archer.powerCost));
                }
                player.sendMessage(Messages.get(Message.FM_INFO_MOB, Messages.get(Message.NAME_SWORDSMAN)));
                if (!Swordsman.enabled) {
                    player.sendMessage(Messages.get(Message.FM_INFO_DISABLED));
                } else {
                    if (plugin.vaultEnabled)
                        player.sendMessage(Messages.get(Message.FM_INFO_COST, plugin.econ.format(Swordsman.moneyCost)));
                    player.sendMessage(Messages.get(Message.FM_INFO_POWER, Swordsman.powerCost));
                }
                player.sendMessage(Messages.get(Message.FM_INFO_MOB, Messages.get(Message.NAME_MAGE)));
                if (!Mage.enabled) {
                    player.sendMessage(Messages.get(Message.FM_INFO_DISABLED));
                } else {
                    if (plugin.vaultEnabled)
                        player.sendMessage(Messages.get(Message.FM_INFO_COST, plugin.econ.format(Mage.moneyCost)));
                    player.sendMessage(Messages.get(Message.FM_INFO_POWER, Mage.powerCost));
                }
                player.sendMessage(Messages.get(Message.FM_INFO_MOB, Messages.get(Message.NAME_TITAN)));
                if (!Titan.enabled) {
                    player.sendMessage(Messages.get(Message.FM_INFO_DISABLED));
                } else {
                    if (plugin.vaultEnabled)
                        player.sendMessage(Messages.get(Message.FM_INFO_COST, plugin.econ.format(Titan.moneyCost)));
                    player.sendMessage(Messages.get(Message.FM_INFO_POWER, Titan.powerCost));
                }
                player.sendMessage(Messages.get(Message.FM_INFO_MOB, Messages.get(Message.NAME_SPIRITBEAR)));
                if (!SpiritBear.enabled) {
                    player.sendMessage(Messages.get(Message.FM_INFO_DISABLED));
                } else {
                    if (plugin.vaultEnabled)
                        player.sendMessage(Messages.get(Message.FM_INFO_COST, plugin.econ.format(SpiritBear.moneyCost)));
                    player.sendMessage(Messages.get(Message.FM_INFO_POWER, SpiritBear.powerCost));
                }
            } else if (split[0].equalsIgnoreCase("deselect")) {
                if (plugin.playerSelections.containsKey(player.getName())) {
                    plugin.playerSelections.get(player.getName()).clear();
                    player.sendMessage(Messages.get(Message.FM_DESELECT));
                    return true;
                }
                player.sendMessage(Messages.get(Message.FM_NOSELECTION));
                return true;
            } else if (split[0].equalsIgnoreCase("selectall")) {
                if (!player.hasPermission("fmob.selectall")) {
                    player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                }
                List<FactionMob> selection = plugin.getPlayerSelection(player);
                selection.clear();
                for (FactionMob fmob : FactionMobs.mobList) {
                    Faction playerFaction = FactionsManager.getPlayerFaction(player);
                    if (playerFaction != null && fmob.getFaction().equals(playerFaction)) {
                        selection.add(fmob);
                    }
                }
                player.sendMessage(Messages.get(Message.FM_SELECTALL, selection.size()));
                return true;
            } else if (split[0].equalsIgnoreCase("selection")) {
                if (plugin.playerSelections.containsKey(player.getName())) {
                    player.sendMessage(Messages.get(Message.FM_SELECTIONSTART));
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getEntity().isAlive())
                            player.sendMessage(Messages.get(Message.FM_SELECTIONITEM, fmob.getLocalizedName()));
                    }
                    player.sendMessage(Messages.get(Message.FM_SELECTIONSTOP));
                    return true;
                }
                player.sendMessage(Messages.get(Message.FM_NOSELECTION));
                return true;
            } else if (split[0].equalsIgnoreCase("spawn")) {
                if ((!player.hasPermission("fmob.spawn")
                        && !player.hasPermission("fmob.spawn.archer")
                        && !player.hasPermission("fmob.spawn.mage")
                        && !player.hasPermission("fmob.spawn.swordsman")
                        && !player.hasPermission("fmob.spawn.titan"))) {
                    player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                    return true;
                }

                Location loc = player.getLocation();
                Faction playerfaction = FactionsManager.getPlayerFaction(player);
                if (playerfaction == null || playerfaction.isNone()) {
                    player.sendMessage(Messages.get(Message.FM_NOFACTION));
                    return true;
                }

                if (!FactionsManager.getPlayerRank(player).isAtLeast(FactionMobs.minRankToSpawn)) {
                    player.sendMessage(Messages.get(Message.FM_NORANK));
                    return true;
                }

                if (!player.hasPermission("fmob.bypass")) {
                    Faction areafaction = FactionsManager.getFactionAt(loc);
                    if (FactionMobs.onlySpawnInTerritory && FactionsManager.supportsLandOwnership() &&
                            (areafaction == null || !playerfaction.getName().equals(areafaction.getName()))) {
                        player.sendMessage(Messages.get(Message.FM_NOTERRITORY));
                        return true;
                    }
                    if (FactionMobs.mobsPerFaction > 0) {
                        if (Utils.countMobsInFaction(playerfaction) >= FactionMobs.mobsPerFaction) {
                            player.sendMessage(Messages.get(Message.FM_NOCAPACITY));
                            return true;
                        }
                    }
                }
                net.minecraft.server.v1_12_R1.World world = ((CraftWorld)player.getWorld()).getHandle();
                FactionMob newMob;
                if (split.length == 1) {
                    player.sendMessage(Messages.get(Message.FM_NOMOB));
                    return true;
                } else if (split[1].equalsIgnoreCase(Archer.typeName) || split[1].equalsIgnoreCase(Archer.localizedName) || split[1].equalsIgnoreCase("Ranger")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.archer")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSIONMOB));
                        return true;
                    }
                    newMob = new Archer(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase(Swordsman.typeName) || split[1].equalsIgnoreCase(Swordsman.localizedName)) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.swordsman")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSIONMOB));
                        return true;
                    }
                    newMob = new Swordsman(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase(Titan.typeName) || split[1].equalsIgnoreCase(Titan.localizedName) || split[1].equalsIgnoreCase("Golem")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.titan")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSIONMOB));
                        return true;
                    }
                    newMob = new Titan(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase(Mage.typeName) || split[1].equalsIgnoreCase(Mage.localizedName) || split[1].equalsIgnoreCase("Witch")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.mage")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSIONMOB));
                        return true;
                    }
                    newMob = new Mage(player.getLocation(), playerfaction);
                } else if (split[1].equalsIgnoreCase(SpiritBear.typeName) || split[1].equalsIgnoreCase(SpiritBear.localizedName) || split[1].equalsIgnoreCase("Bear")) {
                    if (!player.hasPermission("fmob.spawn") && !player.hasPermission("fmob.spawn.spiritbear")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSIONMOB));
                        return true;
                    }
                    newMob = new SpiritBear(player.getLocation(), playerfaction);
                } else {
                    player.sendMessage(Messages.get(Message.FM_NOMOB));
                    return true;
                }
                if (!newMob.getEnabled()) {
                    player.sendMessage(Messages.get(Message.FM_SPAWNDISABLED));
                    newMob.forceDie();
                    return true;
                }

                if (!player.hasPermission("fmob.bypass")) {
                    if (newMob.getPowerCost() > 0) {
                        double factionPowerUsage = Utils.countMobPowerInFaction(playerfaction);
                        if (playerfaction.getPower() >= (factionPowerUsage + newMob.getPowerCost())) {
                            player.sendMessage(Messages.get(Message.FM_POWERUSAGE,
                                    factionPowerUsage + newMob.getPowerCost(), playerfaction.getPower()));
                        } else {
                            player.sendMessage(Messages.get(Message.FM_NOPOWERUSAGE, factionPowerUsage,
                                    playerfaction.getPower(), factionPowerUsage + newMob.getPowerCost() - playerfaction.getPower()));
                            return true;
                        }
                    }

                    if (plugin.vaultEnabled && newMob.getMoneyCost() > 0) {
                        if (plugin.econ.has(player, newMob.getMoneyCost())) {
                            EconomyResponse r = plugin.econ.withdrawPlayer(player, newMob.getMoneyCost());
                            if(r.transactionSuccess()) {
                                player.sendMessage(Messages.get(Message.FM_MONEYUSAGE,
                                        plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
                            } else {
                                player.sendMessage(Messages.get(Message.FM_MONEYERROR, r.errorMessage));
                                plugin.getLogger().severe(String.format("Unable to deduct money from %s", player.getName()));
                                return true;
                            }
                        } else {
                            player.sendMessage(Messages.get(Message.FM_NOMONEY));
                            return true;
                        }
                    }
                }

                if (world.addEntity((Entity) newMob, SpawnReason.CUSTOM)) {
                    FactionMobs.mobList.add(newMob);
                    player.sendMessage(Messages.get(Message.FM_SPAWNSUCCESS, newMob.getLocalizedName()));
                } else {
                    newMob.forceDie();
                    player.sendMessage(Messages.get(Message.FM_SPAWNFAIL, newMob.getLocalizedName()));
                    if (playerfaction.monstersNotAllowed()) {
                        player.sendMessage(Messages.get(Message.FM_MONSTERSDISABLED));
                    }
                    if (!player.hasPermission("fmob.bypass")) {
                        if (plugin.vaultEnabled && newMob.getMoneyCost() > 0) {
                            EconomyResponse r = plugin.econ.depositPlayer(player, newMob.getMoneyCost());
                            if(r.transactionSuccess()) {
                                player.sendMessage(Messages.get(Message.FM_REFUNDMONEY, plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
                            } else {
                                player.sendMessage(Messages.get(Message.FM_MONEYERROR, r.errorMessage));
                                plugin.getLogger().severe(String.format("Unable to refund money to %s", player.getName()));
                                return true;
                            }
                        }
                    }
                }
            } else if (split[0].equalsIgnoreCase("color")) {
                if (!player.hasPermission("fmob.color")) {
                    player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                    return true;
                }
                Faction playerfaction = FactionsManager.getPlayerFaction(player);
                if (playerfaction == null || playerfaction.isNone()) {
                    player.sendMessage(Messages.get(Message.FM_NOFACTION));
                    return true;
                }
                if (split.length == 1) {
                    player.sendMessage(Messages.get(Message.FM_COLORFORMAT));
                    return true;
                } else {
                    try {
                        int myColor = Integer.parseInt(split[1], 16);
                        if (myColor > 16777215 || myColor < 0) {
                            player.sendMessage(Messages.get(Message.FM_COLORFORMAT));
                            return true;
                        }
                        FactionMobs.factionColors.put(playerfaction.getName(), myColor);
                        player.sendMessage(Messages.get(Message.FM_COLORSUCCESS, StringUtils.leftPad(Integer.toHexString(myColor), 6, "0")));
                        plugin.updateList();
                    } catch (NumberFormatException e) {
                        player.sendMessage(Messages.get(Message.FM_COLORFORMAT));
                        return true;
                    }
                }
            } else if (split[0].equalsIgnoreCase("u")) {
                if (player.isOp()) {
                    plugin.updateList();
                    // Test command, not localized
                    player.sendMessage(ChatColor.GREEN + "Faction Mobs refreshed");
                }
            } else if (split[0].equalsIgnoreCase("s")) {
                if (player.isOp()) {
                    plugin.saveMobList();
                    // Test command, not localized
                    player.sendMessage(ChatColor.GREEN + "Faction Mobs data saved");
                    System.out.println("Faction Mobs data saved via command");
                }
            } else if (split[0].equalsIgnoreCase("order") || split[0].equalsIgnoreCase("command")) {
                if (!player.hasPermission("fmob.order")) {
                    player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                    return true;
                }
                if (split.length < 2) {
                    player.sendMessage(Messages.get(Message.FM_NOCOMMAND));
                    return true;
                } else {
                    Faction playerFaction = FactionsManager.getPlayerFaction(player);
                    if (playerFaction == null || playerFaction.isNone()) {
                        player.sendMessage(Messages.get(Message.FM_NOFACTION));
                        return true;
                    }
                    String factionName = playerFaction.getName();
                    List<FactionMob> selection = plugin.playerSelections.get(player.getName());
                    if (selection == null) {
                        player.sendMessage(Messages.get(Message.FM_NOSELECTION));
                        return true;
                    }
                    for (Iterator<FactionMob> it = selection.iterator(); it.hasNext();) {
                        FactionMob fmob = it.next();
                        if (!fmob.getEntity().isAlive() || !fmob.getFactionName().equals(factionName)) {
                            it.remove();
                        }
                    }
                    if (selection.isEmpty()) {
                        player.sendMessage(Messages.get(Message.FM_NOSELECTION));
                        return true;
                    }
                }
                plugin.mobLeader.remove(player.getName());
                if (split[1].equalsIgnoreCase("gohome") || split[1].equalsIgnoreCase("home")) {
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setCommand(FactionMob.Command.home);
                        Location loc = fmob.getSpawn();
                        fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_HOME));
                    return true;
                } else if (split[1].equalsIgnoreCase("follow")) {
                    plugin.mobLeader.add(player.getName());
                    Location loc = player.getLocation();
                    int count = 0;
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getSpawn().getWorld().getName().equals(loc.getWorld().getName())) {
                            double tmpX = (1.5-(count%4))*1.5;
                            double tmpZ = ((-1.) - Math.floor(count / 4.))*1.5;
                            double tmpH = Math.hypot(tmpX, tmpZ);
                            double angle = Math.atan2(tmpZ, tmpX) + (loc.getYaw() * Math.PI / 180.);
                            fmob.setPoi(loc.getX() + tmpH*Math.cos(angle), loc.getY(), loc.getZ() + tmpH*Math.sin(angle));
                            fmob.setCommand(FactionMob.Command.poi);
                            count++;
                        }
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_FOLLOW));
                    return true;
                } else if (split[1].equalsIgnoreCase("stop")) {
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setCommand(FactionMob.Command.poi);
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_STOP));
                    return true;
                } else if (split[1].equalsIgnoreCase("moveToPoint") || split[1].equalsIgnoreCase("move") || split[1].equalsIgnoreCase("point")) {
                    if (!player.hasPermission("fmob.order.move")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                        return true;
                    }
                    Block block = player.getTargetBlock((Set<Material>) null, 64);
                    if (block == null) {
                        player.sendMessage(Messages.get(Message.FM_COMMAND_NOBLOCK));
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
                            fmob.setCommand(FactionMob.Command.poi);
                            count += 1;
                        }
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_MOVE));
                    return true;
                } else if (split[1].equalsIgnoreCase("patrolHere") || split[1].equalsIgnoreCase("patrol")) {
                    Location loc = player.getLocation();
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getSpawn().getWorld().getName().equals(loc.getWorld().getName())) {
                            fmob.setCommand(FactionMob.Command.ppoi);
                            fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
                        }
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_PATROL));
                    return true;
                } else if (split[1].equalsIgnoreCase("wander")) {
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setCommand(FactionMob.Command.wander);
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_WANDER));
                    return true;
                } else if (split[1].equalsIgnoreCase("setHome")) {
                    Location loc = player.getLocation();
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        if (fmob.getSpawn().getWorld().equals(loc.getWorld())) {
                            fmob.setCommand(FactionMob.Command.home);
                            Location spawnLoc = fmob.getSpawn();
                            spawnLoc.setX(loc.getX());
                            spawnLoc.setY(loc.getY());
                            spawnLoc.setZ(loc.getZ());
                            fmob.setPoi(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
                        }
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_SETHOME));
                    return true;
                } else if (split[1].equalsIgnoreCase("tpHome")) {
                    if (!player.hasPermission("fmob.order.tp")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                        return true;
                    }
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setCommand(FactionMob.Command.home);
                        Location loc = fmob.getSpawn();
                        fmob.getEntity().setPosition(loc.getX(), loc.getY(), loc.getZ());
                        fmob.setPoi(loc.getX(), loc.getY(), loc.getZ());
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_TPHOME));
                    return true;
                } else if (split[1].equalsIgnoreCase("tpHere")) {
                    if (!player.hasPermission("fmob.order.tp")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
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
                            fmob.setCommand(FactionMob.Command.poi);
                            count++;
                        }
                    }
                    player.sendMessage(Messages.get(Message.FM_COMMAND_TPHERE));
                    return true;
                } else if (split[1].equalsIgnoreCase("forgive")) {
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.clearAttackedBy();
                        fmob.setAttackAll(false);
                    }
                } else if (split[1].equalsIgnoreCase("attackall")) {
                    if (!player.hasPermission("fmob.order.attackall")) {
                        player.sendMessage(Messages.get(Message.FM_NOPERMISSION));
                        return true;
                    }
                    for (FactionMob fmob : plugin.playerSelections.get(player.getName())) {
                        fmob.setAttackAll(true);
                    }
                } else {
                    player.sendMessage(Messages.get(Message.FM_NOCOMMAND));
                    return true;
                }
            } else if (split[0].equalsIgnoreCase("group")) {
                if (split.length < 2) {
                    player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                    return true;
                }
                if (split[1].equalsIgnoreCase("set") || split[1].equalsIgnoreCase("save")) {
                    if (split.length < 3) {
                        player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                        return true;
                    }
                    try {
                        int index = Integer.parseInt(split[2]) - 1;
                        if (index < 0 || index >= 5) {
                            player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                            return true;
                        }
                        List<FactionMob> playerSelection = plugin.getPlayerSelection(player);
                        if (playerSelection.isEmpty()) {
                            player.sendMessage(Messages.get(Message.FM_NOSELECTION));
                            return true;
                        }
                        List<FactionMob>[] playerGroups = plugin.getPlayerGroups(player);
                        playerGroups[index] = new ArrayList<>(playerSelection);
                        player.sendMessage(Messages.get(Message.FM_GROUP_SAVE, index+1));
                    } catch (NumberFormatException e) {
                        player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                        return true;
                    }
                } else {
                    try {
                        int index = Integer.parseInt(split[1]) - 1;
                        if (index < 0 || index >= 5) {
                            player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                            return true;
                        }
                        List<FactionMob> playerSelection = plugin.getPlayerSelection(player);
                        playerSelection.clear();
                        List<FactionMob>[] playerGroups = plugin.getPlayerGroups(player);
                        if (playerGroups[index] == null) return true;
                        playerSelection.addAll(playerGroups[index]);
                        player.sendMessage(Messages.get(Message.FM_GROUP_LOAD, index+1));
                    } catch (NumberFormatException e) {
                        player.sendMessage(Messages.get(Message.FM_GROUP_USAGE));
                        return true;
                    }
                }
            } else {
                player.sendMessage(Messages.get(Message.FM_HELP));
                return true;
            }
        } else {
            sender.sendMessage("You must be a player");
            return true;
        }
        return true;
    }
}
