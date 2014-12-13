package com.javafiddle.web.services.utils;

import com.javafiddle.core.ejb.ProjectManager;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.tree.*;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.inject.Inject;

public class Utility {
    @EJB
    private ProjectManager pm;
    
    public static Long parseId(String idString) {
        if(isInteger(idString))
            return Long.parseLong(idString);
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
            if (idString.endsWith("_srcfolder"))
                idString = idString.substring(0, idString.length() - "_srcfolder".length());
            return Long.parseLong(idString);
        }
        return -1L;
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
   
}
