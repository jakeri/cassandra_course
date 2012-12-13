package dataserver.webserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MetaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Hello, world!");
        //response.setContentType("text/html");
        //response.setStatus(HttpServletResponse.SC_OK);
        //response.getWriter().println("<h1>Hello ManyHandler</h1>" + request.getRequestURI());
    }
}