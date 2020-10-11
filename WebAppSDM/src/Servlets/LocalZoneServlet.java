package Servlets;

import Constants.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import course.java.sdm.engine.MainSystem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LocalZoneServlet extends HttpServlet {

    public static String ZONE_ROOM_URL = "Pages/ZonePage/ZonePage.html";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long userIDFromSession = SessionUtils.getUserId(request); //return null if no session


        if (userIDFromSession != null) {
            MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
            String usernameFromParameter = request.getParameter(Constants.ZONE);
            request.getSession(false).setAttribute(Constants.ZONE,usernameFromParameter); //added currentZone
            response.sendRedirect(ZONE_ROOM_URL);
        }
        else {
            //todo error
        }
    }
}
