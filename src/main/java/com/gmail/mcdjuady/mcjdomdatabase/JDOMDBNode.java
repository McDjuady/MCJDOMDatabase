/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.mcdjuady.mcjdomdatabase;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

/**
 *
 * @author maximilian.micko
 */
public class JDOMDBNode {

    /**
     * Type describes what kind of value the Node holds
     */
    public static enum Type {

        /**
         * String type
         */
        NODE_STRING,
        /**
         * Int type
         */
        NODE_INT,
        /**
         * float type
         */
        NODE_FLOAT,
        /**
         * double type
         */
        NODE_DOUBLE,
        /**
         * boolean type
         */
        NODE_BOOLEAN,
        /**
         * byte type
         */
        NODE_BYTE,
        /**
         * group type
         */
        NODE_GROUP,
        /**
         * root type
         */
        NODE_ROOT,
    }
    private Object value; //Store it as an object, since it can be a JDOMDBNode
    private Type type;
    private Element elem; //Store the Element since we may need it to create new
    //JDOMDBNodes
    private Element parent; //Used to append new Nodes
    private Document doc; //Store the Document, to delete and append nodes

//----------------------------Existing Node Constructor-------------------------
    /**
     *
     * @param d Document  - The document, the new Element should be added to
     * @param e Element   - The element the node should refer to
     */
    public JDOMDBNode(Document d, Element e) {
        //Init
        if (d == null || e == null) {
            return;
        }
        doc = d;
        elem = e;
        //Let's check if we are a node wirh a parent node or the root node
        parent = null;
        Parent pa = e.getParent();
        //If we actualy are the root node, mark it!
        if (pa instanceof Document) {
            if (pa.equals(d) && d.getRootElement().equals(e)) {
                type = Type.NODE_ROOT;
            }
            return;
        }
        //Since only Document and Element implement parent, we can be sure that
        //we are an Element
        parent = (Element) pa;

        String elemType = e.getAttributeValue("Type");
        type = Type.valueOf("NODE_" + elemType.toUpperCase());
        if (type == null) {
            Bukkit.getLogger().log(Level.INFO, "[JDOMDatabase|JDOM] Failed to load DBNode " + e.getName() + ", since type " + elemType + " isn't supported!");
            return;
        }
        //We store null if we have a group, since group-node handling is done
        //in the group itself
        value = (type == Type.NODE_GROUP || type == Type.NODE_ROOT) ? null : e.getText();

    }

//---------------------------New Node Constructor-------------------------------
    /**
     *
     * @param d Document  - The document, the new Element should be added to
     * @param s Name      - name of the new Node
     * @param p Parent    - The parent node, where it should be attached
     */
    public JDOMDBNode(Document d, String s, Element p) {
        this(d, s, p, Type.NODE_STRING);
    }

    /**
     * 
     * @param d Document  - The document, the new Element should be added to
     * @param s Name      - name of the new Node
     * @param p Parent    - The parent node, where it should be attached
     * @param t Type      - Node Type. See JDOMDBNode.Type
     */
    public JDOMDBNode(Document d, String s, Element p, Type t) {
        if (d == null || s == null || t == null || p == null || s.isEmpty()) {
            return;
        }
        doc = d;
        type = t;
        elem = new Element(s);
        elem.setAttribute("Type", t.toString().substring(5));
        //Let's make sure, our parent is either a root node or a database group,
        //since normal values don't have childs
        if (p.getAttributeValue("Type").equalsIgnoreCase("group") || d.getRootElement().equals(p)) {
            p.addContent(elem);
        }
    }

//---------------------------New Root Node Constructor--------------------------
    /**
     *
     * @param d Document  - The document, the new Element should be added to
     * @param s Name      - name of the new Node
     */
    public JDOMDBNode(Document d, String s) {
        if (d == null || s == null || s.isEmpty() || d.hasRootElement()) {
            return;
        }
        doc = d;
        elem = new Element(s);
        elem.setAttribute("Type", "root");
        doc.setRootElement(elem);
        type = Type.NODE_ROOT;
    }
//-----------------------------Methods------------------------------------------

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean delete() {
        if (type == Type.NODE_ROOT) {
            return false;
        }
        return parent.removeContent(elem);
    }

    public Element getParent() {
        return parent;
    }

    public Element getElement() {
        return elem;
    }

    public void setValue(Object obj) {
        //Skipp checking for now, since it doesn't seem to work
        if (type == Type.NODE_ROOT || type == Type.NODE_GROUP) {
            return;
        }
        value = obj;
        elem.setText(String.valueOf(obj));
    }

    public boolean isValid() {
        return type != null && elem != null;
    }

    public String toString() {
        return "JDOMDBNode [name=" + (elem == null ? "null" : elem.getName()) + " valid=" + isValid() + "]";
    }
}
