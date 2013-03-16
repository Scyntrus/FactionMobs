package com.gmail.scyntrus.fmob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityIronGolem;
import net.minecraft.server.v1_5_R1.EntityPigZombie;
import net.minecraft.server.v1_5_R1.EntitySkeleton;
import net.minecraft.server.v1_5_R1.EntityTypes;
import net.minecraft.server.v1_5_R1.EntityZombie;
import net.minecraft.server.v1_5_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_5_R1.MinecraftServer;
import net.minecraft.server.v1_5_R1.World;
import net.minecraft.server.v1_5_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_5_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.massivecraft.factions.Factions;


public class FactionMobs extends JavaPlugin {
	
	public PluginManager pm = null;
	public static List<FactionMob> mobList = new ArrayList<FactionMob>();
	public static Map<String,Integer> factionColors = new HashMap<String,Integer>();
	
	public Map<String,Boolean> mobLeader = new HashMap<String,Boolean>();
	
	public Map<String,List<FactionMob>> playerSelections = new HashMap<String,List<FactionMob>>();
	
	public static long mobCount = 0;
	
	public static String sndBreath = "";
	public static String sndHurt = "";
	public static String sndDeath = "";
	public static String sndStep = "";
	
	public int spawnLimit = 50;
	public static boolean attackMobs = true;
	public boolean noFriendlyFire = false;
	
	private int saveInterval = 10;
	
    public Economy econ = null;
	public Boolean vaultEnabled = false;
	
	public static float mobSpeed = .3F;
	public static float mobPatrolSpeed = .175F;
	public static float mobNavRange = 64F;
	
	@SuppressWarnings("unchecked")
	public void onEnable() {
		this.saveDefaultConfig();
		FileConfiguration config = this.getConfig();
		config.options().copyDefaults(true);
    	this.saveConfig();
		int modelNum = 51;
		switch (config.getInt("model")) {
		case 0: // skeleton
			modelNum = 51;
			//FactionMobs.sndBreath = "mob.skeleton.say";
			FactionMobs.sndHurt = "mob.skeleton.hurt";
			FactionMobs.sndDeath = "mob.skeleton.death";
			FactionMobs.sndStep = "mob.skeleton.step";
			break;
		case 1: // zombie
			modelNum = 54;
			//FactionMobs.sndBreath = "mob.zombie.say";
			FactionMobs.sndHurt = "mob.zombie.hurt";
			FactionMobs.sndDeath = "mob.zombie.death";
			FactionMobs.sndStep = "mob.zombie.step";
			break;
		case 2: // pigzombie
			modelNum = 57;
			//FactionMobs.sndBreath = "mob.zombiepig.zpig";
			FactionMobs.sndHurt = "mob.zombiepig.zpighurt";
			FactionMobs.sndDeath = "mmob.zombiepig.zpigdeath";
			FactionMobs.sndStep = "mob.zombie.step";
			break;
		}

		this.spawnLimit = config.getInt("spawnLimit", this.spawnLimit);
		this.noFriendlyFire = config.getBoolean("noFriendlyFire", this.noFriendlyFire);
		FactionMobs.attackMobs = config.getBoolean("attackMobs", FactionMobs.attackMobs);
		FactionMobs.mobSpeed = (float) config.getDouble("mobSpeed", FactionMobs.mobSpeed);
		FactionMobs.mobPatrolSpeed = (float) config.getDouble("mobPatrolSpeed", FactionMobs.mobPatrolSpeed);
		FactionMobs.mobNavRange = (float) config.getDouble("mobNavRange", FactionMobs.mobNavRange);
		
		Archer.maxHp = config.getInt("Archer.maxHp", Archer.maxHp);
		if (Archer.maxHp<1) Archer.maxHp = 1;
		Mage.maxHp = config.getInt("Mage.hp", Mage.maxHp);
		if (Mage.maxHp<1) Mage.maxHp = 1;
		Swordsman.maxHp = config.getInt("Swordsman.maxHp", Swordsman.maxHp);
		if (Swordsman.maxHp<1) Swordsman.maxHp = 1;
		Titan.maxHp = config.getInt("Titan.maxHp", Titan.maxHp);
		if (Titan.maxHp<1) Titan.maxHp = 1;
		
		Archer.enabled = config.getBoolean("Archer.enabled", Archer.enabled);
		Mage.enabled = config.getBoolean("Mage.enabled", Mage.enabled);
		Swordsman.enabled = config.getBoolean("Swordsman.enabled", Swordsman.enabled);
		Titan.enabled = config.getBoolean("Titan.enabled", Titan.enabled);
		
		Archer.powerCost = config.getDouble("Archer.powerCost", Archer.powerCost);
		Archer.moneyCost = config.getDouble("Archer.moneyCost", Archer.moneyCost);
		Mage.powerCost = config.getDouble("Mage.powerCost", Mage.powerCost);
		Mage.moneyCost = config.getDouble("Mage.moneyCost", Mage.moneyCost);
		Swordsman.powerCost = config.getDouble("Swordsman.powerCost", Swordsman.powerCost);
		Swordsman.moneyCost = config.getDouble("Swordsman.moneyCost", Swordsman.moneyCost);
		Titan.powerCost = config.getDouble("Titan.powerCost", Titan.powerCost);
		Titan.moneyCost = config.getDouble("Titan.moneyCost", Titan.moneyCost);
		
		this.pm = this.getServer().getPluginManager();
	    try {
	    	Method method = EntityTypes.class.getDeclaredMethod("a", new Class[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Archer.class, Archer.typeName, modelNum);

	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Swordsman.class, Swordsman.typeName, modelNum);

	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Mage.class, Mage.typeName, modelNum);

	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, Titan.class, Titan.typeName, 99);
	    	
