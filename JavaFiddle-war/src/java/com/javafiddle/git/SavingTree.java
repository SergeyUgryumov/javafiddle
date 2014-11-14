package com.javafiddle.git;

/**
 * Class responsible for saving the project tree to the file system.
 * @author roman
 */
public class SavingTree {
    private Tree project;
    private String username;
    public SavingTree(Tree project, String username) {
        this.project = project;
        this.username = username;
    }
    public void createUserDir() {
    }
    public void createProjectDir(String projectName) {
    }
    public void createPackageDir(String projectName, String packageName) {
    }
    public void createClassFile(String projectName, String packageName, String className) {
    }
    public void deleteProjectDir(String projectName) {
    }
    public void deletePackageDir(String projectName, String packageName) {
    }
    public void deleteClassFile(String projectName, String packageName, String className) {
    }
    public void renameProjectDir(String oldProjectName, String newProjectName) {
    }
    public void renamePackageDir(String projectName, String oldPackageName, 
            String newPackageName) {
    }
    public void renameClassFile(String projectName, String packageName, 
            String oldClassName, String newClassName) {
    }
}
