package com.sangeeth.gitbot.retrofitEndPoints;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

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


    /**
     * Gets the All Issues
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


    /**
     * Gets the projects
     *
     * @param gitRepo - name of the repositaory
     * @param gitOwner - name of the owner
     * @param gitSha - id of the commit
     *
     * @return
     */
    @GET("repos/{gitOwner}/{gitRepo}/commits/{sha}")
    Call<Object> getDefineCommit(@Path("gitRepo") String gitRepo, @Path("gitOwner") String gitOwner , @Path("sha") String gitSha);


    /**
     * Gets the projects
     *
     * @param query - queary and offset
     *
     * @return
     */
    @Headers({"Accept: application/vnd.github.cloak-preview"})
    @GET("/search/commits")
    Call<Object> getCommitOnDate(@QueryMap (encoded = true) HashMap<String, Object> query);


    /**
     * Gets the projects
     *
     * @param query - queary and offset
     *
     * @return
     */
    @GET("repos/{gitOwner}/{gitRepo}/commits")
    Call<Object> getCommitByDateTime(@Path("gitRepo") String gitRepo, @Path("gitOwner") String gitOwner , @QueryMap HashMap<String, Object> query);

    /**
     * Gets the file content
     *
     * @param gitRepo - name of the repositaory
     * @param gitOwner - name of the owner
     * @param gitSha - id of the commit
     *
     * @return
     *  //https://raw.githubusercontent.com/sriThariduSangeeth/TestAndroid/c5a063d4427d83b868319775e1b50ec42144dce9/Change%20text.rtf
     */
    @GET("{gitOwner}/{gitRepo}/{sha}/{file}")
    Call<ResponseBody> getFileDownload(@Path("gitRepo") String gitRepo, @Path("gitOwner") String gitOwner , @Path("sha") String gitSha , @Path("file") String filename);


}
