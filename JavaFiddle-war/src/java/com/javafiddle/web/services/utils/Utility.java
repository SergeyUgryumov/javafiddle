package com.javafiddle.web.services.utils;

import com.javafiddle.revisions.Revisions;
import com.javafiddle.web.tree.*;
import java.util.TreeMap;

public class Utility {
    public static int parseId(String idString) {
        if(isInteger(idString))
            return Integer.parseInt(idString);
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
            if (idString.endsWith("_srcfolder"))
                idString = idString.substring(0, idString.length() - "_srcfolder".length());
            return Integer.parseInt(idString);
        }
        return -1;
    }
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        
        return true;
    }

    public static void addExampleTree(Tree tree, IdList idList, TreeMap<Integer, TreeMap<Long, String>> files) {
        TreeProject tpr = tree.addProject(idList, "MyFirstProject");
        TreePackage tp = tpr.addPackage(idList, "com.myfirstproject.web");
        TreeFile main = tp.addFile(idList, "runnable", "Main.java");
        
        Revisions revisions = new Revisions(idList, files);
        revisions.addFileRevision(main, idList);
    }
    
    public static String addExampleTree() {
        return "{\"projects\":[{\"id\": 301, \"name\": \"first_proj\", "
                + "\"packages\": [{\"id\":302, \"name\": \"firstpackage\", \"parentId\": \"301\", "
                + "\"files\": [{\"id\": 303, \"name\": \"main\", \"type\": \"java\"}]}]}]} ";
    }
}
