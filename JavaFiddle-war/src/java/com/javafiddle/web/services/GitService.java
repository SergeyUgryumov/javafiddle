package com.javafiddle.web.services;

import com.javafiddle.web.services.data.ISessionData;
import com.javafiddle.web.services.utils.Utility;
import com.javafiddle.web.tree.Tree;
import com.javafiddle.web.tree.TreeProject;
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
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("git")
@RequestScoped
public class GitService {
    
    @Inject
    private ISessionData sd;
    /**
     * Receives changed contents of the file from the client, and saves it to the FS.
     * @param request
     * @param idString 
     * @param time
     * @param value
     * @return 
     */
    @Path("file")
    @POST
    @RequestScoped
    public Response saveFile(
            @Context HttpServletRequest request,
            @FormParam("id") String idString,
            @FormParam("timeStamp") long time,
            @FormParam("value") String value
    ) throws IOException {
        int id = Utility.parseId(idString);
            FileWriter log = new FileWriter("/home/roman/tmp/javafiddle.log");
            log.write("/webapi/git/file POST\n");
            log.write("id: " + idString + "\n");
            log.write("time: " + Long.toString(time) + "\n");
            log.write("value: " + value + "\n");
            Tree a = sd.getTree();
            for (TreeProject tp: a.getProjects()) {
                log.write("Project name: " + tp.getName() + "\n");
            }
        log.close();
        return Response.status(200).build();
    }
}
