package com.javafiddle.web.services;

import com.javafiddle.web.services.sessiondata.ISessionData;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeProject;
import com.javafiddle.core.ejb.*;
import com.javafiddle.core.jpa.*;
import com.javafiddle.git.GitHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("git")
@RequestScoped
public class GitService {
    
    @Inject
    private ISessionData sd;

    @EJB
    JFProjectBean projectBean;
    
    @EJB
    JFPackageBean packageBean;
    
    @EJB
    JFClassBean classBean;
    
    @EJB
    UserBean userBean;
    
    @EJB
    GitHashesBean hashBean;
    
    @EJB
    ProjectManager pm;
    
    @POST
    @Path("commit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response commit(
            @Context HttpServletRequest request,
            @FormParam("commitMessage") String commitMsg) {
        User user = userBean.getUserById(sd.getUserId());
        System.out.println(user.toString());
        GitHandler git = new GitHandler(user.getNickname(),user.getEmail(),
            pm.getPathForProject(sd.getCurrentProjectId()));

        String hash = git.commit(commitMsg);
        System.out.println("GitService(63): " + hash + " " + commitMsg);
        hashBean.addHash(sd.getCurrentProjectId(), (new Date()).getTime(), hash);
        return Response.ok(hash,MediaType.TEXT_PLAIN).build();
    }
    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add(
            @Context HttpServletRequest request,
            @FormParam("classId") String rawClassId){
        System.out.println("GitService.add(80): " + rawClassId);
        Long classId = Utility.parseId(rawClassId);
        User user = userBean.getUserById(sd.getUserId());
        GitHandler git = new GitHandler(user.getNickname(),user.getEmail(),
            pm.getPathForProject(sd.getCurrentProjectId()));
        String path = pm.getPathForClass(classId);
        git.addFileToRepo(path.substring(path.indexOf("src")));
        return Response.ok(rawClassId, MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("addDefaultClass")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addDefaultClass(
            @Context HttpServletRequest request) {
        Long projId = sd.getCurrentProjectId();
        Long packId = packageBean.getPackageByName(projId, ISessionData.defaultPackageName).getId();
        Long classId = classBean.getClassByName(packId,"Main.java").getId();
        
        User user = userBean.getUserById(sd.getUserId());
        GitHandler git = new GitHandler(user.getNickname(),user.getEmail(),
            pm.getPathForProject(sd.getCurrentProjectId()));
        //path contains username and projectname! Git doesn't accept that!
        String path = pm.getPathForClass(classId);        
        //I hope it's gonna work forever. We add everything in the src/folder.
        git.addFileToRepo("src/");
        String res = classId.toString();
        System.out.println("Default ClassID: " + res);
        return Response.ok(res, MediaType.TEXT_PLAIN).build();
    }

}
