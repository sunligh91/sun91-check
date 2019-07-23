package com.github.sunligh91.check.main;

import com.github.sunligh91.check.listener.CheckListener;
import com.github.sunligh91.check.main.command.Sun91CheckCommandExecutor;
import com.github.sunligh91.check.main.command.Sun91CheckReloadCommandExecutor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Sun91Check extends JavaPlugin {
    private static Logger log = Logger.getLogger("Minecraft");
    public  Economy econ = null;


    @Override
    public void onEnable() {
        System.out.println("Sun91Check插件版本：1.0");
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Sun91CheckTextLoader sun91CheckTextLoader = new Sun91CheckTextLoader();
        sun91CheckTextLoader.init(this);

        Server server = getServer();
        CheckListener checkListener=new CheckListener(this);
        server.getPluginManager().registerEvents(checkListener,this);
        this.getCommand("sunc").setExecutor(new Sun91CheckCommandExecutor(this));
        this.getCommand("suncr").setExecutor(new Sun91CheckReloadCommandExecutor(this));
        System.out.println("Sun91Check加载成功");

    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
