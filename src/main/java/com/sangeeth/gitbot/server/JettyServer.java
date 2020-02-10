package com.sangeeth.gitbot.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author dtsangeeth
 * @created 10 / 02 / 2020
 * @project GitComponentManager
 */

public class JettyServer {

    private String serverClasses;
    private int serverPort;
    private Server jettyServer;
    private ServletHolder jerseyServlet;
    private static Logger logger = LogManager.getLogger(JettyServer.class);

    public JettyServer(){

        Runtime.getRuntime().addShutdownHook( new Thread(){
            public void run() {
                try {
                    logger.info("Shutting down jetty.....");
                    jerseyServlet.stop();
                    jettyServer.stop();
                    jettyServer.destroy();
                    logger.info("jetty terminated successfully.....");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }


    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start(){
        //this.serverClasses = Configurations.getInstance().getServerClassNames();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
    }
}
