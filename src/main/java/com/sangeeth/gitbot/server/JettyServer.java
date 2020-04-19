package com.sangeeth.gitbot.server;

import com.sangeeth.gitbot.core.ConfigXmlProperty;
import org.apache.commons.configuration2.builder.fluent.Configurations;
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
        this.serverClasses = ConfigXmlProperty.getInstance().getServerClassNames();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(serverPort);
        jettyServer.setHandler(context);

        jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/dataservice/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                this.serverClasses);


        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception ioex) {
            logger.error(ioex.getMessage(), ioex);
        }

    }

    public void shutdown() {
        try {
            jettyServer.destroy();
            jerseyServlet.stop();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
