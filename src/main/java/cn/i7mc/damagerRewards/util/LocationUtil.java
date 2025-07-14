package cn.i7mc.damagerRewards.util;

import org.bukkit.Location;

public class LocationUtil {
    
    public static boolean isInRange(Location loc1, Location loc2, double range) {
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }
        
        return loc1.distance(loc2) <= range;
    }
    
    public static double getDistance(Location loc1, Location loc2) {
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return Double.MAX_VALUE;
        }
        
        return loc1.distance(loc2);
    }
    
    public static boolean isSameWorld(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld());
    }
    
    public static String getWorldName(Location location) {
        return location.getWorld().getName();
    }
}