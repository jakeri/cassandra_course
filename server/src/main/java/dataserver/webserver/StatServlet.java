package dataserver.webserver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dataserver.StatsService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class StatServlet extends HttpServlet {

    private final StatsService statsService;

    public StatServlet(StatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String host   = req.getParameter("host");
        String metric = req.getParameter("metric");

        ImmutableList<StatsService.MetricTuple> result = statsService.getStatsFor(host, metric);

        JSONArray json = new JSONArray( Lists.transform(result, new Function<StatsService.MetricTuple, JSONArray>() {
            @Override
            public JSONArray apply(StatsService.MetricTuple current) {
                JSONArray currentJson = new JSONArray();
                currentJson.put(current.timestamp);
                currentJson.put(current.value);
                return currentJson;
            }
        }));

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);

        try {
            json.write(resp.getWriter());
        } catch (JSONException e) {
            throw new ServletException(e);
        }

    }
}