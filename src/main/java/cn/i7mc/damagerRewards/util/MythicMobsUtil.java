package cn.i7mc.damagerRewards.util;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

public class MythicMobsUtil {
    
    public static boolean isMythicMob(Entity entity) {
        return MythicBukkit.inst().getMobManager().isMythicMob(entity);
    }
    
    public static String getMythicMobType(Entity entity) {
        if (!isMythicMob(entity)) {
            return null;
        }
        
        ActiveMob activeMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
        if (activeMob == null) {
            return null;
        }
        
        return activeMob.getMobType();
    }
    
    public static ActiveMob getActiveMob(Entity entity) {
        if (!isMythicMob(entity)) {
            return null;
        }
        
        return MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
    }
    
    public static boolean isTargetMythicMob(Entity entity, String targetMobType) {
        String mobType = getMythicMobType(entity);
        return mobType != null && mobType.equals(targetMobType);
    }
    
    public static double getMobLevel(Entity entity) {
        ActiveMob activeMob = getActiveMob(entity);
        return activeMob != null ? activeMob.getLevel() : 1.0;
    }
    
    public static String getMobDisplayName(Entity entity) {
        ActiveMob activeMob = getActiveMob(entity);
        return activeMob != null ? activeMob.getDisplayName() : entity.getName();
    }
}