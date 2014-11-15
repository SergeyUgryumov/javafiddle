package com.javafiddle.git;

import com.javafiddle.git.util.PackageNameUtility;
import java.io.File;
import java.io.IOException;

/**
 * Class responsible for saving the project tree to the file system.
 * @author roman
 */
public class SavingTree {
    private GitTree project;
    private String username;
    private String sep = System.getProperty("file.separator");
    private String prefix = System.getProperty("user.directory") 
            + sep + "javafiddle_git_data" + sep;
    
    public SavingTree(GitTree project, String username) {
        this.project = project;
        this.username = username;
    }
    /**
     * Creates one directory for one user.
     * Recognizes if the user is a guest or a real user.
     * if username matches "^guest////.*$", we create the dir in guest folder
     */
    public void createUserDir() throws IOException {
        File userdir = new File(prefix + this.username);
        //This function is called exactly one time. Nothing bad if we clear everything
        //up at this moment. At least I hope I'm right.
        if (userdir.exists()) {
            //Delete it! Whatever it is!
        }
        boolean dirWasCreated = userdir.mkdir();
        if (dirWasCreated) return;
        throw new IOException("Couldn't create the " + this.username + " folder.\n");
    }
    public void createProjectDir(String projectName) throws IOException {
        String projectDirName = prefix + this.username + sep + projectName;
        File projectDir = new File(projectDirName);
        if (projectDir.exists()) {
            //Delete it.
            //Or throw an exception. I haven't decided yet.
        }
        if (projectDir.mkdir()) return;
        throw new IOException("Couldn't create the " + this.username + sep 
                + projectName +  " folder.\n");
    }
    public void createPackageDir(String projectName, String packageName) throws IOException {
        //Assuming that createPackageDir is called only after createProjectDir
        String packageDirName = prefix + this.username + sep + projectName;
        while (packageName.length() > 0) {
            packageDirName += sep + PackageNameUtility.getNextFolder(packageName);
            packageName = PackageNameUtility.getNextSubfolder(packageName);
            File pack = new File(packageDirName);
            if (!pack.exists()) {
                pack.mkdir();
            }
        }
        File packageDir = new File(packageDirName);
        if (packageDir.exists()) {
            //Delete it.
            //Or throw an exception. I haven't decided yet.
        }
        if (packageDir.mkdir()) return;
        throw new IOException("Couldn't create the " + packageDir +  " folder.\n");
    }
    public void createClassFile(String projectName, String packageName, String className) throws IOException {
        String clazzName = prefix + this.username + sep + projectName + sep
                + PackageNameUtility.getPathFromPackage(packageName) + sep + className;
        File clazz = new File(clazzName);
        if (clazz.exists()) {
            //Need to decide.
        }
        if (clazz.createNewFile()) return;
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
