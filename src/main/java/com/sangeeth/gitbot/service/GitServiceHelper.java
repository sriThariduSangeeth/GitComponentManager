package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ConfigXmlProperty;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import retrofit2.Call;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */

public class GitServiceHelper {

    private static Logger logger = LogManager.getLogger(GitServiceHelper.class);

    public void getAllIssuesData(GitRetrofitDrive retrofitDriver, ETLJsonObjectMapper instance  ,
                                 String repo , String owner , String state ){
        List<Map<String, Object>> finalDefectList = new LinkedList<>();
        try {
            boolean userDataAvailable = true;
            int page = 1;
            int limit = 100;
            int issuefullcount = 0;
            HashMap<String, Object> defects = new LinkedHashMap<>();

            do{
                defects.put("q" , setSearchQueary(repo , owner , "issue" , state));
                defects.put("page" , page);
                defects.put("per_page" , limit);

                Call<Object> issuesList = ((GitAPI)retrofitDriver.invoke()).getIssues(defects);
                Map<String, Object> issueList = instance.toMap(issuesList.execute().body());
                issuefullcount = ((Double)issueList.get("total_count")).intValue();
                List<Map<String, Object>> issues = (List<Map<String, Object>>) issueList.get("items");

                if (!ObjectUtils.notEqual(issueList, null) || !ObjectUtils.notEqual(0, (issues).size())) {
                    userDataAvailable = false;
                } else {
                    finalDefectList.addAll(issues);
                }

                page ++;
            }while (userDataAvailable);

            if(issuefullcount == finalDefectList.size()){
                System.out.println("this is ok");
            }

        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }

    }

    public void getAllEvents(GitRetrofitDrive retrofitDriver, ETLJsonObjectMapper instance  ,
                                 String repo , String owner  ){
        List<Map<String, Object>> finalEventList = new LinkedList<>();
        try {
            boolean eventDataAvailable = true;
            int page = 1;
            int limit = 100;
            int eventfullcount = 0;
            HashMap<String, Object> events = new LinkedHashMap<>();

            do{
                events.put("page" , page);
                events.put("per_page" , limit);

                Call<Object> eventList = ((GitAPI)retrofitDriver.invoke()).getEventList(repo , owner , events);
                List<Map<String, Object>> responseList = instance.toMapList(eventList.execute().body());

                System.out.println(responseList);
//                List<Map<String, Object>> eventslist = (List<Map<String, Object>>) responseList.get("items");

//                if (!ObjectUtils.notEqual(responseList, null) || !ObjectUtils.notEqual(0, (eventslist).size())) {
//                    eventDataAvailable = false;
//                } else {
//                    finalEventList.addAll(eventslist);
//                }

                page ++;
            }while (eventDataAvailable);

            if(eventfullcount == finalEventList.size()){
                System.out.println("this is ok");
            }

        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }

    }

    public Properties getPropertiesList (String instanceName){

        final Properties[] properties = new Properties[1];

        ConfigXmlProperty.getInstance().getProperties("git").getPropertiesList().forEach(properties1 -> {
            if (!ObjectUtils.notEqual(properties1.getInstanceName(), instanceName)) {
                properties[0] = properties1;
            }
        });

        return properties[0];
    }

    public String setSearchQueary(String repo , String owner , String type , String state){
        StringBuilder sb = new StringBuilder();
//        repo:iluwatar/java-design-patterns+type:issue+state:closed
        sb.append("repo:").append(owner).append("/").append(repo).append("+").append("type:").append(type).append("+").append("state:").append(state);
        return sb.toString();
    }
}
