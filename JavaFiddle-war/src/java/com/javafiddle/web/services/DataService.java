package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.core.ejb.JFClassBean;
import com.javafiddle.core.ejb.JFPackageBean;
import com.javafiddle.core.ejb.JFProjectBean;
import com.javafiddle.core.ejb.ProjectManager;
import com.javafiddle.core.ejb.UserBean;
import com.javafiddle.core.jpa.JFClass;
import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.saving.FileSaver;
import com.javafiddle.saving.GetProjectRevision;
import com.javafiddle.saving.ProjectRevisionSaver;
import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.services.utils.FileRevision;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.utils.SessionUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * DataService class is responsible for saving and getting the project's 
 * hierarchy to and from the disk.
 * <br/>
 * Strongly relies on <i>com.javafiddle.saving</i> and <i>com.javafiddle.revisions</i> packages!!!
 */
@Path("data")
@RequestScoped
public class DataService {
    
    @Inject
    private ISessionData sd;
    
    @EJB
    ProjectManager pm;
    
    @EJB
    JFProjectBean jfprojectBean;
    
    @EJB
    JFPackageBean jfpackageBean;
    
    @EJB
    JFClassBean jfclassBean;
    
    @EJB
    UserBean userBean;
    /**
     * Returns the project with specified hash. "Project" is the tree of the 
     * project including all files. 
     */
    @POST
    @Path("changeproject")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getProject(
            @Context HttpServletRequest request,
            @FormParam("projecthash") String hash
            ) {
        System.out.println("Data Service: hash: " + hash);
        if (hash == null)
            return Response.status(401).build();
//        a();
//        sd.setTree(gpr.getTree());
//        sd.getIdList().putAll(sd.getTree().getIdList());
//        ArrayList<TreeFile> filesList = new ArrayList<>();
//        filesList.addAll(sd.getIdList().getFileList().values());
//        for (TreeFile tf : filesList) {
//            int id = tf.getId();
//            long time = tf.getTimeStamp();
//            String text = gpr.getFile(sd.getIdList().getPackage(tf.getPackageId()).getName(), id, time);
//            TreeMap<Long, String> revisions = new TreeMap<>();
//            revisions.put(time, text);
//            sd.getFiles().put(id, revisions);
//        }
        return Response.ok().build();
    }
    
