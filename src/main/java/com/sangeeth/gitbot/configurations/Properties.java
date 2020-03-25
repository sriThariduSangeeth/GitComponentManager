package com.sangeeth.gitbot.configurations;

import java.util.Map;

/**
 * @author RPSPERERA on 4/5/2018
 */
public class Properties {

    private String instanceName;
    private Map<String, String> propertyMap;

    public Properties(String instanceName, Map<String, String> propertyMap) {
        this.instanceName = instanceName;
        this.propertyMap = propertyMap;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }
}
