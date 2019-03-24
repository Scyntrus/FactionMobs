package com.gmail.scyntrus.fmob;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

// Can't reflect FactionMobs.class if import doesn't exist, so move Vault here.
public class EconomyManager {
    public final Economy on;

    private EconomyManager(Economy econ) {
        this.on = econ;
    }

    public static EconomyManager get(Server server) {
        if (server.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                Economy econ = rsp.getProvider();
                if (econ != null) {
                    return new EconomyManager(econ);
                }
            }
        }
        return null;
    }
}