    /**
     * Pulls the data from the database and saves it to the disk.<br/>
     * Used to save a revision to the disk and generate the hash of the revision. 
     * @param request
     * @return 
     */
    @POST
    @Path("project")
    public Response saveProject (
            @Context HttpServletRequest request
            ) {
        //Pull all the data from the database to the filesystem.
        if (sd.getCurrentProjectId() == 0) {
            this.addNewGuestProjectToServer();
        }
        JFProject proj = jfprojectBean.getProjectById(sd.getCurrentProjectId());
        for (Long packId: jfprojectBean.getPackagesOfProject(sd.getCurrentProjectId())) {
            for (Long classId: jfpackageBean.getClassesOfPackage(packId)) {
                try {
                    //1 - get file's path
                    String path = pm.getPathForClass(classId);
                    //2 - get the content of the file
                    String oldContent = FileSaver.getContentOfFile(path);
                    //3 - get the content from the db
                    String newContent = jfclassBean.getClassContent(classId);
                    //4 - compare
                    if (newContent.equals(oldContent)) 
                        continue;
                    //5 - if smth is different - overwrite it
                    FileSaver.writeToFile(path, newContent);
                } catch (IOException ex) {
                    Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
              
        return Response.ok("STILL HAVE TO ADD THE FUCKING HASH FROM GIT", MediaType.TEXT_PLAIN).build();
    }
    
    /**
     * Still need to understand what this shit is. OK, got it, it goes to Git.
     * <br/>Returns the hierarchy of the project basing on it's id in JSON format.
     * <br/>Used to return the hierarchy of the project basing on it's hash. 
     * @param request
     * @return 
     */
    @GET
    @Path("hierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTreeHierarchy( 
            @Context HttpServletRequest request
            ) {
        
        //To be implemented by git
        
//        GetProjectRevision gpr = new GetProjectRevision(sd.getTree().getHashes());
//        ArrayList<Tree> trees = gpr.findParents(sd.getTree());
//        if (trees == null)
//           return Response.ok().build();
//        ArrayList<String> names = new ArrayList<>();
//        for (Tree entry : trees)
//           names.add(sd.getTree().getHashes().getBranchHash() + entry.getHashes().getTreeHash());
//        Gson gson = new GsonBuilder().create();
        return Response.ok().build();
    }
           
    /**
     * It will be in the Git package.
     * <br/>Returns wrapped into a Response object FileRevision object containing 
     * link to the file with the specified ID.<br/>
     * At least, I (RTur) understand it so.<br/>
     * Separately handles such idStrings as "about_tab" and "shortcuts_tab" for 
     * some reason.
     * @param request
     * @param idString
     * @return 
     */
    @GET
    @Path("file")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileContent(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) throws IOException {
        if (idString == null)
            return Response.status(401).build();
        
        String text;
        Long time = null;
        Gson gson = new GsonBuilder().create();
        switch(idString) {
            case "about_tab":
                text = FileSaver.getContentOfFile("static" + ISessionData.SEP + "about");
                time = (new Date()).getTime();
                break;
            case "shortcuts_tab":
                text = FileSaver.getContentOfFile("static" + ISessionData.SEP + "shortcuts");
                time = (new Date()).getTime();
                break;
            default:
                Long id = Utility.parseId(idString);
                if (jfclassBean.getClassById(id) == null)
                    return Response.status(406).build();
                text = jfclassBean.getClassContent(id);
                time = jfclassBean.getLastSaveTime(id);
                break;
        }
        String jsonResult = "{\"value\":"+gson.toJson(text)+",\"time\":" + time.toString() + "}";
        System.out.println("DataService: " + jsonResult);
        return Response.ok(jsonResult, MediaType.APPLICATION_JSON).build();
    }
    //Finally I found a class that really uses Revisions class...
    /**
     * Saves one single file to the disk.
     * <br/>Used to add current revision into the system.
     * @param request
     * @param idString
     * @param timeStamp
     * @param value
     * @return 
     */
    @POST
    @Path("file")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response saveFile(
            @Context HttpServletRequest request,
            @FormParam("id") String idString,
            @FormParam("timeStamp") long timeStamp,
            @FormParam("value") String value
            ) throws FileNotFoundException, UnsupportedEncodingException {
        if (idString == null || timeStamp == 0 || value == null)
            return Response.status(401).build();
        
        int addResult = 0;
        switch(idString) {
            case "about_tab":
                addResult = 406;
                break;
            case "shortcuts_tab":
                addResult = 406;
                break;
            default:
                Long id = Utility.parseId(idString);
                if (Objects.equals(id, ISessionData.defaultClassId)) { //example class from example tree
                    System.out.println("DS: mess with new guest user");
                    Long newUserId = userBean.addUser("guest", "", "guest@javafiddle.ru");
                    sd.setUserId(newUserId);
                    id = this.addNewGuestProjectToServer();
                    addResult = 213;
                }
                JFClass clazz = this.jfclassBean.getClassById(id);
                jfclassBean.updateContent(id, value);
                //1 - get class'es path in the file system
                String path = pm.getPathForClass(id);
                //2 - get the content from the database
                String newContent = jfclassBean.getClassById(id).getContent();
                {
                    try {
                        //3 - save it to the file system
                        FileSaver.writeToFile(path, newContent);
                    } catch (IOException ex) {
                        Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (addResult != 213) 
                    addResult = 200;
        }
        return Response.status(Response.Status.OK).build();
    }
    /**
     * Returns the new id of the newly added class.
     * <br/>
     * @return 
     */
    public Long addNewGuestProjectToServer() {
        //Here is all about the database
       JFProject newProj = jfprojectBean.addNewProject("MyFirstProject", sd.getUserId());
       sd.setCurrentProjectId(newProj.getId());
       JFPackage newPack = jfpackageBean.addPackage(jfprojectBean.getProjectByName(sd.getUserId(), 
               "MyFirstProject").getId(), "com.javafiddle.main");
       JFClass newClass = jfclassBean.addClass("Main.java", "", 
               jfpackageBean.getPackageByName(sd.getCurrentProjectId(), "com.javafiddle.main").getId(), 
               (new Date()).getTime());
       //here comes the file system.
        try {
            FileSaver.createFile(pm.getPathForClass(newClass.getId()));
            FileSaver.writeToFile(pm.getPathForClass(newClass.getId()), newClass.getContent());
        } catch (IOException ex) {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ex);
        }
       return newClass.getId();
    }
}
