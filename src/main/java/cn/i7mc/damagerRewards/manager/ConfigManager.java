package cn.i7mc.damagerRewards.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class ConfigManager {
    
    protected final File configFile;
    protected FileConfiguration config;
    
    public ConfigManager(File configFile) {
        this.configFile = configFile;
        loadConfig();
    }
    
    protected void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public String getString(String path) {
        return config.getString(path);
    }
    
    public int getInt(String path) {
        return config.getInt(path);
    }
    
    public double getDouble(String path) {
        return config.getDouble(path);
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    
    public Map<String, Object> getConfigurationSection(String path) {
        return config.getConfigurationSection(path).getValues(false);
    }
    
    public boolean contains(String path) {
        return config.contains(path);
    }
}