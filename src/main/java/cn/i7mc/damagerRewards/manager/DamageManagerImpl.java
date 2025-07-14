package cn.i7mc.damagerRewards.manager;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.config.MobConfig;

public class DamageManagerImpl extends DamageManager {
    
    private final DamagerRewards plugin;
    
    public DamageManagerImpl(DamagerRewards plugin) {
        super();
        this.plugin = plugin;
    }
    
    @Override
    public void processRewards(String mobInstanceId, MobConfig mobConfig, org.bukkit.Location deathLocation) {
        RewardManager rewardManager = new RewardManager(plugin);
        rewardManager.processAllRewards(mobInstanceId, mobConfig, deathLocation);
    }
    
    @Override
    public void resetMobRanking(String mobInstanceId) {
        clearRanking(mobInstanceId);
    }
}