package cn.i7mc.damagerRewards.command;

import cn.i7mc.damagerRewards.DamagerRewards;
import cn.i7mc.damagerRewards.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DamagerRewardsCommand implements CommandExecutor, TabCompleter {
    
    private final DamagerRewards plugin;
    
    public DamagerRewardsCommand(DamagerRewards plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("damagerrewards.reload")) {
            MessageUtil.send(sender, "plugin.no-permission");
            return;
        }
        
        try {
            plugin.reloadConfig();
            MessageUtil.send(sender, "plugin.reloaded");
        } catch (Exception e) {
            MessageUtil.send(sender, "error.internal-error", "error", e.getMessage());
            plugin.getLogger().severe("重载配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendHelp(CommandSender sender) {
        MessageUtil.send(sender, "help.header");
        MessageUtil.send(sender, "help.reload");
        MessageUtil.send(sender, "help.footer");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("damagerrewards.reload")) {
                completions.add("reload");
            }
            
            String input = args[0].toLowerCase();
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(input));
        }
        
        return completions;
    }
}