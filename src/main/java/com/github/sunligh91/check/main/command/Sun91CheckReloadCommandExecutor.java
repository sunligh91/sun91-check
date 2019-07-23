package com.github.sunligh91.check.main.command;

import com.github.sunligh91.check.main.Sun91Check;
import com.github.sunligh91.check.main.Sun91CheckTextLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Sun91CheckReloadCommandExecutor implements CommandExecutor {
    private Sun91Check plugin;

    public Sun91CheckReloadCommandExecutor(Sun91Check plugin) {
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        Sun91CheckTextLoader sun91CheckTextLoader = new Sun91CheckTextLoader();
        sun91CheckTextLoader.init(plugin);
        sender.sendMessage("插件配置重载成功");
        return true;
    }
}
