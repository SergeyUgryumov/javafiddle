package com.javafiddle.git;

import java.util.HashMap;

/**
 * Handles structure of one particular project.
 * The keys in the HashMap are the names of projects, packages and files respectively
 * @author roman
 */
public class GitTree {
    private HashMap<String, //Project Name
             HashMap<String, //Package Name
              HashMap<String,String>>>  //Class Name, it's contents
            projects;
    
    public GitTree() {
        this.projects = new HashMap<>();
    }
    public HashMap<String,HashMap<String,String>> getProject(String projectName) {
        return projects.get(projectName);
    }
    public HashMap<String,String> getPackage(String projectName, String packageName) {
        return projects.get(projectName).get(packageName);
    }
    public String getClass(String projectName, String packageName, String className) {
        return projects.get(projectName).get(packageName).get(className);
    }
    public void addClass(String projectName, String packageName, String className, String value) {
        projects.get(projectName).get(packageName).put(className, value);
    }
    public void addPackage(String projectName, String packageName) {
        projects.get(projectName).put(packageName, null);
    }
    public void addProject(String projectName) {
        projects.put(projectName, null);
    }
    public void deleteClass(String projectName, String packageName, String className) {
        projects.get(projectName).get(packageName).remove(className);
    }
    public void deletePackage(String projectName, String packageName) {
        projects.get(projectName).remove(packageName);
    }
    public void deleteProject(String projectName) {
        projects.remove(projectName);
    }
    public void renameClass(String projectName, String packageName, 
            String oldClassName, String newClassName) {
        String value = projects.get(projectName).get(packageName).get(oldClassName);
        projects.get(projectName).get(packageName).put(newClassName, value);
        projects.get(projectName).get(packageName).remove(oldClassName);
    }
    public void renamePackage(String projectName, String oldPackageName,
            String newPackageName) {
        HashMap<String,String> oldPackage = 
                projects.get(projectName).get(oldPackageName);
        projects.get(projectName).put(newPackageName,oldPackage);
        projects.get(projectName).remove(oldPackageName);
    }
    public void renameProject(String oldProjectName, String newProjectName) {
        HashMap<String,HashMap<String,String>> oldProject = projects.get(oldProjectName);
        projects.put(newProjectName, oldProject);
        projects.remove(oldProjectName);
    }
}
