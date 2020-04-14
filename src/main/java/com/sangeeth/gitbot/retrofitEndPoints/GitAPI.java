package com.sangeeth.gitbot.retrofitEndPoints;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.HashMap;

/**
 * @author dtsangeeth
 * @created 25 / 03 / 2020
 * @project GitComponentManager
 *
 * Git Rest API
 *
 */

public interface GitAPI {

    @GET("/rest/api/2/project")
    Call<Object> getProjects();

    /**
     * Gets the projects
     *
     * @param gitRepo - name of the repositaory
     * @param gitOwner - name of the owner
     *
     * @return
     */
    @GET("repos/{gitOwner}/{gitRepo}/issues")
    Call<Object> getAllClosedIssues(@Path("gitRepo") String gitRepo, @Path("gitOwner") String gitOwner);


    /**
     * Gets the projects
     *
     * @param query - queary and offset
     *
     * @return
     */
    @GET("/search/issues")
    Call<Object> getIssues(@QueryMap (encoded = true) HashMap<String, Object> query);


    @GET("/repos/{gitOwner}/{gitRepo}/issues/events")
    Call<Object> getEventList(@Path("gitRepo") String gitRepo, @Path("gitOwner") String gitOwner,@QueryMap HashMap<String, Object> query);


}
