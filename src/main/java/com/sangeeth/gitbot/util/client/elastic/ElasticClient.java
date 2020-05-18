package com.sangeeth.gitbot.util.client.elastic;

import com.sangeeth.gitbot.core.ReadPropertyFile;
import com.sangeeth.gitbot.util.Constants;
import com.sangeeth.gitbot.util.ETLJsonObjectMapper;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dtsangeeth
 * @created 13 / 05 / 2020
 * @project GitComponentManager
 */
public class ElasticClient {

    private static final int STATUS_CODE_SUCCESS = 200;
    private static Logger logger = Logger.getLogger(ElasticClient.class);
    private String hostUrl =  null;

    public ElasticClient(){
        this.hostUrl = ReadPropertyFile.getInstance().config().getString(Constants.ELASTIC_BASE_URL);
    }


    public List<Map<String, Object>> xPack(String query) throws IOException {
        logger.info("Executing " + query);
        StringBuffer result = new StringBuffer();
        try (CloseableHttpClient client = HttpClients.createDefault();) {

            HttpPost httpPost = new HttpPost(hostUrl + "/_xpack/sql");
            httpPost.setEntity(new StringEntity("{\"query\": \"" + query + "\"}", "UTF-8"));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            result = getResult(response);
        }
        return toResultList(result.toString());
    }

    public Map<String, Object> search(String index, String query) throws IOException {
        logger.info("Executing " + query + " on " + index);
        StringBuffer result = new StringBuffer();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(hostUrl + "/" + index + "/_search");
            httpPost.setEntity(new StringEntity(query,"UTF-8"));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            result = getResult(response);
        }

        Map<String , Object> res = ETLJsonObjectMapper.getInstance().stringToMap(result.toString());
        // client.close();
        return (Map<String, Object>) res.get("hits");
    }

    public StringBuffer getResult ( CloseableHttpResponse response) throws IOException{

        StringBuffer result = new StringBuffer();

        if (response.getStatusLine().getStatusCode() != STATUS_CODE_SUCCESS) {
            throw new ClientProtocolException(
                    response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        }
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),Charset.forName("UTF-8")));) {
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }

        return result;
    }

    public void insert(String index, Map<String, Object> summary) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();) {
            String query = ETLJsonObjectMapper.getInstance().toJson(summary);
            logger.info("Executing " + query + " on " + index);
//            HttpPut httpPost = new HttpPut(hostUrl + "/" + index + "/_doc");
            HttpPost httpPost = new HttpPost(hostUrl + "/" + index + "/_doc");
            httpPost.setEntity(new StringEntity(query,"UTF-8"));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new ClientProtocolException(
                        response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
        }
    }


    public String elasticUpdate(String index ,String query) throws ClientProtocolException, IOException {
        logger.info("Executing " + query);
        String result= null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(hostUrl + "/"+index+"/_update_by_query");
            httpPost.setEntity(new StringEntity(query, "UTF-8"));
            //httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != STATUS_CODE_SUCCESS) {
                throw new ClientProtocolException(
                        response.getStatusLine().getStatusCode()
                                + " " + response.getStatusLine().getReasonPhrase());
            }else {
                result = "ok";
            }

        }
        return result;
    }

    private List<Map<String, Object>> toResultList(String result) {
        List<Map<String, Object>> resultList = new ArrayList<>();
//        Map<String, Object> resultMap = JsonUtils.readJsonAsMap(result);
        Map<String, Object> resultMap = ETLJsonObjectMapper.getInstance().stringToMap(result);
        List<Map<String, Object>> columns = (List<Map<String, Object>>) resultMap.get("columns");
        List<String> columnNames = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            columnNames.add((String) column.get("name"));
        }
        List<List<Object>> rows = (List<List<Object>>) resultMap.get("rows");
        for (List<Object> row : rows) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < row.size(); i++) {
                String colName = columnNames.get(i);
                map.put(colName, row.get(i));
            }
            resultList.add(map);
        }
        return resultList;
    }
}
