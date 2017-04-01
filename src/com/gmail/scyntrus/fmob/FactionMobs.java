package com.gmail.scyntrus.fmob;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.SpiritBear;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

public class FactionMobs extends JavaPlugin {

    public PluginManager pm = null;
    public static Set<FactionMob> mobList = new HashSet<FactionMob>();
    public static Map<String,Integer> factionColors = new HashMap<String,Integer>();
    public Set<String> mobLeader = new HashSet<String>();
    public Map<String,List<FactionMob>> playerSelections = new HashMap<String,List<FactionMob>>();
    public Map<String,List<FactionMob>[]> playerGroups = new HashMap<>();
    public static long mobCount = 0;
    public static int mobsPerFaction = 0;
    public static boolean attackMobs = true;
    public static boolean noFriendlyFire = false;
    public static boolean noPlayerFriendlyFire = false;
    public static boolean displayMobFaction = true;
    public static boolean equipArmor = true;
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
    public static int feedItem = 260;
    public static float feedAmount = 5;
    public static boolean silentErrors = true;
    private static String minRankToSpawnStr = "MEMBER";
    public static Rank minRankToSpawn;
    public static boolean onlySpawnInTerritory = true;
    public static final Random random = new Random();
    public static final int responseTime = 20;
    public static double agroRange = 16;
    public static boolean disguiseEnabled = false;
    public static String playerSkin = null;

