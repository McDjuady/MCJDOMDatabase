/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.mcdjuady.mcjdomdatabase;

import com.gmail.mcdjuady.mcdatabase.DatabaseProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author McDjuady
 */
public class JDOMDatabasePlugin extends JavaPlugin {

    public void onDisable() {
        
    }

    public void onEnable() {
        Bukkit.getServicesManager().register(DatabaseProvider.class, new JDOMDatabaseProvider(), this, ServicePriority.Normal);
    }
}
