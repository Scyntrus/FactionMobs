package com.gmail.scyntrus.fmob;

import com.gmail.scyntrus.fmob.mobs.Archer;
import com.gmail.scyntrus.fmob.mobs.Mage;
import com.gmail.scyntrus.fmob.mobs.SpiritBear;
import com.gmail.scyntrus.fmob.mobs.Swordsman;
import com.gmail.scyntrus.fmob.mobs.Titan;
import com.gmail.scyntrus.ifactions.Faction;
import com.gmail.scyntrus.ifactions.FactionsManager;
import com.gmail.scyntrus.ifactions.Rank;
import net.minecraft.server.v1_13_R1.EntityPositionTypes;
import net.minecraft.server.v1_13_R1.EntityTypes;
import net.minecraft.server.v1_13_R1.HeightMap.Type;
import net.minecraft.server.v1_13_R1.World;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class FactionMobs extends JavaPlugin {

    public static FactionMobs instance;
    public static final Random random = new Random();

    public static final int responseTime = 20;
    @Option(key="mobsPerFaction")
    public static int mobsPerFaction = 0;
    @Option(key="attackMobs")
    public static boolean attackMobs = true;
    @Option(key="noFriendlyFire")
    public static boolean noFriendlyFire = false;
    @Option(key="noPlayerFriendlyFire")
    public static boolean noPlayerFriendlyFire = false;
    @Option(key="displayMobFaction")
    public static boolean displayMobFaction = true;
    @Option(key="equipArmor")
    public static boolean equipArmor = true;
    @Option(key="alertAllies")
    public static boolean alertAllies = true;
    @Option(key="mobSpeed")
    public static double mobSpeed = .3;
    @Option(key="mobPatrolSpeed")
    public static double mobPatrolSpeed = .175;
    @Option(key="mobNavRange")
    public static double mobNavRange = 64;
    @Option(key="feedEnabled")
    public static boolean feedEnabled = true;
    @Option(key="feedItem")
    public static Material feedItem = Material.APPLE;
    @Option(key="feedAmount")
    public static float feedAmount = 5;
    @Option(key="silentErrors")
    public static boolean silentErrors = true;
    @Option(key="minRankToSpawnStr")
    private static String minRankToSpawnStr = "MEMBER";
    public static Rank minRankToSpawn;
    @Option(key="onlySpawnInTerritory")
    public static boolean onlySpawnInTerritory = true;
    @Option(key="agroRange")
    public static double agroRange = 16;
    @Option(key="disguiseEnabled")
    public static boolean disguiseEnabled = false;
    @Option(key="playerSkin")
    public static String playerSkin = null;

    public static Set<FactionMob> mobList = new HashSet<>();
    public static Map<String,Integer> factionColors = new HashMap<>();
    public static Set<String> mobLeader = new HashSet<>();
    public static Map<String,List<FactionMob>> playerSelections = new HashMap<>();
    public static Map<String,List<FactionMob>[]> playerGroups = new HashMap<>();

    public static PluginManager pm = null;
    public static EconomyManager ec = null;
    public static boolean vaultEnabled = false;
    public static boolean checkMyPet = false;
    public static boolean scheduleChunkMobLoad = false;
    public static int chunkMobLoadTask = -1;

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

        ConfigManager configManager = new ConfigManager(config);
        configManager.populateOptions(this);
        FactionMobs.mobPatrolSpeed = FactionMobs.mobPatrolSpeed * FactionMobs.mobSpeed;
        FactionMobs.minRankToSpawn = Rank.getByName(FactionMobs.minRankToSpawnStr);
        if (FactionMobs.feedEnabled && FactionMobs.feedItem == null) {
            ErrorManager.handleError("Invalid feed item name.");
            FactionMobs.feedEnabled = false;
        }

        configManager.populateOptions(Archer.class);
        configManager.populateOptions(Mage.class);
        configManager.populateOptions(SpiritBear.class);
        configManager.populateOptions(Swordsman.class);
        configManager.populateOptions(Titan.class);

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
            addEntityType(Archer.class, Archer::new, "Archer");
            addEntityType(Swordsman.class, Swordsman::new, "Swordsman");
            addEntityType(Mage.class, Mage::new, "Mage");
            addEntityType(Titan.class, Titan::new, "Titan");
            addEntityType(SpiritBear.class, SpiritBear::new, "SpiritBear");
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

        this.ec = EconomyManager.get(getServer());
        if (this.ec != null) {
            vaultEnabled = true;
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
        if (getServer().getPluginManager().getPlugin("MyPet") != null) checkMyPet = true;

        this.loadMobList();

        chunkMobLoadTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ChunkMobLoader(this), 4, 4);

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("team_plugin", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return FactionsManager.getVersionString();
            }
        }));
    }

    private <T extends net.minecraft.server.v1_13_R1.Entity> void addEntityType(Class<T> entityClass, Function<? super World, ? extends T> ctor, String entityName) throws InvocationTargetException, IllegalAccessException {
        EntityTypes<T> tempEntityType = EntityTypes.a("fallen_crusader", EntityTypes.a.a(entityClass, ctor).b());
        ReflectionManager.entityPositionTypes_a.invoke(null, tempEntityType, EntityPositionTypes.Surface.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES);
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

                newMob.getEntity().world.addEntity((net.minecraft.server.v1_13_R1.Entity) newMob, SpawnReason.CUSTOM);
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
        FactionMobs.mobList.removeIf(fmob -> {
            fmob.updateMob();
            return !fmob.getEntity().isAlive();
        });
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
