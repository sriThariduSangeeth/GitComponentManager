package com.sangeeth.gitbot.init;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */

import com.sangeeth.gitbot.server.JettyServer;
import com.sangeeth.gitbot.core.ReadPropertyFile;
import static com.sangeeth.gitbot.util.Constants.JETTY_SERVER_POST;

public class Init {

    public static void main(String[] args) {

        JettyServer jettyServer = new JettyServer();
        jettyServer.setServerPort(ReadPropertyFile.getInstance().config().getInt(JETTY_SERVER_POST));
        jettyServer.start();

    }
}
