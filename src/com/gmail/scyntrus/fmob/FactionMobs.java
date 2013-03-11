package com.gmail.scyntrus.fmob;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_4_R1.EntityTypes;
import net.minecraft.server.v1_4_R1.World;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Ranger;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.massivecraft.factions.Factions;


public class FactionMobs extends JavaPlugin{
	
	public PluginManager pm = null;
	public List<FactionMob> mobList = new ArrayList<FactionMob>();
	
	public void onEnable() {
		this.pm = this.getServer().getPluginManager();
	    try {
	    	Method method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Archer.class, "Archer", 51);
	    	
	    	method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Ranger.class, "Ranger", 51);

	    	method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Swordsman.class, "Swordsman", 51);

	    	method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Titan.class, "Titan", 99);

	    	method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Mage.class, "Mage", 51);
	    } catch (Exception e) {
	    	pm.disablePlugin(this);
	    	return;
	    }
	    this.getCommand("fmspawn").setExecutor(new SpawnCommand(this));
	    this.pm.registerEvents(new EntityListener(this), this);
	    File file = new File(getDataFolder(), "data.yml");
	    if (file.exists()) {
	    	YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
			@SuppressWarnings("unchecked")
			List<List<String>> save = (List<List<String>>) conf.getList("data");
			for (List<String> mobData : save) {
				if (mobData.get(0).equalsIgnoreCase("archer")) {
					World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
					Archer newMob = new Archer(world);
					newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
					newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
							Double.parseDouble(mobData.get(3)), 
							Double.parseDouble(mobData.get(4)), 
							Double.parseDouble(mobData.get(5))));
					newMob.locX = Double.parseDouble(mobData.get(6));
					newMob.locY = Double.parseDouble(mobData.get(7));
					newMob.locZ = Double.parseDouble(mobData.get(8));
					newMob.setHealth(Integer.parseInt(mobData.get(9)));
					world.addEntity(newMob, SpawnReason.CUSTOM);
					this.mobList.add(newMob);
				} else if (mobData.get(0).equalsIgnoreCase("mage")) {
					World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
					Mage newMob = new Mage(world);
					newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
					newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
							Double.parseDouble(mobData.get(3)), 
							Double.parseDouble(mobData.get(4)), 
							Double.parseDouble(mobData.get(5))));
					newMob.locX = Double.parseDouble(mobData.get(6));
					newMob.locY = Double.parseDouble(mobData.get(7));
					newMob.locZ = Double.parseDouble(mobData.get(8));
					newMob.setHealth(Integer.parseInt(mobData.get(9)));
					world.addEntity(newMob, SpawnReason.CUSTOM);
					this.mobList.add(newMob);
				} else if (mobData.get(0).equalsIgnoreCase("ranger")) {
					World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
					Ranger newMob = new Ranger(world);
					newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
					newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
							Double.parseDouble(mobData.get(3)), 
							Double.parseDouble(mobData.get(4)), 
							Double.parseDouble(mobData.get(5))));
					newMob.locX = Double.parseDouble(mobData.get(6));
					newMob.locY = Double.parseDouble(mobData.get(7));
					newMob.locZ = Double.parseDouble(mobData.get(8));
					newMob.setHealth(Integer.parseInt(mobData.get(9)));
					world.addEntity(newMob, SpawnReason.CUSTOM);
					this.mobList.add(newMob);
				} else if (mobData.get(0).equalsIgnoreCase("swordsman")) {
					World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
					Swordsman newMob = new Swordsman(world);
					newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
					newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
							Double.parseDouble(mobData.get(3)), 
							Double.parseDouble(mobData.get(4)), 
							Double.parseDouble(mobData.get(5))));
					newMob.locX = Double.parseDouble(mobData.get(6));
					newMob.locY = Double.parseDouble(mobData.get(7));
					newMob.locZ = Double.parseDouble(mobData.get(8));
					newMob.setHealth(Integer.parseInt(mobData.get(9)));
					world.addEntity(newMob, SpawnReason.CUSTOM);
					this.mobList.add(newMob);
				} else if (mobData.get(0).equalsIgnoreCase("titan")) {
					World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
					Titan newMob = new Titan(world);
					newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
					newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
							Double.parseDouble(mobData.get(3)), 
							Double.parseDouble(mobData.get(4)), 
							Double.parseDouble(mobData.get(5))));
					newMob.locX = Double.parseDouble(mobData.get(6));
					newMob.locY = Double.parseDouble(mobData.get(7));
					newMob.locZ = Double.parseDouble(mobData.get(8));
					newMob.setHealth(Integer.parseInt(mobData.get(9)));
					world.addEntity(newMob, SpawnReason.CUSTOM);
					this.mobList.add(newMob);
				}
			}
	    }
	}
	
	public void onDisable() {
		saveMobList();
	}
	
	public void saveMobList() {
		YamlConfiguration conf = new YamlConfiguration();
		List<List<String>> save = new ArrayList<List<String>>();
		for (FactionMob fmob : this.mobList) {
			List<String> mobData = new ArrayList<String>();
			mobData.add(fmob.getTypeName()); //0
			Location spawnLoc = fmob.getSpawn();
			mobData.add(spawnLoc.getWorld().getName()); //1
			mobData.add(fmob.getFaction().getTag()); //2
			mobData.add(""+spawnLoc.getX()); //3
			mobData.add(""+spawnLoc.getY());
			mobData.add(""+spawnLoc.getZ());
			mobData.add(""+fmob.getlocX()); //6
			mobData.add(""+fmob.getlocY());
			mobData.add(""+fmob.getlocZ());
			mobData.add(""+fmob.getHealth()); //9
			save.add(mobData);
			fmob.die();
		}
		conf.set("data", save);
		try {
			conf.save(new File(getDataFolder(), "data.yml"));
		} catch (IOException e) {
			System.out.println("Failed to save fmob data");
		}
	}
}
