package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.core.ejb.JFClassBean;
import com.javafiddle.core.ejb.JFPackageBean;
import com.javafiddle.core.ejb.JFProjectBean;
import com.javafiddle.core.ejb.ProjectManager;
import com.javafiddle.pool.Task;
import com.javafiddle.pool.TaskPool;
import com.javafiddle.pool.TaskType;
import com.javafiddle.runner.Compilation;
import com.javafiddle.runner.Execution;
import com.javafiddle.runner.Killer;
import com.javafiddle.runner.LaunchPermissions;
import com.javafiddle.saving.ProjectRevisionSaver;
import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.templates.ClassTemplate;
import com.javafiddle.maven.PomCreator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



@Path("run")
@RequestScoped
public class RunService {
    
    @EJB
    private ProjectManager pm;
    
    @EJB
    private JFPackageBean packBean;
    
    @EJB
    private JFClassBean classBean;
    
    @EJB
    private JFProjectBean projBean;
    
    private String prefix = System.getProperty("user.home") + File.separator + "javafiddle_data" + File.separator;    
    
    
    public static File createFile(String path, String value, String pack) throws IOException {
		String fullPath = path;
		
		if (pack != null) {
			String[] packDirs = pack.split("\\.");
			for (int i = 0; i < packDirs.length; i++) {
				fullPath += packDirs[i] + File.separator;
			}
		}
		fullPath += "AppTest.java";
				
		File file = new File(fullPath);
		file.getParentFile().mkdirs();
		file.createNewFile();
		
		FileWriter wrt = new FileWriter(file);
	
		wrt.append(value);
		//Запись в файл
		wrt.flush();
		wrt.close();
		
		return file;
	}
    
