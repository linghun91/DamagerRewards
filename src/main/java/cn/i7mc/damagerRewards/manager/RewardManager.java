package cn.i7mc.damagerRewards.manager;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.config.MobConfig;
import cn.i7mc.damagerRewards.data.PlayerDamageData;
import org.bukkit.Bukkit;
import java.util.List;
import java.util.Map;

public class RewardManager {
    
    private final DamagerRewards plugin;
    
    public RewardManager(DamagerRewards plugin) {
        this.plugin = plugin;
    }
    
    public void executeTopRewards(String mobInstanceId, MobConfig mobConfig) {
        List<PlayerDamageData> topPlayers = plugin.getDamageManager().getTopPlayers(mobInstanceId);
        if (topPlayers == null || topPlayers.isEmpty()) {
            return;
        }

        Map<String, Object> topRewards = mobConfig.getTopRewards();
        if (topRewards == null) {
            return;
        }

        for (int i = 0; i < topPlayers.size(); i++) {
            String rankKey = "Top" + (i + 1);
            Object rewardData = topRewards.get(rankKey);

            if (rewardData != null) {
                executeRewardCommands(rewardData, mobInstanceId, i + 1);
            }
        }
    }

    /**
     * 执行奖励命令，支持单行指令和多行指令列表
     * @param rewardData 奖励数据，可以是String或List<String>
     * @param mobInstanceId 怪物实例ID
     * @param rank 排名
     */
    @SuppressWarnings("unchecked")
    private void executeRewardCommands(Object rewardData, String mobInstanceId, int rank) {
        if (rewardData instanceof String) {
            // 单行指令处理
            final String command = replacePlaceholders((String) rewardData, mobInstanceId, rank);
            executeCommand(command);
        } else if (rewardData instanceof List) {
            // 多行指令列表处理
            List<String> commands = (List<String>) rewardData;
            for (String commandStr : commands) {
                final String command = replacePlaceholders(commandStr, mobInstanceId, rank);
                executeCommand(command);
            }
        }
    }

    /**
     * 执行单条命令
     * @param command 要执行的命令
     */
    private void executeCommand(String command) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }
    
    public void executeParticipantRewards(String mobInstanceId, MobConfig mobConfig) {
        List<PlayerDamageData> participants = plugin.getDamageManager().getParticipants(mobInstanceId);
        if (participants == null || participants.isEmpty()) {
            return;
        }

        List<PlayerDamageData> topPlayers = plugin.getDamageManager().getTopPlayers(mobInstanceId);

        Map<String, Object> allRewards = mobConfig.getAllRewards();
        if (allRewards == null) {
            return;
        }

        Object rewardData = allRewards.get("all");
        if (rewardData == null) {
            return;
        }

        for (PlayerDamageData participant : participants) {
            boolean isTopPlayer = topPlayers != null && topPlayers.stream()
                    .anyMatch(topPlayer -> topPlayer.getPlayerUUID().equals(participant.getPlayerUUID()));

            if (!isTopPlayer) {
                executeParticipantRewardCommands(rewardData, participant.getPlayerName());
            }
        }
    }

    /**
     * 执行参与者奖励命令，支持单行指令和多行指令列表
     * @param rewardData 奖励数据，可以是String或List<String>
     * @param playerName 玩家名称
     */
    @SuppressWarnings("unchecked")
    private void executeParticipantRewardCommands(Object rewardData, String playerName) {
        if (rewardData instanceof String) {
            // 单行指令处理
            String command = ((String) rewardData).replace("<player>", playerName);
            executeCommand(command);
        } else if (rewardData instanceof List) {
            // 多行指令列表处理
            List<String> commands = (List<String>) rewardData;
            for (String commandStr : commands) {
                String command = commandStr.replace("<player>", playerName);
                executeCommand(command);
            }
        }
    }
    
    private String replacePlaceholders(String command, String mobInstanceId, int rank) {
        String playerName = plugin.getDamageManager().getTopPlayerName(mobInstanceId, rank);
        String placeholder = "%dr_top_" + rank + "%";
        return command.replace(placeholder, playerName);
    }
    
    /**
     * 显示所有参与者的排名信息给在场玩家
     * @param mythicMobType 怪物类型
     * @param mobConfig 怪物配置
     * @param deathLocation 怪物死亡位置
     */
    public void displayRankingInfo(String mobInstanceId, MobConfig mobConfig, org.bukkit.Location deathLocation) {
        List<PlayerDamageData> allRanked = plugin.getDamageManager().getAllParticipantsRanked(mobInstanceId);
        if (allRanked == null || allRanked.isEmpty()) {
            return;
        }

        double range = mobConfig.getRange();
        String worldName = deathLocation.getWorld().getName();

        if (!mobConfig.isWorldEnabled(worldName)) {
            return;
        }

        String mythicMobType = mobInstanceId.contains("_") ? mobInstanceId.split("_")[0] : mobInstanceId;
        
        java.util.List<String> rankingMessages = new java.util.ArrayList<>();
        rankingMessages.add("§6=== " + mythicMobType + " 伤害排行榜 ===");

        for (int i = 0; i < allRanked.size(); i++) {
            PlayerDamageData player = allRanked.get(i);
            String rankInfo = String.format("§e%d. §f%s §7造成了 §c%.2f §7伤害",
                    i + 1, player.getPlayerName(), player.getTotalDamage());
            rankingMessages.add(rankInfo);
        }
        rankingMessages.add("§6========================");

        for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getWorld().getName().equals(worldName)) {
                continue;
            }

            double distance = onlinePlayer.getLocation().distance(deathLocation);
            if (distance <= range) {
                for (String message : rankingMessages) {
                    onlinePlayer.sendMessage(message);
                }
            }
        }
    }

    public void processAllRewards(String mobInstanceId, MobConfig mobConfig, org.bukkit.Location deathLocation) {
        displayRankingInfo(mobInstanceId, mobConfig, deathLocation);
        executeTopRewards(mobInstanceId, mobConfig);
        executeParticipantRewards(mobInstanceId, mobConfig);
    }
}