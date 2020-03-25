package com.sangeeth.gitbot.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */


@Path("/git")
public class GitService {

    @GET
    @Path("/{instanceName}/getDefectList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefectList(@PathParam("instanceName") String instanceName) {
        System.out.println("hello this is getdefects");
        return "";
    }

}
