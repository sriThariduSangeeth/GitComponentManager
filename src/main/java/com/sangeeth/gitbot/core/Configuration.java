package com.sangeeth.gitbot.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author dtsangeeth
 * @created 11 / 02 / 2020
 * @project GitComponentManager
 */
public class Configuration {

    private static Logger logger = LogManager.getLogger(Configuration.class);
    private String serverClassNames;
    private Map<String, Configuration> properties;

    private Configuration(){

    }

    public static Configuration getInstance(){
        return Configuration.Holder.INSTANCE;
    }

    private static class Holder{
        private static final Configuration INSTANCE = new Configuration();
    }
}
