package cn.i7mc.damagerRewards;

import cn.i7mc.damagerRewards.command.DamagerRewardsCommand;
import cn.i7mc.damagerRewards.config.ConfigLoader;
import cn.i7mc.damagerRewards.listener.DamageListener;
import cn.i7mc.damagerRewards.listener.MobDeathListener;
import cn.i7mc.damagerRewards.manager.DamageManagerImpl;
import cn.i7mc.damagerRewards.manager.RewardManager;
import cn.i7mc.damagerRewards.placeholder.DamagerPlaceholder;
import cn.i7mc.damagerRewards.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class DamagerRewards extends JavaPlugin {
    
    private ConfigLoader configLoader;
    private DamageManagerImpl damageManager;
    private RewardManager rewardManager;
    private DamagerPlaceholder placeholderManager;
    
    @Override
    public void onEnable() {
        initializeComponents();
        registerEvents();
        registerCommands();
        registerPlaceholders();
        
        getLogger().info("DamagerRewards插件已成功启用!");
    }
    
    @Override
    public void onDisable() {
        if (placeholderManager != null) {
            placeholderManager.unregister();
        }
        
        getLogger().info("DamagerRewards插件已关闭!");
    }
    
    private void initializeComponents() {
        saveDefaultConfig();
        
        // 确保message.yml文件存在且有内容
        File messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            saveResource("message.yml", false);
        }
        
        this.configLoader = new ConfigLoader(this);
        this.damageManager = new DamageManagerImpl(this);
        this.rewardManager = new RewardManager(this);
        this.placeholderManager = new DamagerPlaceholder(this);
        
        MessageUtil.setInstance(new MessageUtil(messageFile));
        
        configLoader.createExampleConfig();
        configLoader.loadAllMobConfigs();
        
        getLogger().info("已加载 " + configLoader.getLoadedConfigsCount() + " 个怪物配置文件");
    }
    
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MobDeathListener(this), this);
    }
    
    private void registerCommands() {
        DamagerRewardsCommand commandExecutor = new DamagerRewardsCommand(this);
        getCommand("damagerrewards").setExecutor(commandExecutor);
        getCommand("damagerrewards").setTabCompleter(commandExecutor);
    }
    
    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderManager.register();
            getLogger().info("PlaceholderAPI占位符已注册!");
        } else {
            getLogger().warning("未找到PlaceholderAPI插件，占位符功能将无法使用!");
        }
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        configLoader.reloadAllConfigs();
        configLoader.loadAllMobConfigs();
        
        // 重新加载消息配置
        if (MessageUtil.getInstance() != null) {
            MessageUtil.getInstance().reloadConfig();
        }
        
        getLogger().info("配置文件已重新加载!");
    }
    
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }
    
    public DamageManagerImpl getDamageManager() {
        return damageManager;
    }
    
    public RewardManager getRewardManager() {
        return rewardManager;
    }
    
    public DamagerPlaceholder getPlaceholderManager() {
        return placeholderManager;
    }
}
