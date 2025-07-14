package cn.i7mc.damagerRewards.config;

import cn.i7mc.damagerRewards.DamagerRewards;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    
    private final DamagerRewards plugin;
    private final File mobsFolder;
    private final Map<String, MobConfig> mobConfigs;
    
    public ConfigLoader(DamagerRewards plugin) {
        this.plugin = plugin;
        this.mobsFolder = new File(plugin.getDataFolder(), "mobs");
        this.mobConfigs = new HashMap<>();
        initializeFolders();
    }
    
    private void initializeFolders() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        if (!mobsFolder.exists()) {
            mobsFolder.mkdirs();
        }
    }
    
    public void loadAllMobConfigs() {
        mobConfigs.clear();
        
        File[] configFiles = mobsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (configFiles == null) {
            return;
        }
        
        for (File configFile : configFiles) {
            String fileName = configFile.getName();
            String mobName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            MobConfig mobConfig = new MobConfig(configFile, mobName);
            if (mobConfig.isValidConfig()) {
                mobConfigs.put(mobName, mobConfig);
            }
        }
    }
    
    public MobConfig getMobConfig(String mobName) {
        return mobConfigs.get(mobName);
    }
    
    public Map<String, MobConfig> getAllMobConfigs() {
        return new HashMap<>(mobConfigs);
    }
    
    public boolean hasMobConfig(String mobName) {
        return mobConfigs.containsKey(mobName);
    }
    
    public MobConfig getMobConfigByMythicMobsName(String mythicMobsName) {
        return mobConfigs.values().stream()
                .filter(config -> mythicMobsName.equals(config.getMythicMobsName()))
                .findFirst()
                .orElse(null);
    }
    
    public void reloadAllConfigs() {
        for (MobConfig config : mobConfigs.values()) {
            config.reloadConfig();
        }
    }
    
    public void createExampleConfig() {
        File exampleFile = new File(mobsFolder, "example.yml");
        if (!exampleFile.exists()) {
            MobConfig exampleConfig = new MobConfig(exampleFile, "example");
            exampleConfig.getConfig().set("MythicMobs", "King");
            exampleConfig.getConfig().set("Range", 50.0);

            exampleConfig.getConfig().set("TopRewards.Top1", "mm item 传说圣剑 give %dr_top_1% 1");
            exampleConfig.getConfig().set("TopRewards.Top2", "mm item 优良圣剑 give %dr_top_2% 1");
            exampleConfig.getConfig().set("TopRewards.Top3", "mm item 普通圣剑 give %dr_top_3% 1");

            exampleConfig.getConfig().set("AllRewards.all", "mm item 安慰奖品 give <player> 1");
            
            exampleConfig.getConfig().set("world.world", false);
            exampleConfig.getConfig().set("world.world_nether", true);
            
            exampleConfig.saveConfig();
        }
    }
    
    public int getLoadedConfigsCount() {
        return mobConfigs.size();
    }
}