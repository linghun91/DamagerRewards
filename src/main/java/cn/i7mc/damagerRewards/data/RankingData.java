package cn.i7mc.damagerRewards.data;

import java.util.*;
import java.util.stream.Collectors;

public class RankingData {

    private final String mobName;
    private final Map<UUID, PlayerDamageData> damageDataMap;
    private final int topCount;

    public RankingData(String mobName, int topCount) {
        this.mobName = mobName;
        this.topCount = topCount;
        this.damageDataMap = new HashMap<>();
    }
    
    public void addPlayerDamage(UUID playerUUID, String playerName, double damage) {
        PlayerDamageData data = damageDataMap.computeIfAbsent(playerUUID, 
            k -> new PlayerDamageData(playerUUID, playerName));
        data.addDamage(damage);
    }
    
    /**
     * 获取top排名玩家列表
     * 按伤害值从高到低排序，限制为topCount数量
     * @return top排名玩家列表
     */
    public List<PlayerDamageData> getTopPlayers() {
        return damageDataMap.values().stream()
                .filter(data -> data.isParticipant())
                .sorted((a, b) -> Double.compare(b.getTotalDamage(), a.getTotalDamage()))
                .limit(topCount)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有参与者的完整排名列表（用于显示排名信息）
     * @return 按伤害排序的所有参与者列表
     */
    public List<PlayerDamageData> getAllParticipantsRanked() {
        return damageDataMap.values().stream()
                .filter(data -> data.isParticipant())
                .sorted((a, b) -> Double.compare(b.getTotalDamage(), a.getTotalDamage()))
                .collect(Collectors.toList());
    }
    
    public List<PlayerDamageData> getParticipants() {
        return damageDataMap.values().stream()
                .filter(data -> data.isParticipant())
                .collect(Collectors.toList());
    }
    
    public PlayerDamageData getPlayerData(UUID playerUUID) {
        return damageDataMap.get(playerUUID);
    }
    
    public String getTopPlayerName(int rank) {
        List<PlayerDamageData> allRanked = getAllParticipantsRanked();
        if (rank > 0 && rank <= allRanked.size()) {
            return allRanked.get(rank - 1).getPlayerName();
        }
        return "";
    }

    public double getTopPlayerDamage(int rank) {
        List<PlayerDamageData> allRanked = getAllParticipantsRanked();
        if (rank > 0 && rank <= allRanked.size()) {
            return allRanked.get(rank - 1).getTotalDamage();
        }
        return 0.0;
    }
    
    public String getMobName() {
        return mobName;
    }
    

    
    public int getTopCount() {
        return topCount;
    }
    
    public int getTotalParticipants() {
        return (int) damageDataMap.values().stream()
                .filter(PlayerDamageData::isParticipant)
                .count();
    }
    
    public void clearData() {
        damageDataMap.clear();
    }
    
    public boolean isEmpty() {
        return damageDataMap.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("RankingData{mob='%s', participants=%d, topCount=%d}",
                           mobName, getTotalParticipants(), topCount);
    }
}