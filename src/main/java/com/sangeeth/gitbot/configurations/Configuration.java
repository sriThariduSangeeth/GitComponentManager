package com.sangeeth.gitbot.configurations;

import java.util.List;

/**
 * @author RPSPERERA on 4/4/2018
 */
public class Configuration {

    private List<Properties> propertiesList;
    private String connectorReference;

    public Configuration(List<Properties> propertiesList, String connectorReference) {
        this.propertiesList = propertiesList;
        this.connectorReference = connectorReference;
    }

    public List<Properties> getPropertiesList() {
        return propertiesList;
    }

    public void setPropertiesList(List<Properties> propertiesList) {
        this.propertiesList = propertiesList;
    }

    public String getConnectorReference() {
        return connectorReference;
    }

    public void setConnectorReference(String connectorReference) {
        this.connectorReference = connectorReference;
    }
}
