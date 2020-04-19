package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ConfigXmlProperty;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import com.sangeeth.gitbot.util.Converters;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
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

                if (!ObjectUtils.notEqual(responseList, null) || !ObjectUtils.notEqual(0, responseList.size())) {
                    eventDataAvailable = false;
                } else {
                    finalEventList.addAll(responseList);
                    getFixFileChanges(retrofitDriver , responseList , instance, repo , owner);
                }

                page ++;
            }while (eventDataAvailable);

            if(eventfullcount == finalEventList.size()){
                System.out.println("this is ok");
            }

        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }

    }

    public void getFixFileChanges(GitRetrofitDrive retrofitDriver, List<Map<String, Object>>  respon , ETLJsonObjectMapper instance , String repo , String owner){


        respon.forEach( eventResObject -> {

            String eventState = String.valueOf(eventResObject.get("event"));

            if(eventResObject.containsKey("commit_id") && eventState.equalsIgnoreCase("Closed")){

                Map<String , Object> issue = instance.toMap(eventResObject.get("issue"));
                String sha = String.valueOf(eventResObject.get("commit_id"));
                String date = String.valueOf(issue.get("created_at")).split("T")[0];

                boolean eventDataAvailable = true;
                int page = 1;
                int limit = 100;

                List<Map<String, Object>> commitListitem = new LinkedList<>();
                HashMap<String , Object> comm = new LinkedHashMap<>();
                comm.put("q","repo:"+owner+"/"+repo+"+committer-date:"+date);
                comm.put("per_page", limit);

                try {

                    Call<Object> correctCommit = ((GitAPI)retrofitDriver.invoke()).getDefineCommit(repo , owner , sha );
                    Map<String, Object> rescorrectCommit = instance.toMap(correctCommit.execute().body());

                    do{
                        comm.put("page", page);
                        Call<Object> commitList = ((GitAPI)retrofitDriver.invoke()).getCommitOnDate(comm);
                        Map<String, Object> rescommitList = instance.toMap(commitList.execute().body());
                        List<Map<String, Object>> ff = (List<Map<String, Object>>) rescommitList.get("items");

                        if (!ObjectUtils.notEqual(ff, null) || !ObjectUtils.notEqual(0, ff.size())) {
                            eventDataAvailable = false;
                        }else {
                            commitListitem.addAll(ff);
                        }
                        page ++;
                    }while (eventDataAvailable);

                    Map<String, Object> closetWrongCommit = getClosestCommit(commitListitem, String.valueOf(issue.get("created_at")));

                    //rescorrectCommit

                    //closetWrongCommit
                    //https://api.github.com/repos/sriThariduSangeeth/TestAndroid/contents/Change%20text.rtf?ref=c5a063d4427d83b868319775e1b50ec42144dce9
                    //to download file
                    //https://raw.githubusercontent.com/sriThariduSangeeth/TestAndroid/c5a063d4427d83b868319775e1b50ec42144dce9/Change%20text.rtf

                    CharStream charStream = CharStreams.fromFileName("");

                    System.out.println(rescorrectCommit);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }

            }

        });
    }

    public Map<String , Object> getClosestCommit(List<Map<String, Object>> rescommitList , String datetime){

        long time = Converters.convertDateTimeToEpoch(datetime);
        Map<String,Object> committer = (Map<String, Object>) ((Map<String, Object>) rescommitList.get(0).get("commit")).get("committer");
        long firstTime = Converters.convertDateTimeToEpoch(String.valueOf(committer.get("date")).split("\\.")[0]+"z");
        long distance = Math.abs(firstTime - time);
        int idx = 0;

        for(int c = 1; c < rescommitList.size(); c++){
            Map<String,Object> comm = (Map<String, Object>) ((Map<String, Object>) rescommitList.get(c).get("commit")).get("committer");
            long valTime = Converters.convertDateTimeToEpoch(String.valueOf(comm.get("date")).split("\\.")[0]+"z");
            long cdistance = Math.abs(valTime - time);
            if(cdistance < distance ){
                idx = c;
                distance = cdistance;
            }
        }

        return rescommitList.get(idx);
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
        sb.append("repo:").append(owner).append("/").append(repo).append("+").append("type:").append(type).append("+").append("state:").append(state);
        return sb.toString();
    }
}
