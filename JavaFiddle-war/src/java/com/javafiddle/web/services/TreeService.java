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
import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.TreeFile;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * The TreeService class is responsible for handling operations around the project 
 * tree. It includes operations with files, packages, correctness checks, etc. 
 */
@Path("tree")
@RequestScoped
public class TreeService {
    @EJB
    private ProjectManager pm;
    
    @EJB
    private JFPackageBean packBean;
    
    @EJB
    private JFProjectBean projBean;
    
    @EJB
    private JFClassBean classBean;
    
    @EJB
    private UserBean userBean;
    
    @Inject
    private ISessionData sd;
    
    @GET
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {
        String treeInJSON;
        if (sd.getUserId() == null)
            sd.reset();
        System.out.println("Current project: " + sd.getCurrentProjectId());
        System.out.println("Current user: " + sd.getUserId());
        if(sd.getCurrentProjectId() == 0L)
            treeInJSON = pm.getExampleTree();
        else 
            treeInJSON = pm.projectsOfUserToJSON(sd.getUserId());
        return Response.ok(treeInJSON, MediaType.APPLICATION_JSON).build();
    }
        
    @POST
    @Path("addPackage")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addPackage(
            @Context HttpServletResponse response,
            @FormParam("packageName") String packageName,
            @FormParam("projectName") String projectName
            ) {
        if (packageName == null || projectName == null)
            return Response.status(401).build();
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        packBean.addPackage(projBean.getProjectByName(sd.getUserId(), projectName).getId(),
                packageName);
        return Response.ok().build();
    }
     
