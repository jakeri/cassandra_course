package dataserver;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;




public class Main {

	public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        //copy paste from
        //http://docs.codehaus.org/display/JETTY/Embedding+Jetty

        // assumes that this directory contains .html and .jsp files
        // This is just a directory within your source tree, and can be exported as part of your normal .jar
        final String WEBAPPDIR = "src/main/webapp";

        final Server server = new Server(9090);


        final String CONTEXTPATH = "/";

        // for localhost:port/admin/index.html and whatever else is in the webapp directory
        //final URL warUrl = Main.class.getClassLoader().getResource(WEBAPPDIR);
        final String warUrlString = "src/main/webapp";// warUrl.toExternalForm();
        server.setHandler(new WebAppContext(warUrlString, CONTEXTPATH));

        // for localhost:port/servlets/cust, etc.
        final Context context = new Context(server, "/servlets", Context.SESSIONS);
        context.addServlet(new ServletHolder(new ExampleServlet()), "/cust");


        server.start();
        server.join();



	}
}
