package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ConfigXmlProperty;
import com.sangeeth.gitbot.fileTansformation.ClassTransformation;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import com.sangeeth.gitbot.util.Converters;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import com.sangeeth.gitbot.util.client.classifier.NaiveBayesClassifier;
import com.sangeeth.gitbot.util.client.elastic.ElasticClient;
import org.apache.commons.lang3.ObjectUtils;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import retrofit2.Call;

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

    public void getAllEvents( GitRetrofitDrive retrofitDriver, ETLJsonObjectMapper instance  ,
                             String repo , String owner , String instanceName ){
//        List<Map<String, Object>> finalEventList = new LinkedList<>();
        try {
            boolean eventDataAvailable = true;
            int page = 1;
            int limit = 50;
            HashMap<String, Object> events = new LinkedHashMap<>();

            do{
                events.put("page" , page);
                events.put("per_page" , limit);

                Call<Object> eventList = ((GitAPI)retrofitDriver.invoke()).getEventList(repo , owner , events);
                List<Map<String, Object>> responseList = instance.toMapList(eventList.execute().body());

                if (!ObjectUtils.notEqual(responseList, null) || !ObjectUtils.notEqual(0, responseList.size())) {
                    eventDataAvailable = false;
                } else {
                    logger.info("One event set catch form" + repo +" and event count : " + responseList.size());

                    getFixFileChanges( retrofitDriver , responseList , instance, repo , owner , instanceName);

                }

                page ++;
            }while (eventDataAvailable);


        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }

    }

    public void getFixFileChanges( GitRetrofitDrive retrofitDriver,
                                  List<Map<String, Object>>  respon , ETLJsonObjectMapper instance , String repo , String owner , String instanceName){
        ClassTransformation classTransformation = new ClassTransformation(owner, repo , instanceName , instance);

        respon.forEach( eventResObject -> {

            String eventState = String.valueOf(eventResObject.get("event"));
            if(ObjectUtils.notEqual(eventResObject.get("commit_id"), null)  && eventState.equalsIgnoreCase("Closed")){

                Map<String , Object> issue = instance.toMap(eventResObject.get("issue"));
                String title = instance.toString(issue.get("title"));
                String body = instance.toString(issue.get("body"));
                String sha = String.valueOf(eventResObject.get("commit_id"));
                long time = Converters.convertDateTimeToEpoch(issue.get("created_at").toString());
                String dateTime = Converters.convertEpochToDateTime(time - 1800).split("\\+")[0];

                HashMap<String , Object> comm = new LinkedHashMap<>();

                comm.put("since", dateTime+"Z");
                comm.put("until", issue.get("created_at"));

                if(NaiveBayesClassifier.getInstance().checkPositiveOrNegative(title) || NaiveBayesClassifier.getInstance().checkPositiveOrNegative(body)){
                    try {

                        //get fix commit
                        Call<Object> correctCommit = ((GitAPI)retrofitDriver.invoke()).getDefineCommit(repo , owner , sha );
                        Map<String, Object> rescorrectCommit = instance.toMap(correctCommit.execute().body());

                        //get issue infected commit
                        Call<Object> commitList = ((GitAPI)retrofitDriver.invoke()).getCommitByDateTime(repo , owner ,comm);
                        List<Map<String, Object>> rescommitList = instance.toMapList(commitList.execute().body());
                        Map<String, Object> closetWrongCommit = getClosestCommit(rescommitList ,issue.get("created_at").toString());

                        Map<String , Object> out = classTransformation.downloadFileAndTransform( issue , instance, rescorrectCommit , closetWrongCommit );

                        // Elastic insert
                        if(ObjectUtils.notEqual(out,null)){
                            ElasticClient elasticClient = new ElasticClient();
                            elasticClient.insert("defects" , out);
                        }

                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }


            }

        });
    }

    public Map<String , Object> getClosestCommit(List<Map<String, Object>> rescommitList , String datetime){

        long time = Converters.convertDateTimeToEpoch(datetime);
        Map<String,Object> committer = (Map<String, Object>) ((Map<String, Object>) rescommitList.get(0).get("commit")).get("committer");
        long firstTime = Converters.convertDateTimeToEpoch(String.valueOf(committer.get("date")));
        long distance = Math.abs(firstTime - time);
        int idx = 0;

        for(int c = 1; c < rescommitList.size(); c++){
            Map<String,Object> comm = (Map<String, Object>) ((Map<String, Object>) rescommitList.get(c).get("commit")).get("committer");
            long valTime = Converters.convertDateTimeToEpoch(String.valueOf(comm.get("date")));
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