	    	//Make sure I don't override original classes
	    	
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, EntitySkeleton.class, "Skeleton", 51);
	    	
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, EntityZombie.class, "Zombie", 54);
	    	
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, EntityPigZombie.class, "PigZombie", 57);
	    	
	    	method.setAccessible(true);
	    	method.invoke(EntityTypes.class, EntityIronGolem.class, "VillagerGolem", 99);
	    	
	    } catch (Exception e) {
        	this.getLogger().severe("[Fatal Error] Unable to register mobs");
	    	pm.disablePlugin(this);
	    	return;
	    }
	    this.getCommand("fm").setExecutor(new FmCommand(this));
	    this.pm.registerEvents(new EntityListener(this), this);
	    this.pm.registerEvents(new CommandListener(this), this);
	    File colorFile = new File(getDataFolder(), "colors.dat");
	    if (colorFile.exists()){
			try {
				FileInputStream fileInputStream = new FileInputStream(colorFile);
		    	ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
		    	FactionMobs.factionColors = (HashMap<String, Integer>) oInputStream.readObject();
		    	oInputStream.close();
		    	fileInputStream.close();
			} catch (Exception e) {
	        	this.getLogger().severe("Error reading faction colors file, colors.dat");
			}
	    }
	    
	    if (config.getBoolean("autoSave", false)) {
	    	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSaver(this), this.saveInterval * 1200L, this.saveInterval * 1200L);
	    }
	    
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
                if (econ != null) {
                	vaultEnabled = true;
                }
            }
        }
        if (vaultEnabled) {
        	System.out.println("Vault detected.");
        } else {
        	System.out.println("Vault not detected.");
        }
        
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new MobLoader(this), 5L);
	}
	
	public void onDisable() {
		this.saveMobList();
		for (FactionMob fmob : mobList) {
			fmob.setHealth(0);
			fmob.die();
			fmob.getEntity().world.removeEntity(fmob.getEntity());
		}
		MinecraftServer localMinecraftServer = MinecraftServer.getServer();
		try {
			for (int i = 0; i < localMinecraftServer.worldServer.length; i++)
				if (localMinecraftServer.worldServer[i] != null) {
					WorldServer localWorldServer = localMinecraftServer.worldServer[i];
					boolean bool = localWorldServer.savingDisabled;
					localWorldServer.savingDisabled = false;
					localWorldServer.save(true, null);
					localWorldServer.savingDisabled = bool;
				}
			}
		catch (ExceptionWorldConflict localExceptionWorldConflict) {
		}
	}
	
	public void loadMobList() {
		File file = new File(getDataFolder(), "data.dat");
	    boolean backup = false;
	    if (file.exists()) {
	    	YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
			@SuppressWarnings("unchecked")
			List<List<String>> save = (List<List<String>>) conf.getList("data", new ArrayList<List<String>>());
			for (List<String> mobData : save) {
				FactionMob newMob = null;
				if (mobData.size() < 10) {
					System.out.println("Incomplete Faction Mob found and removed. Did you delete or rename a world?");
					if (!backup) {
						backup = true;
						try {
							conf.save(new File(getDataFolder(), "data_backup.dat"));
							System.out.println("Backup file saved as data_backup.dat");
						} catch (IOException e) {
							System.out.println("Failed to save backup file");
						}
					}
					continue;
				}
				if (this.getServer().getWorld(mobData.get(1)) == null) {
					System.out.println("Worldless Faction Mob found and removed. Did you delete or rename a world?");
					if (!backup) {
						backup = true;
						try {
							conf.save(new File(getDataFolder(), "data_backup.dat"));
							System.out.println("Backup file saved as data_backup.dat");
						} catch (IOException e) {
							System.out.println("Failed to save backup file");
						}
					}
					continue;
				}
				if (Factions.i.getByTag(mobData.get(2)) == null) {
					System.out.println("Factionless Faction Mob found and removed. Did something happen to Factions?");
					if (!backup) {
						backup = true;
						try {
							conf.save(new File(getDataFolder(), "data_backup.dat"));
							System.out.println("Backup file saved as data_backup.dat");
						} catch (IOException e) {
							System.out.println("Failed to save backup file");
						}
					}
					continue;
				}
				World world = ((CraftWorld) this.getServer().getWorld(mobData.get(1))).getHandle();
				if (mobData.get(0).equalsIgnoreCase("Archer") || mobData.get(0).equalsIgnoreCase("Ranger")) {
					newMob = new Archer(world);
				} else if (mobData.get(0).equalsIgnoreCase("Mage")) {
					newMob = new Mage(world);
				} else if (mobData.get(0).equalsIgnoreCase("Swordsman")) {
					newMob = new Swordsman(world);
				} else if (mobData.get(0).equalsIgnoreCase("Titan")) {
					newMob = new Titan(world);
				} else {
					continue;
				}
				newMob.setFaction(Factions.i.getByTag(mobData.get(2)));
				newMob.setFactionName(mobData.get(2));
				if (newMob.getFaction() == null || newMob.getFactionName() == null) {
					System.out.println("Factionless Faction Mob found and removed. Did something happen to Factions?");
					if (!backup) {
						backup = true;
						try {
							conf.save(new File(getDataFolder(), "data_backup.dat"));
							System.out.println("Backup file saved as data_backup.dat");
						} catch (IOException e) {
							System.out.println("Failed to save backup file");
						}
					}
					continue;
				}
				newMob.setSpawn(new Location(this.getServer().getWorld(mobData.get(1)), 
						Double.parseDouble(mobData.get(3)), 
						Double.parseDouble(mobData.get(4)), 
						Double.parseDouble(mobData.get(5))));
				newMob.setPosition(Double.parseDouble(mobData.get(6)),
						Double.parseDouble(mobData.get(7)),
						Double.parseDouble(mobData.get(8)));
				newMob.setHealth(Integer.parseInt(mobData.get(9)));
				Utils.giveColorArmor(newMob);
				
				if (mobData.size() > 10) {
					newMob.setPoi(
						Double.parseDouble(mobData.get(10)), 
						Double.parseDouble(mobData.get(11)), 
						Double.parseDouble(mobData.get(12)));
					newMob.setOrder(mobData.get(13));
				} else {
					newMob.setPoi(
							Double.parseDouble(mobData.get(6)), 
							Double.parseDouble(mobData.get(7)), 
							Double.parseDouble(mobData.get(8)));
					newMob.setOrder("poi");
				}
				
				world.addEntity((Entity) newMob, SpawnReason.CUSTOM);
				mobList.add(newMob);
			}
	    }
	}
	
	public void saveMobList() {
		YamlConfiguration conf = new YamlConfiguration();
		List<List<String>> save = new ArrayList<List<String>>();
		for (FactionMob fmob : mobList) {
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
			mobData.add(""+fmob.getPoiX()); //10
			mobData.add(""+fmob.getPoiY());
			mobData.add(""+fmob.getPoiZ());
			mobData.add(fmob.getOrder()); //13
			save.add(mobData);
		}
		conf.set("data", save);
		try {
			conf.save(new File(getDataFolder(), "data.dat"));
			System.out.println("FactionMobs data saved.");
		} catch (IOException e) {
        	this.getLogger().severe("Failed to save faction mob data, data.dat");
		}
		try {
		    File colorFile = new File(getDataFolder(), "colors.dat");
		    colorFile.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(colorFile);
	    	ObjectOutputStream oOut = new ObjectOutputStream(fileOut);
	    	oOut.writeObject(FactionMobs.factionColors);
	    	oOut.close();
	    	fileOut.close();
			System.out.println("FactionMobs color data saved.");
		} catch (Exception e) {
        	this.getLogger().severe("Error writing faction colors file, colors.dat");
		}
		
	}
	
	public void updateList() {
		List<FactionMob> toDelete = new ArrayList<FactionMob>();
		for (FactionMob fmob : mobList) {
			if ((!fmob.isAlive())
					|| (fmob.getFaction() == null)
					|| fmob.getFaction().isNone()
					|| (Factions.i.getByTag(fmob.getFaction().getTag()) == null)) {
				toDelete.add(fmob);
			} else {
				fmob.updateMob();
				Utils.giveColorArmor(fmob);
			}
		}
		for (FactionMob fmob : toDelete) {
			mobList.remove(fmob);
			fmob.die();
		}
	}
}
