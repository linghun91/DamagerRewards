package cn.i7mc.damagerRewards.util;

import cn.i7mc.damagerRewards.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.io.File;

public class MessageUtil extends ConfigManager {
    
    private static MessageUtil instance;
    
    public MessageUtil(File configFile) {
        super(configFile);
    }
    
    public static void setInstance(MessageUtil instance) {
        MessageUtil.instance = instance;
    }
    
    public static MessageUtil getInstance() {
        return instance;
    }
    
    public String getMessage(String path) {
        String message = getString(path);
        if (message == null) {
            return "&c消息路径未找到: " + path;
        }
        
        // 处理${prefix}占位符
        if (message.contains("${prefix}")) {
            String prefix = getString("prefix");
            if (prefix != null) {
                message = message.replace("${prefix}", prefix);
            }
        }
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String path, String placeholder, String value) {
        String message = getMessage(path);
        return message.replace("{" + placeholder + "}", value);
    }
    
    public String getMessage(String path, String[] placeholders, String[] values) {
        String message = getMessage(path);
        for (int i = 0; i < placeholders.length && i < values.length; i++) {
            message = message.replace("{" + placeholders[i] + "}", values[i]);
        }
        return message;
    }
    
    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getMessage(path));
    }
    
    public void sendMessage(CommandSender sender, String path, String placeholder, String value) {
        sender.sendMessage(getMessage(path, placeholder, value));
    }
    
    public void sendMessage(CommandSender sender, String path, String[] placeholders, String[] values) {
        sender.sendMessage(getMessage(path, placeholders, values));
    }
    
    public static void send(CommandSender sender, String path) {
        if (instance != null) {
            instance.sendMessage(sender, path);
        }
    }
    
    public static void send(CommandSender sender, String path, String placeholder, String value) {
        if (instance != null) {
            instance.sendMessage(sender, path, placeholder, value);
        }
    }
    
    public static void send(CommandSender sender, String path, String[] placeholders, String[] values) {
        if (instance != null) {
            instance.sendMessage(sender, path, placeholders, values);
        }
    }
}