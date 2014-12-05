package com.javafiddle.saving;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Contains methods involved into working with the file system.
 * <br/>All of the paths the methods accept are supposed to be relative relatively
 * basic folder for JavaFiddle.
 * @author roman
 */
public class FileSaver {
    private final static String SEP = File.separator;
    private final static String PREFIX = System.getProperty("user.home") 
            + SEP + "javafiddle_data" + SEP;
    
    public static void createFile(String path) throws IOException {
        File file = new File(PREFIX + path);
        if (file.exists()) 
            return;
        file.createNewFile();
    }
    /**
     * Removes previous version of the file and creates new with new content.
     * <br/><br/>
     * Does NOT handle the check whether the file has changed.
     * @param path
     * @param content
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void writeToFile(String path, String content) 
            throws FileNotFoundException, IOException {
        File file = new File(PREFIX + path);
        file.delete();
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file);
        writer.write(content);
        writer.close();
    }
    public static String getContentOfFile(String path) throws FileNotFoundException, IOException {
        File file = new File(PREFIX + path);
        FileInputStream fis = new FileInputStream(file);
        byte [] data = new byte[(int)file.length()];
        fis.read(data);
        fis.close();
        String content = new String(data, "UTF-8");
        return content;
    }
    /**
     * 
     * @param path
     * @param newname part of the word from the last separator to the end.
     */
    public static void renameFile(String path, String newname) {
        File file = new File(PREFIX + path);
        String newPath = PREFIX + path.substring(0, path.lastIndexOf(SEP)) + newname;
        file.renameTo(new File(newPath));
    }
    public static void createDir(String path) {
        File dir = new File(PREFIX + path);
        dir.mkdirs();
    }
    public static void deleteObject(String path) {
        File object;
        if (path.startsWith(PREFIX)) {
            object = new File(path); // for the recursive call.
        }
        else {
            object = new File(PREFIX + path);
        }
        if (object.isFile()) {
            object.delete();
            return;
        }
        for (File internalObject: object.listFiles()) {
            FileSaver.deleteObject(internalObject.getAbsolutePath());
        }
        object.delete();
    }
    
}
