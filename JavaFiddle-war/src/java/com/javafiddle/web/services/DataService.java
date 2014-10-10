package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.saving.GetProjectRevision;
import com.javafiddle.saving.ProjectRevisionSaver;
import com.javafiddle.web.services.data.ISessionData;
import com.javafiddle.web.services.utils.FileRevision;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.utils.SessionUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
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
        if (hash == null)
            return Response.status(401).build();
        
        GetProjectRevision gpr = new GetProjectRevision(hash);
        if (!gpr.treeExists())
            return Response.status(404).build();
        sd.resetData();
        sd.setTree(gpr.getTree());
        sd.getIdList().putAll(sd.getTree().getIdList());
        ArrayList<TreeFile> filesList = new ArrayList<>();
        filesList.addAll(sd.getIdList().getFileList().values());
        for (TreeFile tf : filesList) {
            int id = tf.getId();
            long time = tf.getTimeStamp();
            String text = gpr.getFile(sd.getIdList().getPackage(tf.getPackageId()).getName(), id, time);
            TreeMap<Long, String> revisions = new TreeMap<>();
            revisions.put(time, text);
            sd.getFiles().put(id, revisions);
        }
        return Response.ok().build();
    }
    
    /**
     * Saves a revision to the disk and generates the hash of the revision. 
     * @param request
     * @return 
     */
    @POST
    @Path("project")
    public Response saveProjectRevision (
            @Context HttpServletRequest request
            ) {
        HttpSession session = SessionUtils.getSession(request, true);
        Long currentUserId = SessionUtils.getUserId(session);
        
        // save project to disk
        Date date = new Date();

        ProjectRevisionSaver spr = new ProjectRevisionSaver(sd.getTree(), sd.getIdList(), sd.getFiles());			
        spr.saveRevision();	
        
        String hash = sd.getTree().getHashes().getBranchHash() + sd.getTree().getHashes().getTreeHash();
        
        return Response.ok(hash, MediaType.TEXT_PLAIN).build();
    }
    
    /**
     * Returns the hierarchy of the project basing on it's hash. 
     * @param request
     * @return 
     */
    @GET
    @Path("hierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTreeHierarchy( 
            @Context HttpServletRequest request
            ) {
        GetProjectRevision gpr = new GetProjectRevision(sd.getTree().getHashes());
        ArrayList<Tree> trees = gpr.findParents(sd.getTree());
        if (trees == null)
           return Response.ok().build();
        ArrayList<String> names = new ArrayList<>();
        for (Tree entry : trees)
           names.add(sd.getTree().getHashes().getBranchHash() + entry.getHashes().getTreeHash());
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(names), MediaType.APPLICATION_JSON).build();
    }
           
    /**
     * Returns wrapped into a Response object FileRevision object containing 
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
    public Response getFileRevision(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        FileRevision fr;
        String text;
        Gson gson = new GsonBuilder().create();
        switch(idString) {
            case "about_tab":
                text = GetProjectRevision.readFile(ISessionData.PREFIX + ISessionData.SEP + "static" + ISessionData.SEP + "about");
                fr = new FileRevision(new Date().getTime(), text);
                break;
            case "shortcuts_tab":
                text = GetProjectRevision.readFile(ISessionData.PREFIX + ISessionData.SEP + "static" + ISessionData.SEP + "shortcuts");
                fr = new FileRevision(new Date().getTime(), text);
                break;
            default:
                int id = Utility.parseId(idString);
                if (sd.getIdList().getFile(id) == null)
                    return Response.status(406).build();
                long time = sd.getIdList().getFile(id).getTimeStamp();
                if (time == 0) {
                    fr = new FileRevision(0, "");
                } else {   
                    text = sd.getFiles().get(id).get(time);
                    fr = new FileRevision(time, text);
                }  
                break;
        }
        
        return Response.ok(gson.toJson(fr), MediaType.APPLICATION_JSON).build();
    }
    //Finally I found a class that really uses Revisions class...
    /**
     * Adds current revision into the system.
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
            ) {
        if (idString == null || timeStamp == 0 || value == null)
            return Response.status(401).build();
        
        int addResult;
        switch(idString) {
            case "about_tab":
                addResult = 406;
                break;
            case "shortcuts_tab":
                addResult = 406;
                break;
            default:
                int id = Utility.parseId(idString);
                Revisions revisions = new Revisions(sd.getIdList(), sd.getFiles());
                addResult = revisions.addFileRevision(id, timeStamp, value);
        }
        return Response.status(addResult == 304 ? 200 : addResult).build();
    }
}
