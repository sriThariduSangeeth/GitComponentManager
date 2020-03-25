package com.sangeeth.gitbot.core;

import com.sangeeth.gitbot.configurations.Configuration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Map;

/**
 * @author dtsangeeth
 * @created 11 / 02 / 2020
 * @project GitComponentManager
 */
public class ConfigXmlProperty {

    private static Logger logger = LogManager.getLogger(ConfigXmlProperty.class);
    private String serverClassNames;
    private Map<String, Configuration> properties;

    private ConfigXmlProperty(){

        StringBuilder stringBuilder  = new StringBuilder();
        FileSystemXmlApplicationContext systemXmlApplicationContext = new FileSystemXmlApplicationContext("/Properties/components.xml");
        properties = systemXmlApplicationContext.getBeansOfType(Configuration.class);
        properties.forEach((k,v) -> stringBuilder.append(v.getConnectorReference()).append(";"));
        serverClassNames = stringBuilder.substring(0, stringBuilder.length() - 1);
        systemXmlApplicationContext.close();

    }

    public String getServerClassNames() {
        return serverClassNames;
    }

    public Configuration getProperties(String uniqueIdentifier) {
        return properties.get(uniqueIdentifier);
    }

    public static ConfigXmlProperty getInstance(){
        return ConfigXmlProperty.Holder.INSTANCE;
    }

    private static class Holder{
        private static final ConfigXmlProperty INSTANCE = new ConfigXmlProperty();
    }
}
