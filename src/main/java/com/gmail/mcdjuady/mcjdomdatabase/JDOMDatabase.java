/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.mcdjuady.mcjdomdatabase;

import com.gmail.mcdjuady.mcdatabase.Database;
import com.gmail.mcdjuady.mcdatabase.DatabaseGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.util.FileUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author maximilian.micko
 */
public class JDOMDatabase implements Database {

    private File dbFile;
    private Document doc;
    private JDOMDBNode rootNode;
    private Map<String, JDOMDBNode> nodes = new HashMap<String, JDOMDBNode>();
    private Map<String, DatabaseGroup> groups = new HashMap<String, DatabaseGroup>();

    public Collection<DatabaseGroup> getGroups() {
        return groups.values();
    }

    public DatabaseGroup readGroup(String name) {
        return groups.get(name);
    }

    public DatabaseGroup createGroup(String name) {
        if (groups.containsKey(name)) {
            return groups.get(name);
        }
        JDOMDatabaseGroup group = new JDOMDatabaseGroup(doc, rootNode, name);
        groups.put(name, group);
        nodes.put(name, group.getGroupNode());
        return group;
    }

    public DatabaseGroup destroyGroup(DatabaseGroup group) {
        if (!groups.containsKey(group.getName())) {
            return null;
        }
        nodes.remove(group.getName()).delete();
        return groups.remove(group.getName());
    }

    public void copyTo(Database db) {
        Collection<String> keys = groups.keySet();
        for (String key : keys) {
            DatabaseGroup from = groups.get(key);
            DatabaseGroup to = db.createGroup(key);
            from.copyTo(to);
        }
    }
    
    public void load(File f) throws IOException {
        load(f,false);
    }
    
    public void load(File f, boolean isNew) throws IOException {
        if (f == null) {
            return;
        }
        dbFile = f;
        SAXBuilder builder = new SAXBuilder();
        File xmlFolder = new File(dbFile, "..");
        if (!xmlFolder.exists()) {
            xmlFolder.mkdirs();
        }
        if (!dbFile.exists()) {
            dbFile.createNewFile();
            doc = new Document();
            rootNode = new JDOMDBNode(doc, "root");
        } else {
            try {
                doc = builder.build(dbFile);
                rootNode = new JDOMDBNode(doc, doc.getRootElement());
            } catch (JDOMException ex) {
                if (!isNew) {
                    Bukkit.getLogger().log(Level.WARNING, "[MCDatabase|JDOM] Warning! Database " + dbFile.getName() + " was corrupt. Creating backup and starting new Database!");
                    File backup = new File(dbFile.getPath() + ".backup");
                    if (!FileUtil.copy(dbFile, backup)) {
                        Bukkit.getLogger().log(Level.SEVERE, "[MCDatabase|JDOM] Failed to rename old database!" + backup.toString());
                    }
                }
                //dbFile.createNewFile();
                doc = new Document();
                rootNode = new JDOMDBNode(doc, "root");
            }
        }
        List<Element> childs = rootNode.getElement().getChildren();
        //TODO make this threaded. may even use fork join pool :D
        for (Element e : childs) {
            JDOMDBNode node = new JDOMDBNode(doc, e);
            if (node == null || !node.isValid() || node.getType() != JDOMDBNode.Type.NODE_GROUP) {
                continue;
            }
            nodes.put(node.getElement().getName(), node);
            groups.put(node.getElement().getName(), new JDOMDatabaseGroup(doc, node));
        }

    }

    public void save() throws IOException {
        //File xmlFile = new File(SpoutRPG.getInstance().getDataFolder(),SpoutRPG.getInstance().getRPGConfig().getString("Database.JDOM.location", "database/jdom/db.xml"));
        if (dbFile == null) {
            return;
        }
        if (!dbFile.exists()) {
            dbFile.mkdirs();
            dbFile.createNewFile();
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        // display nice nice
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, new FileOutputStream(dbFile));
        doc = null;
        groups.clear();
        nodes.clear();
    }

    public boolean groupExists(String name) {
        return groups.containsKey(name);
    }

    public File getFile() {
        return dbFile;
    }

    public boolean equals(Object obj) {
        if (obj instanceof JDOMDatabase) {
            return ((JDOMDatabase) obj).doc.equals(doc);
        }
        return false;
    }

    public void flush() {
        for (JDOMDBNode node : nodes.values()) {
            node.delete();
        }
        groups.clear();
        nodes.clear();
    }

    public void close() {
        //Nothing needed since we do everything in save
    }
}
