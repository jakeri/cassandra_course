package dataserver;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.*;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        //http://docs.codehaus.org/display/JETTY/Embedding+Jetty
        final Server server = new Server();
        Connector connector=new SelectChannelConnector();
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("src/main/webapp");

        Handler custHandler = new HelloHandler("/cust");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { custHandler, resource_handler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        server.join();
	}

    public static class HelloHandler extends AbstractHandler
    {
        private final String url;
        public HelloHandler(String url) { this.url = url;}

        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException
        {
            Request base_request = (request instanceof Request) ? (Request)request: HttpConnection.getCurrentConnection().getRequest();
            if ( request.getRequestURI().startsWith(url) ){
                base_request.setHandled(true);
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("<h1>Hello ManyHandler</h1>" + request.getRequestURI());
            }
        }
    }

}
