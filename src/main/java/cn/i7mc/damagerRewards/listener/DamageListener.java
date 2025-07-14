package cn.i7mc.damagerRewards.listener;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.config.MobConfig;
import cn.i7mc.damagerRewards.util.LocationUtil;
import cn.i7mc.damagerRewards.util.MythicMobsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageListener implements Listener {

    private final DamagerRewards plugin;

    private final Map<String, Map<UUID, Double>> debugDamageTracker = new HashMap<>();

    public DamageListener(DamagerRewards plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        Entity victim = event.getEntity();

        if (victim instanceof Player) {
            return;
        }

        if (!MythicMobsUtil.isMythicMob(victim)) {
            return;
        }

        String mythicMobType = MythicMobsUtil.getMythicMobType(victim);
        if (mythicMobType == null) {
            return;
        }

        MobConfig mobConfig = plugin.getConfigLoader().getMobConfigByMythicMobsName(mythicMobType);
        if (mobConfig == null) {
            return;
        }

        String worldName = LocationUtil.getWorldName(victim.getLocation());
        if (!mobConfig.isWorldEnabled(worldName)) {
            return;
        }

        double range = mobConfig.getRange();
        if (!LocationUtil.isInRange(player.getLocation(), victim.getLocation(), range)) {
            return;
        }

        if (!(victim instanceof org.bukkit.entity.Damageable)) {
            return;
        }

        org.bukkit.entity.Damageable damageableVictim = (org.bukkit.entity.Damageable) victim;
        double healthBefore = damageableVictim.getHealth();
        double maxHealth = damageableVictim.getMaxHealth();
        double eventFinalDamage = event.getFinalDamage();

        String mobInstanceId = mythicMobType + "_" + victim.getUniqueId().toString();

        if (plugin.getConfig().getBoolean("debug", false)) {
            Bukkit.getConsoleSender().sendMessage("§e[DamagerRewards调试] 伤害事件开始:");
            Bukkit.getConsoleSender().sendMessage("§e  玩家: " + player.getName() + " (" + player.getUniqueId() + ")");
            Bukkit.getConsoleSender().sendMessage("§e  怪物: " + mythicMobType + " (ID:" + victim.getEntityId() + ")");
            Bukkit.getConsoleSender().sendMessage("§e  伤害前生命值: " + String.format("%.2f", healthBefore) + "/" + String.format("%.2f", maxHealth));
            Bukkit.getConsoleSender().sendMessage("§e  事件最终伤害: " + String.format("%.2f", eventFinalDamage));
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            double actualDamage = 0;
            boolean mobDied = victim.isDead();

            if (mobDied) {
                actualDamage = healthBefore;
                if (plugin.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§c  怪物死亡! 实际伤害 = 死亡前生命值: " + String.format("%.2f", actualDamage));
                }
            } else {
                double healthAfter = damageableVictim.getHealth();
                actualDamage = healthBefore - healthAfter;
                if (plugin.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§a  伤害后生命值: " + String.format("%.2f", healthAfter) + "/" + String.format("%.2f", maxHealth));
                    Bukkit.getConsoleSender().sendMessage("§a  实际伤害: " + String.format("%.2f", healthBefore) + " - " + String.format("%.2f", healthAfter) + " = " + String.format("%.2f", actualDamage));
                }
            }

            if (actualDamage > 0) {
                if (plugin.getConfig().getBoolean("debug", false)) {
                    debugDamageTracker.computeIfAbsent(mobInstanceId, k -> new HashMap<>());
                    Map<UUID, Double> playerDamages = debugDamageTracker.get(mobInstanceId);
                    double previousDamage = playerDamages.getOrDefault(player.getUniqueId(), 0.0);
                    double newTotalDamage = previousDamage + actualDamage;
                    playerDamages.put(player.getUniqueId(), newTotalDamage);

                    Bukkit.getConsoleSender().sendMessage("§b  玩家累计真实伤害: " + String.format("%.2f", previousDamage) + " + " + String.format("%.2f", actualDamage) + " = " + String.format("%.2f", newTotalDamage));

                    if (mobDied) {
                        outputFinalDebugComparison(mobInstanceId, mobInstanceId, playerDamages);
                    }
                }

                recordDamage(mobInstanceId, player, actualDamage, mobConfig);
            } else {
                if (plugin.getConfig().getBoolean("debug", false)) {
                    Bukkit.getConsoleSender().sendMessage("§7  无有效伤害，跳过记录");
                }
            }

            if (plugin.getConfig().getBoolean("debug", false)) {
                Bukkit.getConsoleSender().sendMessage("§e[DamagerRewards调试] 伤害事件结束\n");
            }
        });
    }

    private void recordDamage(String mobInstanceId, Player player, double damage, MobConfig mobConfig) {
        plugin.getDamageManager().recordDamage(
            mobInstanceId,
            player.getUniqueId(),
            player.getName(),
            damage,
            mobConfig
        );
    }

    private void outputFinalDebugComparison(String mobInstanceId, String mobDebugId, Map<UUID, Double> debugPlayerDamages) {
        if (!plugin.getConfig().getBoolean("debug", false)) {
            return;
        }

        Bukkit.getConsoleSender().sendMessage("§6========== 怪物死亡 - 最终伤害统计对比 ==========");
        Bukkit.getConsoleSender().sendMessage("§6怪物: " + mobInstanceId + " (调试ID: " + mobDebugId + ")");

        var rankingData = plugin.getDamageManager().getRanking(mobInstanceId);
        if (rankingData != null) {
            Bukkit.getConsoleSender().sendMessage("§6\n对比结果:");

            for (Map.Entry<UUID, Double> entry : debugPlayerDamages.entrySet()) {
                UUID playerId = entry.getKey();
                double debugTotalDamage = entry.getValue();

                var playerData = rankingData.getPlayerData(playerId);
                double systemDamage = playerData != null ? playerData.getTotalDamage() : 0.0;

                String playerName = Bukkit.getOfflinePlayer(playerId).getName();
                Bukkit.getConsoleSender().sendMessage("§6  玩家: " + playerName + " (" + playerId + ")");
                Bukkit.getConsoleSender().sendMessage("§6    调试追踪累计伤害: " + String.format("%.2f", debugTotalDamage));
                Bukkit.getConsoleSender().sendMessage("§6    系统统计伤害: " + String.format("%.2f", systemDamage));

                double difference = Math.abs(debugTotalDamage - systemDamage);
                if (difference < 0.01) {
                    Bukkit.getConsoleSender().sendMessage("§a    ✓ 数据一致!");
                } else {
                    Bukkit.getConsoleSender().sendMessage("§c    ✗ 数据不一致! 差异: " + String.format("%.2f", difference));
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§c无法获取系统伤害统计数据!");
        }

        debugDamageTracker.remove(mobInstanceId);
        Bukkit.getConsoleSender().sendMessage("§6================================================\n");
    }
}