    @POST
    @Path("addFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFile(
            @Context HttpServletResponse response,
            @FormParam("className") String className,
            @FormParam("packageName") String packageName,
            @FormParam("projectName") String projectName,
            @FormParam("type") String type
            ) {
        if (className == null || packageName == null || projectName == null || type == null)
            return Response.status(401).build();
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        Long projId = projBean.getProjectByName(sd.getUserId(), projectName).getId();
        Long packId = packBean.getPackageByName(projId, packageName).getId();
        Long time = (new Date()).getTime();
        Long newClassId = classBean.addClass(className + ".java", "", packId, time).getId();
        
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(newClassId), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("rename")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response renameElement(
            @Context HttpServletResponse response,
            @FormParam("id") String idString,
            @FormParam("name") String name
            ) {
        if (idString == null || name == null)
            return Response.status(401).build();
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        
        Long id = Utility.parseId(idString);
        
        //Had to do this dirty code, because idk how to make it better.
        JFProject proj = this.projBean.getProjectById(id);
        JFPackage pack = this.packBean.getPackageById(id);
        JFClass clazz = this.classBean.getClassById(id);
        if (proj == null && pack == null && clazz == null
                || proj != null && pack != null && clazz == null
                || proj != null && pack == null && clazz != null
                || proj == null && pack != null && clazz != null) {
            return Response.status(401).build();
        }
        if (proj != null) projBean.rename(id, name);
        if (pack != null) packBean.rename(id, name);
        if (clazz != null) classBean.rename(id, name);
        
        return Response.ok().build();
    }
       
    @POST
    @Path("remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(
            @Context HttpServletRequest request,
            String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
                if (sd.getUserId() == 0L) {
        sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        
        Long id = Utility.parseId(idString);
        
        //Same thing. Had to copypast the shitcode. Sorry.
        JFProject proj = this.projBean.getProjectById(id);
        JFPackage pack = this.packBean.getPackageById(id);
        JFClass clazz = this.classBean.getClassById(id);
        if (proj == null && pack == null && clazz == null
                || proj != null && pack != null && clazz == null
                || proj != null && pack == null && clazz != null
                || proj == null && pack != null && clazz != null) {
            return Response.status(401).build();
        }
        if (proj != null) projBean.delete(id);
        if (pack != null) packBean.delete(id);
        if (clazz != null) classBean.delete(id);
        
        return Response.ok().build();
    }
    
    @GET
    @Path("rprojname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightProjectName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name
            ) {
        if (name == null)
            return Response.status(401).build();
        
        
        if(name.matches("([a-zA-Z][a-zA-Z0-9_]*)"))
            return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
        
        return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();
    }
            
    @GET
    @Path("rpackname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightPackageName(
            @Context HttpServletRequest request,
            @QueryParam("packageName") String packageName,
            @QueryParam("projectName") String projectName
            ) {
        if (packageName == null || projectName == null)
            return Response.status(401).build();
        
        if (!packageName.matches("(([a-zA-Z][a-zA-Z0-9]*)(\\.)?)+"))
            return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();

        if (packageName.endsWith("."))
            packageName = packageName.substring(0, packageName.length()-1);
        
        JFProject proj = projBean.getProjectByName(sd.getUserId(), projectName);
        
        if (projBean.getNamesOfPackagesOfProject(proj.getId()).contains(packageName))
            return Response.ok("\"wrongprojectname\"", MediaType.APPLICATION_JSON).build();
        
        return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("rclassname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isRightClassName(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam("packageName") String packageName,
            @QueryParam("projectName") String projectName
            ) {
        if (name == null || packageName == null || projectName == null)
            return Response.status(401).build();
        
        if (name.endsWith(".java"))
            name = name.substring(0, name.length()-5);
        
        if (!name.matches("([a-zA-Z][a-zA-Z0-9_]*)"))
            return Response.ok("\"wrongname\"", MediaType.APPLICATION_JSON).build();
                
        if (!name.endsWith(".java"))
            name += ".java";
                
        JFProject proj = projBean.getProjectByName(sd.getUserId(), projectName);
        
        if (proj == null)
            return Response.ok("\"unknownproject\"", MediaType.APPLICATION_JSON).build();
        JFPackage pack = packBean.getPackageByName(proj.getId(), packageName);
        if (pack == null)
            return Response.ok("\"unknownpack\"", MediaType.APPLICATION_JSON).build();
        
        if (packBean.getNamesOfClassesOfPackage(pack.getId()).contains(name))
            return Response.ok("\"used\"", MediaType.APPLICATION_JSON).build();
        return Response.ok("\"ok\"", MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("projectslist")
    public Response getProjectsList(
            @Context HttpServletRequest request
            ) {
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        List<String> projects = pm.getNamesOfProjectsOfUser(sd.getUserId());

        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(projects), MediaType.APPLICATION_JSON).build();        
    }
    
    @GET
    @Path("packageslist")
    public Response getPackagesList(
            @Context HttpServletRequest request,
            @QueryParam("projectname") String projectname
            ) {
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        JFProject project = projBean.getProjectByName(sd.getUserId(), projectname);
        if (project == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(projBean.getNamesOfPackagesOfProject(project.getId())), 
                MediaType.APPLICATION_JSON).build();
    }
    
    // other/temp
    //
    @GET
    @Path("lasthash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastHash(
            @Context HttpServletRequest request
            ) {
        //Разобраться позжа
        return Response.ok(null, MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("filedata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileData(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        
        TreeFile tf;
        String content;
        switch(idString) {
            case "about_tab":
                tf = new TreeFile("About", "help"); 
                break;
            case "shortcuts_tab":
                tf = new TreeFile("Shortcuts", "help"); 
                break;
            default:
                Long id = Utility.parseId(idString);
                tf = new TreeFile(classBean.getClassName(id),"runnable");
                content = classBean.getClassContent(id);
        }
        if (tf == null)
            return Response.status(410).build();
        
        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(tf));
        return Response.ok(gson.toJson(tf), MediaType.APPLICATION_JSON).build();
    }
        
    @GET 
    @Path("namebyid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNameByID(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
        if (idString == null)
            return Response.status(401).build();
        if (sd.getUserId() == 0L) {
            sd.setUserId(userBean.addNewGuestUser());
            sd.setCurrentProjectId(projBean.addNewDefaultProject(sd.getUserId()));
        }
        
        Long id = Utility.parseId(idString);
          
        JFProject proj = this.projBean.getProjectById(id);
        JFPackage pack = this.packBean.getPackageById(id);
        JFClass clazz = this.classBean.getClassById(id);
        String name;
        if (proj == null && pack == null && clazz == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (proj != null && pack == null && clazz == null) {
            return Response.ok("\"" + proj.getProjectName() +"\"", MediaType.APPLICATION_JSON).build();
        }
        if (proj == null && pack != null && clazz == null) {
            return Response.ok("\"" + pack.getPackageName() +"\"", MediaType.APPLICATION_JSON).build();
        }
        if (proj == null && pack == null && clazz != null) {
            return Response.ok("\"" + clazz.getClassName() +"\"", MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build(); //Two are not nulls;
    }
}
