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
     * @param jiraProjectKey - name of the project
     * @return
     */
    @GET("/rest/api/2/project/{jiraProjectKey}")
    Call<Object> getProject(@Path("jiraProjectKey") String jiraProjectKey);

    /**
     * map keys = project , startAt , maxResults
     * map values = projectKey , offset , limit
     * project=<<projectKey>>&startAt=<<offset>>&maxResults=<<limit>>
     *
     * @param query
     * @return
     */
    @GET("/rest/api/2/user/assignable/search")
    Call<Object> getUserAssignable(@QueryMap HashMap<String, Object> query);


}
