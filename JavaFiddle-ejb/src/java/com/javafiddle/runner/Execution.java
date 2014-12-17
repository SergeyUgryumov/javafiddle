package com.javafiddle.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Execution is a class responsible for running the app created by user.
 * It is very similar to Compilation class and might have same issues with 
 * running on non-Unix systems. 
 */
public class Execution implements Launcher, Serializable {

    private String args = "";
    private String pathtoclass = "";
    private String pathtoProject = "";
    private Process process;
    private Boolean killed = false;
    private Queue<String> stream = null;
    private Integer pid = null;

    public Execution(String args, String pathtoclass, String pathtoProject) {
        this.stream = new LinkedList<>();
        this.args = args;
        this.pathtoclass = pathtoclass;
        this.pathtoProject = pathtoProject;
    }

    public Execution(String pathtoclass) {
        this.stream = new LinkedList<>();
        this.pathtoclass = pathtoclass;
    }

    @Override
    public void run() {
//        String OS = System.getProperty("os.name").toLowerCase();
//        if (OS.indexOf("win") >= 0) {
//            // Windows Commands
//        } else {
//            // Linux Commands
//        }
        try {
            File conf = new File(pathtoProject + File.separator + "main-id.conf");
            if (conf.exists()){
            String command = "java " + args + " " + pathtoclass;            
            process = Runtime.getRuntime().exec(command);
            setPid(process);
            printLines(" stdout:", process.getInputStream());
            printLines(" stderr:", process.getErrorStream());
            process.waitFor();
            stream.add(" exitValue() " + process.exitValue());
            stream.add("#END_OF_STREAM#");
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void destroy() {
        if(process != null) {
            process.destroy();
            killed = true;
        }
    }

    @Override
    public void printLines(String name, InputStream ins) throws IOException {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            stream.add(name + " " + line);
        }
    }

    @Override
    public String getOutputStream() {
        return stream.poll();
    }
    
    @Override
    public OutputStream getInputStream() {
        return process.getOutputStream();
    }
    
    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    @Override
    public Boolean streamIsEmpty() {
        return stream.isEmpty();
    }
    
    @Override
    public int getExitCode() {
        return process.exitValue();
    }
    
    @Override
    public int waitFor() {
        try {
            return process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    @Override
    public Integer getPid() {
        return pid;
    }

    private void setPid(Process process) {
   //     if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Class cl = process.getClass();
                Field field = cl.getDeclaredField("pid");
                field.setAccessible(true);
                Object pidObject = field.get(process);
                pid = (Integer) pidObject;
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
            }
   /*     } else {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }
  */  }

    @Override
    public void addToOutput(String line) {
        stream.add(line);
    }

}
