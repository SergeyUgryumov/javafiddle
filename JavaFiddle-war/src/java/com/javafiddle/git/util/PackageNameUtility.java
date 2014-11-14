package com.javafiddle.git.util;

/**
 * Class with methods for parsing package names.
 * @author roman
 */
public class PackageNameUtility {
    /**
     * Returns the next folder in the package name.<br/>
     * For example, for com.javafiddle.git it will return com
     * @param packageName
     * @return 
     */
    public static String getNextFolder(String packageName) {
        return packageName.substring(0,packageName.indexOf(".")-1);
    }
    /**
     * Returns the rest of the package's name.<br/>
     * com.javafiddle.git -> javafiddle.git
     * @param packageName
     * @return 
     */
    public static String getNextSubfolder(String packageName) {
        if (packageName.indexOf(".") > 0) {
            return packageName.substring(packageName.indexOf(".")+1);   
        }
        return "";
    }
    public static String getPathFromPackage(String packageName) {
        return packageName.replace(".", System.getProperty("file.separator"));
    }
}
