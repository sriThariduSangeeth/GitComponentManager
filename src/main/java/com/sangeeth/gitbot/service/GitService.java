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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        ETLJsonObjectMapper instance = ETLJsonObjectMapper.getInstance();

        if(properties.getPropertyMap().containsKey("repo") && properties.getPropertyMap().containsKey("owner")){
            repoName = properties.getPropertyMap().get("repo").toString();
            ownaerName = properties.getPropertyMap().get("owner").toString();

            ExecutorServiceManager.executorService.execute(() -> {
                gitServiceHelper.getAllEvents(gitRetrofitDrive, instance  , repoName , ownaerName );
            });
        }

        return "";
    }

}
