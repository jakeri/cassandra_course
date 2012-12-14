package dataserver.webserver;

import dataserver.StatsService;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * User: magnus
 * Date: 2012-12-13
 * Time: 22:56
 */
public class Server {


    public static void run(StatsService statsService) throws Exception {

        final org.mortbay.jetty.Server server = new org.mortbay.jetty.Server();
        Connector connector=new SelectChannelConnector();
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        ResourceHandler resource_handler = new ResourceHandler();

        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("server/src/main/webapp");

        ServletHandler servletHandler=new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new MetaServlet(statsService)), "/meta/");
        servletHandler.addServletWithMapping(new ServletHolder(new StatServlet(statsService)), "/stat/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, servletHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        server.join();

    }
}
