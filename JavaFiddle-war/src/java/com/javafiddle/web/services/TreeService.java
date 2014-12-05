package com.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javafiddle.core.ejb.JFClassBean;
import com.javafiddle.core.ejb.JFPackageBean;
import com.javafiddle.core.ejb.JFProjectBean;
import com.javafiddle.core.ejb.ProjectManager;
import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.revisions.Revisions;
import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.services.utils.Utility;
import static com.javafiddle.web.tree.IdNodeType.FILE;
import static com.javafiddle.web.tree.IdNodeType.PACKAGE;
import static com.javafiddle.web.tree.IdNodeType.PROJECT;
import com.javafiddle.web.tree.TreeFile;
import com.javafiddle.web.tree.TreeNode;
import com.javafiddle.web.tree.TreePackage;
import com.javafiddle.web.tree.TreeProject;
import java.util.ArrayList;
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
    
    @Inject
    private ISessionData sd;
    
    @GET
    @Path("tree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTree(
            @Context HttpServletRequest request
            ) {
        String treeInJSON;
        if(sd.getCurrentProjectId() == 0)
            treeInJSON = Utility.addExampleTree();
        else 
            treeInJSON = pm.projectTreeToJSON(sd.getCurrentProjectId());
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
        Long projId = projBean.getProjectByName(sd.getUserId(), projectName).getId();
        Long packId = packBean.getPackageByName(projId, packageName).getId();
        classBean.addClass(className, null, packId);
        Long newClassId = classBean.getClassByName(packId, className).getId();
        
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
        
        int id = Utility.parseId(idString);
        classBean.renameClass(classBean.getClassById(Integer.toUnsignedLong(id)).getId(), name);
        
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
        
        int id = Utility.parseId(idString);
        //Запилить после ответа Сереги.
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
                int id = Utility.parseId(idString);
                tf = new TreeFile(classBean.getClassName(Integer.toUnsignedLong(id)),"runnable");
                content = classBean.getClassContent(Integer.toUnsignedLong(id));
        }
        if (tf == null)
            return Response.status(410).build();
        
        Gson gson = new GsonBuilder().create();
        return Response.ok(gson.toJson(tf), MediaType.APPLICATION_JSON).build();
    }
        
    @GET 
    @Path("namebyid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNameByID(
            @Context HttpServletRequest request,
            @QueryParam("id") String idString
            ) {
//        if (idString == null)
//            return Response.status(401).build();
//        
//        int id = Utility.parseId(idString);
//        
//        TreeNode tn = sd.getIdList().get(id);
//        
//        if (tn != null)
//            return Response.ok("\"" + tn.getName() +"\"", MediaType.APPLICATION_JSON).build();
//        else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
