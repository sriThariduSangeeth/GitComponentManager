package com.sangeeth.gitbot.service;

import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.core.ConfigXmlProperty;
import com.sangeeth.gitbot.core.ReadPropertyFile;
import com.sangeeth.gitbot.retrofitDrive.GitRetrofitDrive;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ObjectUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */


@Path("/git")
public class GitService {


    // ex : http://localhost:8990/dataservice/git/instance1/getDefectList
    @GET
    @Path("/{instanceName}/getDefectList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDefectList(@PathParam("instanceName") String instanceName) {
        Configuration reader = ReadPropertyFile.getInstance().config();

        final Properties[] properties = new Properties[1];
        List<String> projectKeys = new ArrayList<>();
        ConfigXmlProperty.getInstance().getProperties("git").getPropertiesList().forEach(properties1 -> {
            if (!ObjectUtils.notEqual(properties1.getInstanceName(), instanceName)) {
                properties[0] = properties1;
            }
        });

        GitRetrofitDrive gitRetrofitDrive = new GitRetrofitDrive(properties[0]);



        return "";
    }

}
