package Servlets;

import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import course.java.sdm.engine.MainSystem;
import course.java.sdm.generatedClasses.AreaInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AllAreasServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
        String userNameFromSession = SessionUtils.getUserName(request);
        String AreasVersionString = request.getParameter(Constants.Constants.Areas_VERSION_PARAMETER);
        int UserAreaVersion = Integer.parseInt(AreasVersionString);
        int OfficialAreasVersion=0;

        List<AreaInfo> areasEntries;
        synchronized (getServletContext()) {
            OfficialAreasVersion = MainSDM.getAreaVersion();
            areasEntries = MainSDM.getAreaEntries(UserAreaVersion);
        }

        AreaAndVersion cav = new AreaAndVersion(areasEntries, OfficialAreasVersion);
        //List<AreaInfo> areasEntries = MainSDM.getAllAreaEntries();
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(cav);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    private static class AreaAndVersion {

        final private List<AreaInfo> entries;
        final private int version;

        public AreaAndVersion(List<AreaInfo> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }
}
