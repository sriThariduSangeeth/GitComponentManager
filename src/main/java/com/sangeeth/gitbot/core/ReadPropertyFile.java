package com.sangeeth.gitbot.core;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.nio.file.Paths;

/**
 * @author dtsangeeth
 * @created 24 / 03 / 2020
 * @project GitComponentManager
 */
public class ReadPropertyFile {

    private Configuration config;
    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private static Logger logger = LogManager.getLogger(ReadPropertyFile.class);

    public ReadPropertyFile() {

        try {
            Parameters parameters = new Parameters();
            builder  = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(parameters.fileBased()
                            .setFileName(Paths.get("").toAbsolutePath().toString() + "/Properties/config.properties"));
            builder.setAutoSave(true);
            builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST ,
                    configurationBuilderEvent -> builder.getReloadingController().checkForReloading(null));
            config = builder.getConfiguration();

        }catch (ConfigurationException e){
            logger.error(e.getMessage(), e);
        }


    }

    public Configuration config() {
        return config;
    }

    public static ReadPropertyFile getInstance(){
        return ReadPropertyFile.Holder.INSTANCE;
    }

    private static class Holder{
        private static final ReadPropertyFile INSTANCE = new ReadPropertyFile();
    }

}
