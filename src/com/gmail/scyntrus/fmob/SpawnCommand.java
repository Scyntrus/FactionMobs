package com.gmail.scyntrus.fmob;

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

public class SpawnCommand  implements CommandExecutor{

	FactionMobs plugin;
	
	public SpawnCommand(FactionMobs plugin) {
		this.plugin = plugin;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (sender instanceof Player) {
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
			net.minecraft.server.v1_4_R1.World world = ((CraftWorld)player.getWorld()).getHandle();
			if (split.length == 0) {
				player.sendMessage("You must specify a mob");
				return true;
			} else if (split[0].equalsIgnoreCase("archer")) {
				Archer newMob = new Archer(world);
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				world.addEntity(newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned an archer");
			} else if (split[0].equalsIgnoreCase("swordsman")) {
				Swordsman newMob = new Swordsman(world);
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				world.addEntity(newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned a swordsman");
			} else if (split[0].equalsIgnoreCase("ranger")) {
				Ranger newMob = new Ranger(world);
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				world.addEntity(newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned a ranger");
			} else if (split[0].equalsIgnoreCase("titan")) {
				Titan newMob = new Titan(world);
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				world.addEntity(newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned a titan");
			} else if (split[0].equalsIgnoreCase("mage")) {
				Mage newMob = new Mage(world);
				newMob.setSpawn(player.getLocation());
				newMob.setFaction(playerfaction);
				world.addEntity(newMob, SpawnReason.CUSTOM);
				plugin.mobList.add(newMob);
				player.sendMessage("You have spawned a mage");
			} else {
				player.sendMessage("Unrecognized mob name");
				return true;
			}
		}
		return true;
	}
}
