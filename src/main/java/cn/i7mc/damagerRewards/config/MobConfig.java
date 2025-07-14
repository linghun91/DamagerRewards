package cn.i7mc.damagerRewards.config;

import cn.i7mc.damagerRewards.manager.ConfigManager;
import java.io.File;
import java.util.Map;

public class MobConfig extends ConfigManager {
    
    private final String mobName;
    
    public MobConfig(File configFile, String mobName) {
        super(configFile);
        this.mobName = mobName;
    }
    
    public String getMythicMobsName() {
        return getString("MythicMobs");
    }
    
    /**
     * 动态获取top排名数量
     * 根据TopRewards配置中实际设置的奖励数量确定
     * @return top排名数量
     */
    public int getDamagerTop() {
        Map<String, Object> topRewards = getTopRewards();
        if (topRewards == null || topRewards.isEmpty()) {
            return 0;
        }

        int maxTop = 0;
        for (String key : topRewards.keySet()) {
            if (key.startsWith("Top")) {
                try {
                    int topNum = Integer.parseInt(key.substring(3));
                    maxTop = Math.max(maxTop, topNum);
                } catch (NumberFormatException e) {
                    // 忽略无效的key
                }
            }
        }
        return maxTop;
    }
    
    public Map<String, Object> getTopRewards() {
        return getConfigurationSection("TopRewards");
    }
    
    public Map<String, Object> getAllRewards() {
        return getConfigurationSection("AllRewards");
    }
    
    public Map<String, Object> getWorldSettings() {
        if (contains("world")) {
            return getConfigurationSection("world");
        }
        return null;
    }
    
    public double getRange() {
        return getDouble("Range");
    }
    

    
    public boolean isWorldEnabled(String worldName) {
        Map<String, Object> worldSettings = getWorldSettings();
        if (worldSettings == null) {
            return true;
        }
        
        Object enabled = worldSettings.get(worldName);
        if (enabled instanceof Boolean) {
            return (Boolean) enabled;
        }
        return true;
    }
    
    public String getMobConfigName() {
        return mobName;
    }
    
    public boolean isValidConfig() {
        return getMythicMobsName() != null &&
               getRange() > 0 &&
               getTopRewards() != null;
    }
}