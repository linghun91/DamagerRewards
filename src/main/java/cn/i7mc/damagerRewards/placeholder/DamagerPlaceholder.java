package cn.i7mc.damagerRewards.placeholder;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.config.MobConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class DamagerPlaceholder extends PlaceholderExpansion {
    
    private final DamagerRewards plugin;
    
    public DamagerPlaceholder(DamagerRewards plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "dr";
    }
    
    @Override
    public String getAuthor() {
        return "i7mc";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params == null) {
            return "";
        }
        
        if (params.startsWith("top_")) {
            return handleTopPlaceholder(params);
        }
        
        return "";
    }
    
    private String handleTopPlaceholder(String params) {
        String[] parts = params.split("_");
        if (parts.length < 2) {
            return "";
        }
        
        try {
            int rank = Integer.parseInt(parts[1]);
            
            if (parts.length == 2) {
                return getGlobalTopPlayer(rank);
            } else if (parts.length >= 3) {
                String mobName = String.join("_", java.util.Arrays.copyOfRange(parts, 2, parts.length));
                return getMobTopPlayer(mobName, rank);
            }
        } catch (NumberFormatException e) {
            return "";
        }
        
        return "";
    }
    
    private String getGlobalTopPlayer(int rank) {
        for (MobConfig mobConfig : plugin.getConfigLoader().getAllMobConfigs().values()) {
            String mythicMobsName = mobConfig.getMythicMobsName();
            String playerName = plugin.getDamageManager().getTopPlayerName(mythicMobsName, rank);
            if (!playerName.isEmpty()) {
                return playerName;
            }
        }
        return "";
    }
    
    private String getMobTopPlayer(String mobName, int rank) {
        MobConfig mobConfig = plugin.getConfigLoader().getMobConfig(mobName);
        if (mobConfig == null) {
            return "";
        }
        
        String mythicMobsName = mobConfig.getMythicMobsName();
        return plugin.getDamageManager().getTopPlayerName(mythicMobsName, rank);
    }
}