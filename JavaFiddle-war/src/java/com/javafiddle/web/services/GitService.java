package com.javafiddle.web.services;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("git")
public class GitService {
    
    @Path("file")
    @POST
    @RequestScoped
    public Response addFile(
            @Context HttpServletRequest request,
            @FormParam("id") String idString,
            @FormParam("value") String value
    ) {
        return Response.status(200).build();
    }
}
