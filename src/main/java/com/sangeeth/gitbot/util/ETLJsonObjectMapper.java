package com.sangeeth.gitbot.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.json.JsonObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */
public class ETLJsonObjectMapper {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static Logger logger = LogManager.getLogger(ETLJsonObjectMapper.class);


    public static ETLJsonObjectMapper getInstance() {
        return ETLJsonObjectMapper.Holder.INSTANCE;
    }

    private ETLJsonObjectMapper() {
        super();
    }

    public ObjectMapper getObjectMapper() {
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return objectMapper;
    }

    /**
     * Converts any Object to String.
     *
     * @param data the object
     * @return the string
     */

    public String toString(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Converts String value to a JOSN.
     *
     *
     * @param object the object
     * @return the string
     */
    public  String toJson(final String object) {
        if (object != null && (object.startsWith("[") || object.startsWith("{")
                || (object.startsWith("\"[") || object.startsWith("\"{")))) {
            return object;
        } else
            return "{\"" + "{\"success\" : 1}" + "\":\"" + object + "\"}";
    }

    /**
     * Converts any Object to JSON.
     *
     * @param object the object
     * @return the string
     */
    public  String toJsonfrmObject(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(simpleDateFormat);
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Invalid JSON!", e);
        }
        return "";
    }


    public Map<String , Object> stringToMap(String data){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(data, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Converts Json Object to Map<obj></>.
     *
     * @param data the object
     * @return the Map<String, Object>
     */

    public Map<String, Object> toMap(Object data) {

        try {
            String dataF = null;
            if (!(data instanceof String)) {
                dataF = toString(data);
            }
//            else {
//             dataF = objectMapper.writeValueAsString(data);
//            }
            return objectMapper.readValue(dataF, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Converts Json Object to List<obj></>.
     *
     * @param data the object
     * @return the List<Map<String, Object>>
     */

    public List<Map<String, Object>> toMapList(Object data) {

        try {
            String dataF;
            if (!(data instanceof String)) {
                dataF = toString(data);
            } else {
                dataF = objectMapper.writeValueAsString(data);
            }
            return objectMapper.readValue(dataF, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String toJson(final Map<String, Object> metaData) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(simpleDateFormat);
            return mapper.writeValueAsString(metaData);
        } catch (IOException e) {
            logger.error("Invalid JSON!", e);
        }
        return "";
    }

    public String toSearchJson (Map<String,String> parm) throws ParseException {

        JSONObject obj = new JSONObject();
        for (Map.Entry<String, String> entry : parm.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }
        JSONObject obj1 = new JSONObject();
        obj1.put("match" , obj);
        JSONObject obj2 = new JSONObject();
        obj2.put("query" , obj1);

        return obj2.toString();
    }

    private static class Holder {
        private static final ETLJsonObjectMapper INSTANCE = new ETLJsonObjectMapper();
    }
}
