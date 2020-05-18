package com.sangeeth.gitbot.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ReadPropertyFile;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.util.client.elastic.ElasticClient;
import com.sangeeth.gitbot.util.client.stanford.NlpPipline;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import com.sangeeth.gitbot.util.ExecutorServiceManager;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ObjectUtils;


import org.apache.http.util.TextUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.lang.Nullable;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.io.IOException;
import java.util.*;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */


@Path("/git")
public class GitService {


    private static Logger logger = LogManager.getLogger(GitService.class);
    private GitServiceHelper gitServiceHelper = new GitServiceHelper();
    private GitHookServiceHelper gitHook = new GitHookServiceHelper();
    private String repoName;
    private String ownaerName;
    private Configuration reader;
    private GitRetrofitDrive gitRetrofitDrive;
    private Properties properties;
    private StanfordCoreNLP stanfordCoreNLP;
    public ETLJsonObjectMapper instance = ETLJsonObjectMapper.getInstance();

    /** @ex : http://localhost:8990/dataservice/git/instance1/getIssueList */

    @GET
    @Path("/{instanceName}/{ownerName}/{repoName}/getIssueList/{state}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefectList(@PathParam("instanceName") String instanceName , @PathParam("repoName") String repo, @PathParam("ownerName") String owner, @PathParam("state") String state) {
        reader = ReadPropertyFile.getInstance().config();

        properties = gitServiceHelper.getPropertiesList(instanceName);

        gitRetrofitDrive = new GitRetrofitDrive(properties.getPropertyMap().get("authToken") , properties.getPropertyMap().get("baseUrl"));


        if(properties.getPropertyMap().containsKey("repo") && properties.getPropertyMap().containsKey("owner")){
            repoName = properties.getPropertyMap().get("repo").toString();
            ownaerName = properties.getPropertyMap().get("owner").toString();

            ExecutorServiceManager.executorService.execute(() -> {
               gitServiceHelper.getAllIssuesData(gitRetrofitDrive, instance  , repoName , ownaerName , state);
            });

//            JSONArray jsonArray = new JSONArray(finalDefectList);
//            JSONObject finalJsonObject = new JSONObject();
//            finalJsonObject.put("data", jsonArray);

        }

        return "";
    }


    @GET
    @Path("/{instanceName}/{ownerName}/{repoName}/getEventList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEventList(@PathParam("instanceName") String instanceName, @PathParam("repoName") String repo, @PathParam("ownerName") String owner) {
        reader = ReadPropertyFile.getInstance().config();

        properties = gitServiceHelper.getPropertiesList(instanceName);
        gitRetrofitDrive = new GitRetrofitDrive(properties.getPropertyMap().get("authToken") , properties.getPropertyMap().get("baseUrl"));

        if(!TextUtils.isEmpty(repo) && !TextUtils.isEmpty(owner)){
            this.repoName = repo.toString();
            this.ownaerName = owner.toString();

            logger.info("get event list point trigger...");

            ExecutorServiceManager.executorService.execute(() -> {
                gitServiceHelper.getAllEvents( gitRetrofitDrive, instance  , repoName , ownaerName  , instanceName);
            });
        }

        return "";
    }

    /** @ex : http://localhost:8990/dataservice/git/dtsangeeth/getUserdefectsList */

    @GET
    @Path("/{userName}/getUserdefectsList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserDefects(@PathParam("userName") String userName) throws IOException {
        reader = ReadPropertyFile.getInstance().config();

        ElasticClient elasticClient = new ElasticClient();
        String queary = "SELECT * FROM found_defects WHERE commiter='"+userName+"'";
        List<Map<String, Object>> resu = elasticClient.xPack(queary.toString());
        String json = new Gson().toJson(resu);

        return json;
    }

    @POST
    @Path("{instanceName}/{ownerName}/{repoName}/gitWebHook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String gitWebHook(@PathParam("instanceName") String instanceName ,
                             @PathParam("repoName") String repoName ,@PathParam("ownerName") String owner , @Context Request request ,
                             @Nullable String body) {

        logger.info("New commit trigger from" + repoName);
        logger.info("analysing commit.....");

        reader = ReadPropertyFile.getInstance().config();
        properties = gitServiceHelper.getPropertiesList(instanceName);
        gitRetrofitDrive = new GitRetrofitDrive(properties.getPropertyMap().get("authToken") , properties.getPropertyMap().get("contentBaseUrl"));

        Map<String , Object> response = instance.stringToMap(body);

        if(ObjectUtils.notEqual(response, null) || ObjectUtils.notEqual(0,response.size())){
            List<Map<String , Object>> commit = (List<Map<String, Object>>) response.get("commits");
            for (Map<String , Object> subCommit: commit) {

                ExecutorServiceManager.executorService.execute(() -> {
                    gitHook.checkCommitViolation(gitRetrofitDrive,subCommit ,repoName , owner );
                });

            }

        }else {
            return "response not valid.";
        }
        return "ok";
    }

}
