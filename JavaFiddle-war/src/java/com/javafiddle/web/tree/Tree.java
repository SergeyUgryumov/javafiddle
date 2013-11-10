package com.javafiddle.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tree implements Serializable {
    private List<TreeProject> projects = new ArrayList<>();
    
     public TreeProject addProject(IdList idList, String projectName) {
        for (TreeProject temp : projects)
            if (projectName.equals(temp.getName()))
                return null;
        TreeProject tpr = new TreeProject(projectName);
        projects.add(tpr);
        tpr.setId(idList.addId(tpr));
        
        return tpr;
    }
        
    public TreeProject getProjectInstance(IdList idList, String projectName) {
        for (TreeProject temp : projects)
            if (projectName.equals(temp.getName()))
                return temp;
        TreeProject tpr = new TreeProject(projectName);
        projects.add(tpr);
        tpr.setId(idList.addId(tpr));
        
        return tpr;
    }
    
    public void deleteProject(IdList idList, int id) {
        TreeProject tpr = idList.getProject(id);
        for (TreePackage temp : tpr.getPackages()) {
            tpr.deletePackage(idList, temp.getId());
        }
        projects.remove(tpr);
        idList.removeId(id);
    }
}