    @Override
    public void onEnable() {
        FactionMobs.instance = this;
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        FactionMobs.silentErrors = config.getBoolean("silentErrors");
        ErrorManager.initErrorStream();
        Messages.init(this);

        try {
            VersionManager.checkVersion();
        } catch (VersionManager.VersionException e) {
            ErrorManager.handleError(e.getMessage(), e);
            this.getCommand("fm").setExecutor(new ErrorCommand(this));
            this.getCommand("fmc").setExecutor(new ErrorCommand(this));
            return;
        }

        Utils.copyDefaultConfig();

        if (!FactionsManager.init(this)) {
            ErrorManager.handleError("You are running an unsupported version of Factions. Please contact the plugin author for more info.");
            this.getCommand("fm").setExecutor(new ErrorCommand(this));
            this.getCommand("fmc").setExecutor(new ErrorCommand(this));
            return;
        }

        FactionMobs.mobsPerFaction = config.getInt("mobsPerFaction");
        FactionMobs.noFriendlyFire = config.getBoolean("noFriendlyFire");
        FactionMobs.noPlayerFriendlyFire = config.getBoolean("noPlayerFriendlyFire");
        FactionMobs.alertAllies = config.getBoolean("alertAllies");
        FactionMobs.displayMobFaction = config.getBoolean("displayMobFaction");
        FactionMobs.equipArmor = config.getBoolean("equipArmor");
        FactionMobs.attackMobs = config.getBoolean("attackMobs");
        FactionMobs.mobSpeed = (float) config.getDouble("mobSpeed");
        FactionMobs.mobPatrolSpeed = (float) config.getDouble("mobPatrolSpeed");
        FactionMobs.mobPatrolSpeed = FactionMobs.mobPatrolSpeed / FactionMobs.mobSpeed;
        FactionMobs.mobNavRange = (float) config.getDouble("mobNavRange");
        FactionMobs.feedEnabled = config.getBoolean("feedEnabled");
        FactionMobs.feedItem = config.getInt("feedItem");
        FactionMobs.feedAmount = (float) config.getDouble("feedAmount");
        FactionMobs.minRankToSpawnStr = config.getString("minRankToSpawn");
        FactionMobs.minRankToSpawn = Rank.getByName(FactionMobs.minRankToSpawnStr);
        FactionMobs.onlySpawnInTerritory = config.getBoolean("onlySpawnInTerritory");

        Archer.maxHp = (float) config.getDouble("Archer.maxHp");
        if (Archer.maxHp<1) Archer.maxHp = 1;
        Mage.maxHp = (float) config.getDouble("Mage.maxHp");
        if (Mage.maxHp<1) Mage.maxHp = 1;
        Swordsman.maxHp = (float) config.getDouble("Swordsman.maxHp");
        if (Swordsman.maxHp<1) Swordsman.maxHp = 1;
        Titan.maxHp = (float) config.getDouble("Titan.maxHp");
        if (Titan.maxHp<1) Titan.maxHp = 1;
        SpiritBear.maxHp = (float) config.getDouble("SpiritBear.maxHp");
        if (SpiritBear.maxHp<1) SpiritBear.maxHp = 1;

        Archer.damage = config.getDouble("Archer.damage");
        if (Archer.damage<0) Archer.damage = 0;
        Swordsman.damage = config.getDouble("Swordsman.damage");
        if (Swordsman.damage<0) Swordsman.damage = 0;
        Titan.damage = config.getDouble("Titan.damage");
        if (Titan.damage<0) Titan.damage = 0;
        SpiritBear.damage = config.getDouble("SpiritBear.damage");
        if (SpiritBear.damage<0) SpiritBear.damage = 0;

        Archer.enabled = config.getBoolean("Archer.enabled");
        Mage.enabled = config.getBoolean("Mage.enabled");
        Swordsman.enabled = config.getBoolean("Swordsman.enabled");
        Titan.enabled = config.getBoolean("Titan.enabled");
        SpiritBear.enabled = config.getBoolean("SpiritBear.enabled");

        Archer.powerCost = config.getDouble("Archer.powerCost");
        Archer.moneyCost = config.getDouble("Archer.moneyCost");
        Mage.powerCost = config.getDouble("Mage.powerCost");
        Mage.moneyCost = config.getDouble("Mage.moneyCost");
        Swordsman.powerCost = config.getDouble("Swordsman.powerCost");
        Swordsman.moneyCost = config.getDouble("Swordsman.moneyCost");
        Titan.powerCost = config.getDouble("Titan.powerCost");
        Titan.moneyCost = config.getDouble("Titan.moneyCost");
        SpiritBear.powerCost = config.getDouble("SpiritBear.powerCost");
        SpiritBear.moneyCost = config.getDouble("SpiritBear.moneyCost");

        Archer.drops = config.getInt("Archer.drops");
        Mage.drops = config.getInt("Mage.drops");
        Swordsman.drops = config.getInt("Swordsman.drops");
        Titan.drops = config.getInt("Titan.drops");
        SpiritBear.drops = config.getInt("SpiritBear.drops");

        Archer.localizedName = Messages.get(Messages.Message.NAME_ARCHER);
        Swordsman.localizedName = Messages.get(Messages.Message.NAME_SWORDSMAN);
        Mage.localizedName = Messages.get(Messages.Message.NAME_MAGE);
        Titan.localizedName = Messages.get(Messages.Message.NAME_TITAN);
        SpiritBear.localizedName = Messages.get(Messages.Message.NAME_SPIRITBEAR);

        this.pm = this.getServer().getPluginManager();
        if (!ReflectionManager.init()) {
            this.getLogger().severe("[Fatal Error] Unable to access native code.");
            this.getCommand("fm").setExecutor(new ErrorCommand(this));
            this.getCommand("fmc").setExecutor(new ErrorCommand(this));
            return;
        }
        try {
            addEntityType(Archer.class, Archer.typeName, 51);
            addEntityType(Swordsman.class, Swordsman.typeName, 51);
            addEntityType(Mage.class, Mage.typeName, 66);
            addEntityType(Titan.class, Titan.typeName, 99);
            addEntityType(SpiritBear.class, SpiritBear.typeName, 102);
        } catch (Exception e) {
            this.getLogger().severe("[Fatal Error] Unable to register mobs");
            this.getCommand("fm").setExecutor(new ErrorCommand(this));
            this.getCommand("fmc").setExecutor(new ErrorCommand(this));
            return;
        }

        this.getCommand("fm").setExecutor(new FmCommand(this));
        if (config.getBoolean("fmcEnabled")) {
            this.getCommand("fmc").setExecutor(new FmcCommand(this));
        }

        this.pm.registerEvents(new EntityListener(this), this);
        this.pm.registerEvents(new CommandListener(this), this);

        File colorFile = new File(getDataFolder(), "colors.dat");
        if (colorFile.exists()){
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(colorFile);
                ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
                @SuppressWarnings("unchecked")
                Map<String, Integer> colorMap = (Map<String, Integer>) oInputStream.readObject();
                for (Iterator<Map.Entry<String, Integer>> it = colorMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, Integer> pair = it.next();
                    if (pair.getValue() > 16777215 || pair.getValue() < 0) {
                        it.remove();
                    }
                }
                FactionMobs.factionColors = colorMap;
                oInputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                ErrorManager.handleError("Error reading faction colors file, colors.dat.", e);
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

        if (getServer().getPluginManager().getPlugin("LibsDisguises") != null) {
            disguiseEnabled = true;
            System.out.println("[FactionMobs] LibsDisguises detected. Units will have player models.");
            FactionMobs.playerSkin = config.getString("disguiseSkin", "").trim();
            DisguiseConnector.initPlayerDisguise();
        }

        runMetrics(); // using mcstats.org metrics

        this.loadMobList();

        chunkMobLoadTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ChunkMobLoader(this), 4, 4);
    }

