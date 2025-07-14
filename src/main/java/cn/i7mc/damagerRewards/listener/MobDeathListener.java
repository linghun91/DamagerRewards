package cn.i7mc.damagerRewards.listener;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.config.MobConfig;
import cn.i7mc.damagerRewards.util.MythicMobsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeathListener implements Listener {
    
    private final DamagerRewards plugin;
    
    public MobDeathListener(DamagerRewards plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!MythicMobsUtil.isMythicMob(entity)) {
            return;
        }

        String mythicMobType = MythicMobsUtil.getMythicMobType(entity);
        if (mythicMobType == null) {
            return;
        }

        MobConfig mobConfig = plugin.getConfigLoader().getMobConfigByMythicMobsName(mythicMobType);
        if (mobConfig == null) {
            return;
        }

        // 使用与DamageListener相同的怪物实例ID
        String mobInstanceId = mythicMobType + "_" + entity.getUniqueId().toString();

        // 延迟处理死亡事件，确保伤害统计完成
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getDamageManager().processRewards(mobInstanceId, mobConfig, entity.getLocation());
            plugin.getDamageManager().resetMobRanking(mobInstanceId);
        });
    }
}