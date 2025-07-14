package cn.i7mc.damagerRewards.manager;

import cn.i7mc.damagerRewards.config.MobConfig;
import cn.i7mc.damagerRewards.data.PlayerDamageData;
import cn.i7mc.damagerRewards.data.RankingData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class DamageManager {
    
    protected final Map<String, RankingData> mobRankings;
    
    public DamageManager() {
        this.mobRankings = new HashMap<>();
    }
    
    public void recordDamage(String mobInstanceId, UUID playerUUID, String playerName,
                           double damage, MobConfig mobConfig) {
        RankingData ranking = mobRankings.computeIfAbsent(mobInstanceId,
            k -> new RankingData(mobInstanceId, mobConfig.getDamagerTop()));

        ranking.addPlayerDamage(playerUUID, playerName, damage);
    }
    
    public RankingData getRanking(String mobInstanceId) {
        return mobRankings.get(mobInstanceId);
    }
    
    public List<PlayerDamageData> getTopPlayers(String mobInstanceId) {
        RankingData ranking = getRanking(mobInstanceId);
        return ranking != null ? ranking.getTopPlayers() : null;
    }

    public List<PlayerDamageData> getAllParticipantsRanked(String mobInstanceId) {
        RankingData ranking = getRanking(mobInstanceId);
        return ranking != null ? ranking.getAllParticipantsRanked() : null;
    }

    public List<PlayerDamageData> getParticipants(String mobInstanceId) {
        RankingData ranking = getRanking(mobInstanceId);
        return ranking != null ? ranking.getParticipants() : null;
    }
    
    public String getTopPlayerName(String mobInstanceId, int rank) {
        RankingData ranking = getRanking(mobInstanceId);
        return ranking != null ? ranking.getTopPlayerName(rank) : "";
    }
    
    public void clearRanking(String mobInstanceId) {
        RankingData ranking = getRanking(mobInstanceId);
        if (ranking != null) {
            ranking.clearData();
        }
    }
    
    public void clearAllRankings() {
        mobRankings.clear();
    }
    
    public abstract void processRewards(String mobInstanceId, MobConfig mobConfig, org.bukkit.Location deathLocation);
    
    public abstract void resetMobRanking(String mobInstanceId);
}