    private void addEntityType(Class<? extends net.minecraft.server.v1_11_R1.Entity> entityClass, String entityName, int entityId) {
        EntityTypes.b.a(entityId, new MinecraftKey(entityName), entityClass); // TODO: Update name on version change (RegistryMaterials.add)
    }

    private void runMetrics() {
        try {
            String factionsVersion = FactionsManager.getVersionString();
            String factionMobsVersion = this.getDescription().getVersion();
            Metrics metrics = new Metrics(this);

            Graph versionGraph = metrics.createGraph("Factions Version");

            versionGraph.addPlotter(new Metrics.Plotter(factionsVersion) {

                @Override
                public int getValue() {
                    return 1;
                }

            });

            Graph versionComboGraph = metrics.createGraph("Version Combination");

            versionComboGraph.addPlotter(new Metrics.Plotter(factionMobsVersion+":"+factionsVersion) {

                @Override
                public int getValue() {
                    return 1;
                }

            });

            metrics.start();
        } catch (Exception e) {
            ErrorManager.handleError("Metrics failed to start", e);
        }
    }

    @Override
    public void onDisable() {
        this.saveMobList();
        for (FactionMob fmob : mobList) {
            fmob.forceDie();
        }
        mobList.clear();
        ErrorManager.closeErrorStream();
    }

    public void loadMobList() {
        File file = new File(getDataFolder(), "data.dat");
        boolean backup = false;
        if (file.exists()) {
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
            @SuppressWarnings("unchecked")
            List<List<String>> save = (List<List<String>>) conf.getList("data", null);
            if (save == null) {
                ErrorManager.handleError("data.dat is empty");
                return;
            }
            for (List<String> mobData : save) {
                FactionMob newMob;
                if (mobData.size() < 10) {
                    System.out.println("Incomplete Faction Mob found and removed. Did you edit the data.dat file?");
                    backup = true;
                    continue;
                }
                org.bukkit.World world = this.getServer().getWorld(mobData.get(1));
                if (world == null) {
                    System.out.println("Worldless Faction Mob found and removed. Did you delete or rename a world?");
                    backup = true;
                    continue;
                }
                Faction faction = FactionsManager.getFactionByName(mobData.get(2));
                if (faction == null || faction.isNone()) {
                    System.out.println("Factionless Faction Mob found and removed. Did you delete a Faction?");
                    backup = true;
                    continue;
                }
                Location spawnLoc = new Location(
                        world,
                        Double.parseDouble(mobData.get(3)),
                        Double.parseDouble(mobData.get(4)),
                        Double.parseDouble(mobData.get(5)));
                if (mobData.get(0).equalsIgnoreCase(Archer.typeName) || mobData.get(0).equalsIgnoreCase("Ranger")) {
                    newMob = new Archer(spawnLoc, faction);
                } else if (mobData.get(0).equalsIgnoreCase(Mage.typeName)) {
                    newMob = new Mage(spawnLoc, faction);
                } else if (mobData.get(0).equalsIgnoreCase(Swordsman.typeName)) {
                    newMob = new Swordsman(spawnLoc, faction);
                } else if (mobData.get(0).equalsIgnoreCase(Titan.typeName)) {
                    newMob = new Titan(spawnLoc, faction);
                } else if (mobData.get(0).equalsIgnoreCase(SpiritBear.typeName)) {
                    newMob = new SpiritBear(spawnLoc, faction);
                } else {
                    ErrorManager.handleError("Unrecognized typeName when loading data");
                    continue;
                }
                if (!newMob.getEnabled()) {
                    System.out.println("Disabled Faction mob found and removed. Did you change the config?");
                    backup = true;
                    continue;
                }
                if (newMob.getFaction() == null || newMob.getFactionName() == null || newMob.getFaction().isNone()) {
                    System.out.println("Factionless Faction Mob found and removed. Did something happen to Factions?");
                    backup = true;
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
                    newMob.setCommand(FactionMob.Command.valueOf(mobData.get(13)));
                } else {
                    newMob.setPoi(
                            Double.parseDouble(mobData.get(6)),
                            Double.parseDouble(mobData.get(7)),
                            Double.parseDouble(mobData.get(8)));
                    newMob.setCommand(FactionMob.Command.poi);
                }

                if (mobData.size() > 14) {
                    if ("1".equals(mobData.get(14))) {
                        newMob.setAttackAll(true);
                    }
                }

                newMob.getEntity().world.addEntity((net.minecraft.server.v1_11_R1.Entity) newMob, SpawnReason.CUSTOM);
                mobList.add(newMob);
                newMob.getEntity().dead = false;
            }
            if (backup) {
                try {
                    conf.save(new File(getDataFolder(), "data_backup.dat"));
                    System.out.println("Backup file saved as data_backup.dat");
                } catch (IOException e) {
                    System.out.println("Failed to save backup file");
                    if (!FactionMobs.silentErrors) e.printStackTrace();
                }
            }
        }
    }