    public void createServiceFiles(String path, String pack) {
        ClassTemplate ct = new ClassTemplate("AppTest", "JUnit", pack);
		
		try {
			createFile(path + File.separator + "src"
					+ File.separator + "test"
					+ File.separator + "java"
					+ File.separator, ct.getValue(), pack);
		} catch (IOException ex) {
			Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		PomCreator pc = new PomCreator(path);
                pc.setGroupId(pack);
                pc.setArtifactId((projBean.getProjectById(sd.getCurrentProjectId())).getProjectName());
                pc.setName((projBean.getProjectById(sd.getCurrentProjectId())).getProjectName());
		pc.createFile();
    }
    
    public class MainClassScanner {
        File mainConf;
        String pathtoProject;
        private long mainClassID;
        private long mainClassPackID;
        
        public MainClassScanner(){
        }
        
        public MainClassScanner(String pathtoProject) {
            mainConf = new File(pathtoProject + File.separator + "main-id.conf");
            this.pathtoProject = pathtoProject;            
        }
        
        public void manage() {
            String s;
            if (mainConf.exists()) {             
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mainConf)));
                        s = br.readLine();
                        mainClassID = Long.parseLong(s);
                        s = br.readLine();
                        mainClassPackID = Long.parseLong(s);
                        br.close();
                        if (!check(prefix + pm.getPathForClass(mainClassID))) mainConf.delete();
                    } catch (IOException ex) {
                        Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
                    }              
            } 
            if (!mainConf.exists()) {
                try {
                    if (scan()) mainConf.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mainConf)));
                    s = Long.toString(mainClassID);
                    bw.write(s);
                    bw.flush();
                    bw.newLine();
                    s = Long.toString(mainClassPackID);
                    bw.write(s);
                    bw.flush();
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }            
        }
        
        public boolean scan() throws IOException{
            boolean found = false;
                        
            List<Long> packages = projBean.getPackagesOfProject(sd.getCurrentProjectId());
            List<Long> classes;
            Long packId;
            Long classId;
                    
            Iterator<Long> packIterator = packages.iterator();
            Iterator<Long> classIterator;
            while ((packIterator.hasNext()) && (!found)) {
                packId = packIterator.next();
                classes = packBean.getClassesOfPackage(packId);
                classIterator = classes.iterator();
                while ((classIterator.hasNext()) && (!found)) {
                    classId = classIterator.next();
                    if (check(prefix + pm.getPathForClass(classId))) {
                        found = true;
                        mainClassID = classId;
                        mainClassPackID = packId;
                    }               
                }                
            }
            
            return found;
        }
        
        public boolean check(String className) throws IOException {
            boolean found = false;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(className))));
            String s;
            while(((s = br.readLine()) != null) && (!found)) {
                if (s.matches("^.*public\\s+static\\s+void\\s+main.*$")) found = true;
            }
            return found;     
        }
        
        public long getMainClassID(){
            return mainClassID;
        }
        
        public long getMainClassPackID(){
            return mainClassPackID;
        }
    }
    
    @Inject
    private ISessionData sd;
    
    /**
     * compile function collects all files of the project and sends them as 
     * parameters to an object of Compilation class. All of that is run by 
     * class Task.
     * @param request
     * @return 
     */
    @POST
    @Path("compile")
    public Response compile(
            @Context HttpServletRequest request
            ) {        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {                
                String path = prefix + pm.getPathForProject(sd.getCurrentProjectId());
                                
                MainClassScanner mcs = new MainClassScanner(path);
                mcs.manage();                
                long mainClassPackID = mcs.getMainClassPackID();
                String mainClassPack = packBean.getPackageName(mainClassPackID);
                
                Compilation comp = new Compilation(path);
                comp.addToOutput("Creating of service files");
                createServiceFiles(path, mainClassPack);
		comp.addToOutput("Service files created successfully");
                comp.addToOutput("Launching of maven invocation, please wait...");                
                
                Task task = new Task(TaskType.COMPILATION, comp);
                TaskPool.getInstance().add(task);
               
                task.start();           

                return null;
            }
        }, LaunchPermissions.getSecureContext());
        return Response.ok().build();
    }
    /**
     * execute function collects all runnable files and sends them to class 
     * Execution. Altogether it goes to the Task class and is executed there. 
     * @param request
     * @return 
     */
    @POST
    @Path("execute")
    public Response execute(
            @Context HttpServletRequest request
            ) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                
                String path = prefix + pm.getPathForProject(sd.getCurrentProjectId());
                String pathtoFile;
                
                MainClassScanner mcs = new MainClassScanner(path);
                mcs.manage();
                long mainClassID = mcs.getMainClassID();
                long mainClassPackID = mcs.getMainClassPackID();
                
                String className = classBean.getClassName(mainClassID);
                className = className.replace(".java", "");
                pathtoFile = packBean.getPackageName(mainClassPackID) + "." + className;          
                
                                
                Task task = new Task(TaskType.EXECUTION, new Execution("-cp " + path + File.separator + "target" + File.separator + "classes", pathtoFile));
                TaskPool.getInstance().add(task);
                try {
                    task.start();
                } finally {
                    Killer killer = new Killer(task);
                    killer.start();
                }

                return null;
            }
        }, LaunchPermissions.getSecureContext());
        
        return Response.ok().build();
    }
    /**
     * Combines compile and execute methods. 
     * @param request
     * @return 
     */
    @POST
    @Path("compilerun")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response compileAndRun(
            @Context HttpServletRequest request
            ) {        
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                
                String path = prefix + pm.getPathForProject(sd.getCurrentProjectId());
                                
                MainClassScanner mcs = new MainClassScanner(path);
                mcs.manage();                
                long mainClassPackID = mcs.getMainClassPackID();
                long mainClassID = mcs.getMainClassID();
                String mainClassPack = packBean.getPackageName(mainClassPackID);
                
                String className = classBean.getClassName(mainClassID);
                className = className.replace(".java", "");
                String pathtoFile = packBean.getPackageName(mainClassPackID) + "." + className; 
                
                Compilation comp = new Compilation(path);
                comp.addToOutput("Creating of service files");
                createServiceFiles(path, mainClassPack);
		comp.addToOutput("Service files created successfully");
                comp.addToOutput("Launching of maven invocation, please wait...");                
                
                Task task1 = new Task(TaskType.COMPILATION, comp);
                TaskPool.getInstance().add(task1);
                
                task1.start();
                
                try{
                    task1.join();
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
                } finally { 
                    if(!task1.isError()) {                        
                        Task task2 = new Task(TaskType.EXECUTION, new Execution("-cp " + path + File.separator + "target" + File.separator + "classes", pathtoFile));
                        TaskPool.getInstance().add(task2);
                        try {
                            task2.start();
                        } finally {
                            Killer killer = new Killer(task2);
                            killer.start();
                        }
                    }
                }
                return null;
            }
        }, LaunchPermissions.getSecureContext());   
        
        return Response.ok().build();
    }
    /**
     * Sends output (if any) to the web-page. If there is nothing to put out,
     * it waits for the thread for some time.
     * @param request
     * @return 
     */
    @GET
    @Path("output")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutput(
            @Context HttpServletRequest request
            ){
        Task task = TaskPool.getInstance().get(TaskPool.getInstance().size()-1);
        if (task == null)
            return Response.status(404).build();

        for (int i = 0; i < 20; i++) {
            try {
                String result;
                if ((result = task.getOutputStream()) == null) 
                    Thread.sleep(100);
                else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(result);
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, result);
                    while ((result = task.getOutputStream()) != null) {
                        list.add(result);
                        Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, result);
                    }
                    Gson gson = new GsonBuilder().create();
                    return Response.ok(gson.toJson(list), MediaType.APPLICATION_JSON).build();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.ok().build();
    }
    /**
     * Sends input to the program. 
     * @param request
     * @param input Values to be sent to the program.
     * @return 
     */
    @POST
    @Path("send")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setInput(
            @Context HttpServletRequest request,
            @FormParam("input") String input
            ) {
            
            Task task = TaskPool.getInstance().get(TaskPool.getInstance().size()-1);
            OutputStream stream = task.getInputStream();
            try {
                String str = input + "\n";
                stream.write(str.getBytes());
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(RunService.class.getName()).log(Level.SEVERE, null, ex);
            }
        return Response.ok().build();
    }
}
