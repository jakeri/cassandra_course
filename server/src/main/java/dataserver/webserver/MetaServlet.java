package dataserver.webserver;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import dataserver.StatsService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class MetaServlet extends HttpServlet {

    private final StatsService statsService;

    public MetaServlet(StatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        StatsService.MetaResult result = statsService.getMetaData();

        JSONArray json = new JSONArray();
        json.put(new JSONArray(result.hostnames));
        json.put(new JSONArray(result.metricName));

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            json.write(resp.getWriter());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }
}