    public void saveMobList() {
        YamlConfiguration conf = new YamlConfiguration();
        List<List<String>> save = new ArrayList<List<String>>(mobList.size());
        for (FactionMob fmob : mobList) {
            if (fmob.getFaction() == null || fmob.getFaction().isNone()) {
                continue;
            }
            List<String> mobData = new ArrayList<String>(13);
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
            mobData.add(fmob.getCommand().toString()); //13
            mobData.add(fmob.getAttackAll() ? "1" : "0"); //14
            save.add(mobData);
        }
        conf.set("data", save);
        try {
            conf.save(new File(getDataFolder(), "data.dat"));
            System.out.println("FactionMobs data saved.");
        } catch (IOException e) {
            ErrorManager.handleError("Failed to save faction mob data, data.dat", e);
        }
        try {
            File colorFile = new File(getDataFolder(), "colors.dat");
            //noinspection ResultOfMethodCallIgnored
            colorFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(colorFile);
            ObjectOutputStream oOut = new ObjectOutputStream(fileOut);
            oOut.writeObject(FactionMobs.factionColors);
            oOut.close();
            fileOut.close();
            System.out.println("FactionMobs color data saved.");
        } catch (Exception e) {
            ErrorManager.handleError("Error writing faction colors file, colors.dat", e);
        }
    }

    public void updateList() {
        for (Iterator<FactionMob> it = FactionMobs.mobList.iterator(); it.hasNext();) {
            FactionMob mob = it.next();
            mob.updateMob();
            if (!mob.getEntity().isAlive())
                it.remove();
        }
    }

    public List<FactionMob>[] getPlayerGroups(Player player) {
        if (player == null) return null;
        String playername = player.getName();
        if (!playerGroups.containsKey(playername)) {
            playerGroups.put(playername, new List[5]);
        }
        return playerGroups.get(playername);
    }

    public void removePlayerGroups(Player player) {
        if (player == null) return;
        playerGroups.remove(player.getName());
    }

    public List<FactionMob> getPlayerSelection(Player player) {
        if (player == null) return null;
        String playername = player.getName();
        if (!playerSelections.containsKey(playername)) {
            playerSelections.put(playername, new LinkedList<FactionMob>());
        }
        return playerSelections.get(playername);
    }

    public void removePlayerSelection(Player player) {
        if (player == null) return;
        playerSelections.remove(player.getName());
    }

    public static final String signature_Author = "Scyntrus";
    public static final String signature_URL = "http://dev.bukkit.org/bukkit-plugins/faction-mobs/";
    public static final String signature_Source = "http://github.com/Scyntrus/FactionMobs";
}
