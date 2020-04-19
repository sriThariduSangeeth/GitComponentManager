package com.sangeeth.gitbot.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */
public class ETLJsonObjectMapper {

    private ObjectMapper objectMapper = new ObjectMapper();

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

    private String toString(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
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

    private static class Holder {
        private static final ETLJsonObjectMapper INSTANCE = new ETLJsonObjectMapper();
    }
}
