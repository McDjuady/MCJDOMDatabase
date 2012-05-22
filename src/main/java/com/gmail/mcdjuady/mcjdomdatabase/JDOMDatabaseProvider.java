/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.mcdjuady.mcjdomdatabase;

import com.gmail.mcdjuady.mcdatabase.Database;
import com.gmail.mcdjuady.mcdatabase.DatabaseProvider;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author McDjuady
 */
public class JDOMDatabaseProvider implements DatabaseProvider {

    public Database newInstance(Plugin p, String name) {
        JDOMDatabase db = new JDOMDatabase();
        File dbFile = new File(p.getDataFolder(), p.getConfig().get("Database.Base.Path")+ "/" + name + ".xml");
        try {
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                Bukkit.getLogger().severe("[MCDatabase|JDOM] Error while creating database "+name+"\n"+"Creation of a new File failed ("+dbFile.getAbsolutePath());
            return null;
            }
            db.load(dbFile, true);
        } catch (IOException ex) {
            Bukkit.getLogger().severe("[MCDatabase|JDOM] Error while creating database "+name+"\nPath: "+dbFile.getAbsolutePath()+"\n"+ex.toString());
            return null;
        }
        Bukkit.getLogger().info("[MCDatabase|JDOM] Created database "+name);
        return db;
    }

    public Map<String, Database> startup(Plugin p) {
        FileConfiguration config = p.getConfig();
        if (!config.contains("Database.Base.Path")) {
            config.addDefault("Database.Base.Path", "/Database");
            config.set("Database.Base.Path", "/Database");
            p.saveConfig();
            p.reloadConfig();
            config = p.getConfig();
        }
        String path = config.getString("Database.Base.Path");
        File dataFolder = p.getDataFolder();
        if ((!dataFolder.exists() || !dataFolder.isDirectory()) && !dataFolder.mkdir()) {
            Bukkit.getLogger().severe("[MCDatabase|JDOM] Failed to create database folder");
            return Collections.emptyMap();
        }
        File dbFolder = new File(p.getDataFolder(), path);
        if (!dbFolder.exists() && !dbFolder.mkdirs()) {
            Bukkit.getLogger().severe("[MCDatabase|JDOM] Failed to create database folder");
            return Collections.emptyMap();
        }
        final String fileEnding = ".xml";
        Map<String, Database> dbs = new HashMap<String, Database>();
        for (File f : dbFolder.listFiles(new FilenameFilter() {

            public boolean accept(File f, String name) {
                return name.endsWith(fileEnding);
            }
        })) {
            String name = f.getName().replace(fileEnding, "");
            JDOMDatabase db = new JDOMDatabase();
            try {
                db.load(f);
            } catch (IOException ex) {
                Bukkit.getLogger().severe("[MCDatabase|JDOM] IOException while loading Database "+name+"\n"+ex.getLocalizedMessage());
                continue;
            }
            Bukkit.getLogger().info("[MCDatabase|JDOM] Loaded database "+name);
            dbs.put(name, db);
        }
        return dbs;
    }
    
    public void delete(Plugin p, Database db) {
        if (!(db instanceof JDOMDatabase)) {
            return;
        }
        try {
            db.save();
        } catch (IOException ex) {
            
        }
        File f = ((JDOMDatabase)db).getFile();
        f.delete();
    }
}
