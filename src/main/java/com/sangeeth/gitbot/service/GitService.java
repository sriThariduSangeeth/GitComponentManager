package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ConfigXmlProperty;
import com.sangeeth.gitbot.core.ReadPropertyFile;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import com.sangeeth.gitbot.util.ExecutorServiceManager;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.Nullable;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
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
    private String repoName;
    private String ownaerName;
    private Configuration reader;
    private GitRetrofitDrive gitRetrofitDrive;
    public ETLJsonObjectMapper instance = ETLJsonObjectMapper.getInstance();

    /** @ex : http://localhost:8990/dataservice/git/instance1/getIssueList */

    @GET
    @Path("/{instanceName}/getIssueList/{state}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefectList(@PathParam("instanceName") String instanceName , @PathParam("state") String state) {
        reader = ReadPropertyFile.getInstance().config();
        Properties properties;

        properties = gitServiceHelper.getPropertiesList(instanceName);

        gitRetrofitDrive = new GitRetrofitDrive(properties);


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
    @Path("/{instanceName}/geEventList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEventList(@PathParam("instanceName") String instanceName) {
        reader = ReadPropertyFile.getInstance().config();
        Properties properties;

        properties = gitServiceHelper.getPropertiesList(instanceName);
        gitRetrofitDrive = new GitRetrofitDrive(properties);

        if(properties.getPropertyMap().containsKey("repo") && properties.getPropertyMap().containsKey("owner")){
            repoName = properties.getPropertyMap().get("repo").toString();
            ownaerName = properties.getPropertyMap().get("owner").toString();

            ExecutorServiceManager.executorService.execute(() -> {
                gitServiceHelper.getAllEvents(gitRetrofitDrive, instance  , repoName , ownaerName );
            });
        }

        return "";
    }

    @POST
    @Path("{instanceName}/{repoName}/gitWebHook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String gitWebHook(@PathParam("instanceName") String instanceName , @PathParam("repoName") String repoName , @Context Request request , @Nullable String body) {

        reader = ReadPropertyFile.getInstance().config();
        Properties properties = gitServiceHelper.getPropertiesList(instanceName);
        gitRetrofitDrive = new GitRetrofitDrive(properties.getPropertyMap().get("baseUrl"));

        Map<String , Object> response = instance.stringToMap(body);

        if(ObjectUtils.notEqual(response, null) || ObjectUtils.notEqual(0,response.size())){
            List<Map<String , Object>> commit = (List<Map<String, Object>>) response.get("commits");
            for (Map<String , Object> subCommit: commit) {
                System.out.println(subCommit);
            }
        }else {
            return "response not valid.";
        }
        return "ok";
    }

}
