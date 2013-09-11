package com.gmail.scyntrus.fmob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.EntityIronGolem;
import net.minecraft.server.v1_6_R2.EntityPigZombie;
import net.minecraft.server.v1_6_R2.EntitySkeleton;
import net.minecraft.server.v1_6_R2.EntityTypes;
import net.minecraft.server.v1_6_R2.EntityZombie;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;

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
	
	public static int spawnLimit = 50;
	public static int mobsPerFaction = 0;
	public static boolean attackMobs = true;
	public static boolean noFriendlyFire = false;
	public static boolean displayMobFaction = true;
	public static boolean attackZombies = true;
	public static boolean alertAllies = true;
	
	private long saveInterval = 6000;
	
    public Economy econ = null;
	public Boolean vaultEnabled = false;
	
	public static double mobSpeed = .3;
	public static double mobPatrolSpeed = .175;
	public static double mobNavRange = 64;
	
	public static FactionMobs instance;
	
	public static boolean scheduleChunkMobLoad = false;
	public static int chunkMobLoadTask = -1;
	
	public static boolean feedEnabled = true;
	public static float feedAmount = 5;
	
	public static boolean excludeFromKillCommands = true;
	public static boolean runKeepAliveTask = true;
	
	@SuppressWarnings("unchecked")
	public void onEnable() {
		FactionMobs.instance = this;
		this.saveDefaultConfig();
		FileConfiguration config = this.getConfig();
		config.options().copyDefaults(true);
    	this.saveConfig();
    	
    	try {
    	    Class.forName("org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity");
    	} catch(Exception e) {
    	    System.out.println("[FactionMobs] You are running an unsupported version of CraftBukkit (requires 1.6.2-R0.1). FactionMobs will not be enabled.");
    	    this.getCommand("fm").setExecutor(new ErrorCommand(this));
    	    this.getCommand("fmc").setExecutor(new ErrorCommand(this));
    	    return;
    	}
    	
    	try {
    	    Class.forName("com.massivecraft.factions.entity.Faction");
    	} catch (Exception e) {
			System.out.println("[FactionMobs] You are running an unsupported version of Factions (requires 2.0.5). FactionMobs will not be enabled.");
			this.getCommand("fm").setExecutor(new ErrorCommand(this));
			this.getCommand("fmc").setExecutor(new ErrorCommand(this));
			return;
    	}
    	
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

		FactionMobs.spawnLimit = config.getInt("spawnLimit", FactionMobs.spawnLimit);
		FactionMobs.mobsPerFaction = config.getInt("mobsPerFaction", FactionMobs.mobsPerFaction);
		FactionMobs.noFriendlyFire = config.getBoolean("noFriendlyFire", FactionMobs.noFriendlyFire);
		FactionMobs.alertAllies = config.getBoolean("alertAllies", FactionMobs.alertAllies);
		FactionMobs.displayMobFaction = config.getBoolean("displayMobFaction", FactionMobs.displayMobFaction);
		FactionMobs.attackMobs = config.getBoolean("attackMobs", FactionMobs.attackMobs);
		FactionMobs.attackZombies = config.getBoolean("attackZombies", FactionMobs.attackZombies);
		FactionMobs.mobSpeed = (float) config.getDouble("mobSpeed", FactionMobs.mobSpeed);
		FactionMobs.mobPatrolSpeed = (float) config.getDouble("mobPatrolSpeed", FactionMobs.mobPatrolSpeed);
		FactionMobs.mobNavRange = (float) config.getDouble("mobNavRange", FactionMobs.mobNavRange);
		FactionMobs.excludeFromKillCommands = config.getBoolean("excludeFromKillCommands", FactionMobs.excludeFromKillCommands);
		FactionMobs.runKeepAliveTask = config.getBoolean("runKeepAliveTask", FactionMobs.runKeepAliveTask);
		if (runKeepAliveTask) excludeFromKillCommands = false;

		FactionMobs.feedEnabled = config.getBoolean("feedEnabled", FactionMobs.feedEnabled);
		FactionMobs.feedAmount = (float) config.getDouble("feedAmount", FactionMobs.feedAmount);
		
		Archer.maxHp = (float) config.getDouble("Archer.maxHp", Archer.maxHp);
		if (Archer.maxHp<1) Archer.maxHp = 1;
		Mage.maxHp = (float) config.getDouble("Mage.hp", Mage.maxHp);
		if (Mage.maxHp<1) Mage.maxHp = 1;
		Swordsman.maxHp = (float) config.getDouble("Swordsman.maxHp", Swordsman.maxHp);
		if (Swordsman.maxHp<1) Swordsman.maxHp = 1;
		Titan.maxHp = (float) config.getDouble("Titan.maxHp", Titan.maxHp);
		if (Titan.maxHp<1) Titan.maxHp = 1;
		
		Archer.damage = config.getInt("Archer.damage", Archer.damage);
		if (Archer.damage<0) Archer.damage = 0;
		Swordsman.damage = config.getInt("Swordsman.damage", Swordsman.damage);
		if (Swordsman.damage<0) Swordsman.damage = 0;
		Titan.damage = config.getInt("Titan.damage", Titan.damage);
		if (Titan.damage<0) Titan.damage = 0;
		
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

		Archer.drops = config.getInt("Archer.drops", 0);
		Mage.drops = config.getInt("Mage.drops", 0);
		Swordsman.drops = config.getInt("Swordsman.drops", 0);
		Titan.drops = config.getInt("Titan.drops", 0);
		
		this.pm = this.getServer().getPluginManager();
		
		if (!ReflectionManager.init()) {
        	this.getLogger().severe("[Fatal Error] Unable to register mobs");
	    	pm.disablePlugin(this);
			return;
		}
		
	    try {
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, Archer.class, Archer.typeName, modelNum);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, Swordsman.class, Swordsman.typeName, modelNum);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, Mage.class, Mage.typeName, modelNum);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, Titan.class, Titan.typeName, 99);
	    	
	    	//Make sure I don't override original classes
	    	
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, EntitySkeleton.class, "Skeleton", 51);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, EntityZombie.class, "Zombie", 54);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, EntityPigZombie.class, "PigZombie", 57);
	    	ReflectionManager.entityTypesA.invoke(EntityTypes.class, EntityIronGolem.class, "VillagerGolem", 99);
	    } catch (Exception e) {
        	this.getLogger().severe("[Fatal Error] Unable to register mobs");
        	e.printStackTrace();
	    	pm.disablePlugin(this);
	    	return;
	    }
	    
	    this.getCommand("fm").setExecutor(new FmCommand(this));
	    if (config.getBoolean("fmcEnabled", false)) {
		    this.getCommand("fmc").setExecutor(new FmcCommand(this));
	    }
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
	        	this.getLogger().severe("[FactionMobs] Error reading faction colors file, colors.dat");
			}
	    }
	    
	    if (config.getBoolean("autoSave", false)) {
	    	this.saveInterval = config.getLong("saveInterval", this.saveInterval);
	    	if (this.saveInterval > 0) {
	    		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSaver(this), this.saveInterval, this.saveInterval);
	    		System.out.println("[FactionMobs] Auto-Save enabled.");
	    	}
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
        	System.out.println("[FactionMobs] Vault detected.");
        } else {
        	System.out.println("[FactionMobs] Vault not detected.");
        }
        
		try { // using mcstats.org metrics
			MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (IOException e) {
            System.out.println("[Metrics] " + e.getMessage());
		}
        
		this.loadMobList();
		if (runKeepAliveTask) this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new DeadChecker(this), 1, 1);
        chunkMobLoadTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ChunkMobLoader(this), 4, 4);
	}
	
	public void onDisable() {
		this.saveMobList();
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
				org.bukkit.World world = this.getServer().getWorld(mobData.get(1));
				if (world == null) {
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
				Faction faction = FactionColls.get().getForWorld(mobData.get(1)).getByName(mobData.get(2));
				if (faction == null) {
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
				Location spawnLoc = new Location(
						world, 
						Double.parseDouble(mobData.get(3)), 
						Double.parseDouble(mobData.get(4)), 
						Double.parseDouble(mobData.get(5)));
				if (mobData.get(0).equalsIgnoreCase("Archer") || mobData.get(0).equalsIgnoreCase("Ranger")) {
					newMob = new Archer(spawnLoc, faction);
				} else if (mobData.get(0).equalsIgnoreCase("Mage")) {
					newMob = new Mage(spawnLoc, faction);
				} else if (mobData.get(0).equalsIgnoreCase("Swordsman")) {
					newMob = new Swordsman(spawnLoc, faction);
				} else if (mobData.get(0).equalsIgnoreCase("Titan")) {
					newMob = new Titan(spawnLoc, faction);
				} else {
					continue;
				}
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
				newMob.getEntity().setPosition(Double.parseDouble(mobData.get(6)),
						Double.parseDouble(mobData.get(7)),
						Double.parseDouble(mobData.get(8)));
				newMob.getEntity().setHealth(Float.parseFloat(mobData.get(9)));
				
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
				
				newMob.getEntity().world.addEntity((Entity) newMob, SpawnReason.CUSTOM);
				mobList.add(newMob);
				newMob.getEntity().dead = false;
			}
	    }
	}
	
	public void saveMobList() {
		YamlConfiguration conf = new YamlConfiguration();
		List<List<String>> save = new ArrayList<List<String>>();
		for (FactionMob fmob : mobList) {
			if (fmob.getFaction() == null) {
				continue;
			}
			List<String> mobData = new ArrayList<String>();
			mobData.add(fmob.getTypeName()); //0
			Location spawnLoc = fmob.getSpawn();
			mobData.add(spawnLoc.getWorld().getName()); //1
			mobData.add(fmob.getFactionName()); //2
			mobData.add(""+spawnLoc.getX()); //3
			mobData.add(""+spawnLoc.getY());
			mobData.add(""+spawnLoc.getZ());
			mobData.add(""+fmob.getlocX()); //6
			mobData.add(""+fmob.getlocY());
			mobData.add(""+fmob.getlocZ());
			mobData.add(""+fmob.getEntity().getHealth()); //9
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
		for (int i = mobList.size()-1; i >= 0; i--) {
			mobList.get(i).updateMob();
		}
	}
}
