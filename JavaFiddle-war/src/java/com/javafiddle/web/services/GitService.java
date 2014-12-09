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
    UserBean userBean;
    
    @EJB
    ProjectManager pm;
    
    @POST
    @Path("commit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response commit(
            @Context HttpServletRequest request,
            @FormParam("commitMessage") String commitMsg) {
        User user = userBean.getUserById(sd.getUserId());
        GitHandler git = new GitHandler(user.getNickname(),user.getEmail(),
            pm.getPathForProject(sd.getCurrentProjectId()));
        String hash = git.commit(commitMsg);
        return Response.ok(hash,MediaType.TEXT_PLAIN).build();
    }
    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response add(
            @Context HttpServletRequest request,
            @FormParam("classId") String rawClassId){
        Long classId = Utility.parseId(rawClassId);
        User user = userBean.getUserById(sd.getUserId());
        GitHandler git = new GitHandler(user.getNickname(),user.getEmail(),
            pm.getPathForProject(sd.getCurrentProjectId()));
        String path = pm.getPathForClass(classId);
        git.addFileToRepo(path);
        return Response.ok().build();
    }

}
