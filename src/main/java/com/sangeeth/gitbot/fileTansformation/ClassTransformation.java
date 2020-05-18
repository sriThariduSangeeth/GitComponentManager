package com.sangeeth.gitbot.fileTansformation;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import com.sangeeth.gitbot.service.GitServiceHelper;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import com.sangeeth.gitbot.util.client.antlr.AntlrPaser;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;

/**
 * @author dtsangeeth
 * @created 08 / 05 / 2020
 * @project GitComponentManager
 */
public class ClassTransformation {

    private static Logger logger = LogManager.getLogger(ClassTransformation.class);

    private String auther;
    private String repo;
    private GitServiceHelper gitServiceHelper = new GitServiceHelper();
    private Properties properties;

    public ClassTransformation(String autho , String repo , String instanceName , ETLJsonObjectMapper instance){
        this.auther = autho;
        this.repo = repo;
        //download
        properties = gitServiceHelper.getPropertiesList(instanceName);
    }

    public Map<String , Object> downloadFileAndTransform ( Map<String, Object> issue ,ETLJsonObjectMapper instance ,
                                                          Map<String, Object> fixCommit , Map<String, Object> wrongCommit){

        Map<String,Object> param = new HashMap<>();
        GitRetrofitDrive gitRetrofitDriveForDown = new GitRetrofitDrive(properties.getPropertyMap().get("authToken") , properties.getPropertyMap().get("contentBaseUrl"));

        param.put("issue_name", issue.get("title"));
        param.put("issue_body", issue.get("body"));
        if (ObjectUtils.notEqual(fixCommit.get("commit_id"), null) || ObjectUtils.notEqual(0,fixCommit.size()) ){

            List<Map<String, Object>> fileList = (List<Map<String, Object>>) fixCommit.get("files");

            fileList.forEach(file -> {
                String fileName = file.get("filename").toString();
                StringTokenizer defaultTokenizer = new StringTokenizer(fileName , "://.-");

                List<String> cLab = new ArrayList<>();
                while (defaultTokenizer.hasMoreTokens())
                {
                    cLab.add(defaultTokenizer.nextToken());
                }

                String fixCommitShh = fixCommit.get("sha").toString();
                String wrongCommitShh = wrongCommit.get("sha").toString();

                if(cLab.contains("java")){
                    try {

                        param.put("class_name" , fileName);

                        Call<ResponseBody> fixClassFile = ((GitAPI)gitRetrofitDriveForDown.invoke()).getFileDownload(this.repo , this.auther ,fixCommitShh, fileName );
                        String fixClassFileOut = fixClassFile.execute().body().string();
                        AntlrPaser antlrPaser1 = new AntlrPaser(fixClassFileOut);
                        String fixSekeloton = antlrPaser1.getCodeSkeleton();

                        Call<ResponseBody> worngClassFile = ((GitAPI)gitRetrofitDriveForDown.invoke()).getFileDownload(this.repo , this.auther ,wrongCommitShh, fileName );
                        String worngclassFileOut = worngClassFile.execute().body().string();
                        AntlrPaser antlrPaser2 = new AntlrPaser(fixClassFileOut);
                        String wrongSekeloton = antlrPaser2.getCodeSkeleton();

                        if(ObjectUtils.notEqual(fixClassFileOut,null) && ObjectUtils.notEqual(worngclassFileOut,null)){
                            param.put("issue_fix_class", fixClassFileOut);
                            param.put("issue_class", worngclassFileOut);
                        }


                    } catch (IOException e) {
                        logger.error("exception" , e);
                    }catch (NullPointerException e){
                        logger.error("404 response" , e);
                    }
                }

            });

        }

        return param;
    }


}
