package cn.i7mc.damagerRewards.data;

import org.bukkit.entity.Player;
import java.util.UUID;

public class PlayerDamageData {
    
    private final UUID playerUUID;
    private final String playerName;
    private double totalDamage;
    private boolean isParticipant;
    
    public PlayerDamageData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
        this.totalDamage = 0.0;
        this.isParticipant = false;
    }
    
    public PlayerDamageData(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.totalDamage = 0.0;
        this.isParticipant = false;
    }
    
    public void addDamage(double damage) {
        this.totalDamage += damage;
        this.isParticipant = true;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public double getTotalDamage() {
        return totalDamage;
    }
    
    public boolean isParticipant() {
        return isParticipant;
    }
    
    public void setTotalDamage(double totalDamage) {
        this.totalDamage = totalDamage;
    }
    
    public void setParticipant(boolean participant) {
        this.isParticipant = participant;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerDamageData that = (PlayerDamageData) obj;
        return playerUUID.equals(that.playerUUID);
    }
    
    @Override
    public int hashCode() {
        return playerUUID.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("PlayerDamageData{name='%s', damage=%.2f, participant=%s}", 
                           playerName, totalDamage, isParticipant);
    }
}