package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.fileTansformation.ClassTransformation;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import com.sangeeth.gitbot.util.client.elastic.ElasticClient;
import com.sangeeth.gitbot.util.client.stanford.NlpPipline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import okhttp3.ResponseBody;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import retrofit2.Call;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author dtsangeeth
 * @created 18 / 04 / 2020
 * @project GitComponentManager
 */
public class GitHookServiceHelper {

    private static Logger logger = LogManager.getLogger(GitHookServiceHelper.class);

    private String repoName = null;
    private String ownerName = null;
    private String commit_Sha = null;
    private String commiter = null;
    private ElasticClient elasticClient;


    public void checkCommitViolation( GitRetrofitDrive gitRetrofitDrive, Map<String, Object> respon  ,  String repoName, String owner){

        this.repoName = repoName;
        this.ownerName = owner;
        this.commit_Sha = (String) respon.get("id");

        String text = respon.get("message").toString();
        this.commiter = (String) ((Map<String, Object>) respon.get("committer")).get("username");

        List<String> bagOfWords = NlpPipline.getInstance().commitConvertToBagOfWords(text);

        List<String> added_files = (List<String>) respon.get("added");

        List<String> modify_files = (List<String>) respon.get("modified");

        if (ObjectUtils.notEqual(added_files,null) && ObjectUtils.notEqual(added_files.size(),0)){

            for (String file : added_files) {
                downFileAndGetScore(gitRetrofitDrive , file);
            }
        }

        if (ObjectUtils.notEqual(modify_files,null) && ObjectUtils.notEqual(modify_files.size(),0)){
            for (String file: modify_files) {
                downFileAndGetScore(gitRetrofitDrive , file);
            }
        }

        logger.info("session over.");

    }


    public void downFileAndGetScore(GitRetrofitDrive gitRetrofitDrive, String filename){

        String classFileOut = null;
        Map<String , Object> ela_input = new HashMap<>();

        try {

            Map<String , String> pram = new HashMap<>();
            Call<ResponseBody> ClassFile = ((GitAPI)gitRetrofitDrive.invoke()).getFileDownload(repoName , ownerName ,commit_Sha, filename );
            classFileOut = ClassFile.execute().body().string();

            pram.put("issue_class" , classFileOut);
            String queary = ETLJsonObjectMapper.getInstance().toSearchJson(pram);

            elasticClient = new ElasticClient();
            Map<String , Object> result = elasticClient.search("defects" , queary);

            double score = (double) result.get("max_score");

            if(ObjectUtils.notEqual(result , null) && score > 5.0){
                //here

                Map<String , Object> ela_result = (Map<String, Object>) ((List<Map<String, Object>>)result.get("hits")).get(0).get("_source");
                ela_input.put("repo", this.repoName);
                ela_input.put("commiter" , this.commiter );
                ela_input.put("issue", ela_result.get("issue_name").toString());
                ela_input.put("class_name" , filename);

                elasticClient = new ElasticClient();
                elasticClient.insert("found_defects" , ela_input);
            }


        } catch (IOException e) {
            logger.error("exception" , e);
        }catch (NullPointerException e){
            logger.error("404 response" , e);
        }catch (ParseException e){
            logger.error("ParseException" , e);
        }
    }
}
