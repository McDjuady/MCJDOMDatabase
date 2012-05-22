/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.mcdjuady.mcjdomdatabase;

import com.gmail.mcdjuady.mcdatabase.DatabaseGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author maximilian.micko
 */
public class JDOMDatabaseGroup implements DatabaseGroup {

    private Document doc;
    private JDOMDBNode groupNode;
    private String groupName;
    private Map<String, JDOMDBNode> nodes = new HashMap<String, JDOMDBNode>();
    private Map<String, DatabaseGroup> groups = new HashMap<String, DatabaseGroup>();

    public JDOMDatabaseGroup(Document d, JDOMDBNode node) {
        if (d == null || node == null || !node.isValid() || !(node.getType() == JDOMDBNode.Type.NODE_GROUP || node.getType() == JDOMDBNode.Type.NODE_ROOT)) {
            return;
        }
        doc = d;
        groupNode = node;
        Element elem = node.getElement();
        groupName = node.getElement().getName();
        @SuppressWarnings("unchecked")
        List<Element> childs = elem.getChildren();
        for (Element e : childs) {
            JDOMDBNode childNode = new JDOMDBNode(doc, e);
            if (!childNode.isValid()) {
                continue;
            } else if (childNode.getType() == JDOMDBNode.Type.NODE_ROOT) {
                continue;
            } else if (childNode.getType() == JDOMDBNode.Type.NODE_GROUP) {
                groups.put(e.getName(), new JDOMDatabaseGroup(d, childNode));
                nodes.put(e.getName(), childNode);
            } else {
                nodes.put(e.getName(), childNode);
            }
        }
    }

    public JDOMDatabaseGroup(Document d, JDOMDBNode parent, String s) {
        if (d == null || parent == null || !parent.isValid() || s == null || s.isEmpty()) {
            return;
        }
        doc = d;
        groupName = s;
        groupNode = new JDOMDBNode(doc, groupName, parent.getElement(), JDOMDBNode.Type.NODE_GROUP);
    }

    public String getString(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_STRING)) {
            return "";
        }
        return (String) node.getValue();
    }

    public int getInt(String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_INT)) {
            return 0;
        }
        return Integer.valueOf((String) node.getValue());
    }

    public float getFloat(String name) {
        if (name == null || name.isEmpty()) {
            return 0.0f;
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_FLOAT)) {
            return 0.0f;
        }
        return Float.valueOf((String) node.getValue());
    }

    public double getDouble(String name) {
        if (name == null || name.isEmpty()) {
            return 0.0d;
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_DOUBLE)) {
            return 0.0d;
        }
        return Double.valueOf((String) node.getValue());
    }

    public boolean getBoolean(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_BOOLEAN)) {
            return false;
        }
        return Boolean.valueOf((String) node.getValue());
    }

    public DatabaseGroup getSubgroup(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return groups.get(name);
    }

    public String addString(String name, String val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_STRING) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_STRING);
        node.setValue(val);
        nodes.put(name, node);
        return val;
    }

    public int addInt(String name, int val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_INT) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_INT);
        node.setValue((Integer) val);
        nodes.put(name, node);
        return val;
    }

    public float addFloat(String name, float val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_FLOAT) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_FLOAT);
        node.setValue(val);
        nodes.put(name, node);
        return val;
    }

    public double addDouble(String name, double val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_DOUBLE) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_DOUBLE);
        node.setValue(val);
        nodes.put(name, node);
        return val;
    }

    public boolean addBoolean(String name, boolean val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_BOOLEAN) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_BOOLEAN);
        node.setValue(val);
        nodes.put(name, node);
        return val;
    }

    public DatabaseGroup createSubgroup(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (groups.containsKey(name)) {
            return groups.get(name);
        }
        JDOMDatabaseGroup group = new JDOMDatabaseGroup(doc, groupNode, name);
        groups.put(name, group);
        nodes.put(name, group.getGroupNode());
        return group;
    }

    public Object destroyEntry(String name) {
        if (!nodes.containsKey(name)) {
            return null;
        }
        JDOMDBNode node = nodes.remove(name);
        Object val = node.getValue();
        node.delete();
        return val;
    }

    public DatabaseGroup destroySubgroup(String name) {
        if (!groups.containsKey(name) || !nodes.containsKey(name)) {
            return null;
        }
        nodes.remove(name).delete();
        return groups.remove(name);
    }

    public Collection<DatabaseGroup> getSubgroups() {
        return groups.values();
    }

    public String getName() {
        return groupName;
    }

    public boolean hasSubgroup(String name) {
        return groups.containsKey(name);
    }

    public void copyTo(DatabaseGroup group) {
        Collection<String> nodeNames = nodes.keySet();
        for (String nodeName : nodeNames) {
            JDOMDBNode node = nodes.get(nodeName);
            if (node == null || !node.isValid()) {
                continue;
            }
            addValueTo(group, nodeName, node);
        }
        Collection<String> groupNames = groups.keySet();
        for (String gName : groupNames) {
            DatabaseGroup from = groups.get(gName);
            if (from == null) {
                continue;
            }
            DatabaseGroup to = group.createSubgroup(gName);
            from.copyTo(to);
        }
    }

    private void addValueTo(DatabaseGroup group, String name, JDOMDBNode node) {
        if (node.getType() == JDOMDBNode.Type.NODE_BOOLEAN) {
            group.addBoolean(name, Boolean.valueOf((String) node.getValue()));
        } else if (node.getType() == JDOMDBNode.Type.NODE_BYTE) {
            group.addByte(name, Byte.valueOf((String) node.getValue()));
        } else if (node.getType() == JDOMDBNode.Type.NODE_DOUBLE) {
            group.addDouble(name, Double.valueOf((String) node.getValue()));
        } else if (node.getType() == JDOMDBNode.Type.NODE_FLOAT) {
            group.addFloat(name, Float.valueOf((String) node.getValue()));
        } else if (node.getType() == JDOMDBNode.Type.NODE_INT) {
            group.addInt(name, Integer.valueOf((String) node.getValue()));
        } else if (node.getType() == JDOMDBNode.Type.NODE_STRING) {
            group.addString(name, (String) node.getValue());
        }
        //Skip groups since they are handled diffrent
        //Skip root nodes since they shouldn't occur in groups
    }

    public JDOMDBNode getGroupNode() {
        return groupNode;
    }

    @Override
    public byte getByte(String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        JDOMDBNode node = nodes.get(name);
        if (node == null || !(node.getType() == JDOMDBNode.Type.NODE_BYTE)) {
            return 0;
        }
        return Byte.valueOf((String) node.getValue());
    }

    @Override
    public byte addByte(String name, byte val) {
        if (nodes.containsKey(name)) {
            JDOMDBNode node = nodes.get(name);
            if (node.getType() != JDOMDBNode.Type.NODE_BYTE) {
                return val;
            }
            node.setValue(val);
            return val;
        }
        JDOMDBNode node = new JDOMDBNode(doc, name, groupNode.getElement(), JDOMDBNode.Type.NODE_BYTE);
        node.setValue((Byte) val);
        nodes.put(name, node);
        return val;
    }
}
