package com.sangeeth.gitbot.init;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */


import com.sangeeth.gitbot.server.JettyServer;
import com.sangeeth.gitbot.core.ReadPropertyFile;
import org.apache.commons.configuration2.Configuration;

import static com.sangeeth.gitbot.util.Constants.JETTY_SERVER_POST;
import static com.sangeeth.gitbot.util.Constants.VERSION_CONTROL_TYPR;

public class Init {

    public static void main(String[] args) {

        JettyServer jettyServer = new JettyServer();
        Configuration conf = ReadPropertyFile.getInstance().config();
        jettyServer.setServerPort(conf.getInt(JETTY_SERVER_POST) , conf.getString(VERSION_CONTROL_TYPR));
        jettyServer.start();

    }
}
