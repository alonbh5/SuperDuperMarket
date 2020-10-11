package Servlets;

import Constants.Constants;
import Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetUserTypeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException {
        /*
        * this servlet is for ajax use by JS jQuery
        * its load response with user Info By Session in the form of :
        * {"userType":"seller","userName":"Alon","userId":"1","userZone":"Tel Aviv"}
        */



        response.setContentType("application/json");

        String userType = SessionUtils.getUserType(request);
        String userZone = SessionUtils.getUserCurZone(request);
        String userName = SessionUtils.getUserName(request);
        Long userId = SessionUtils.getUserId(request);

        UserSession UserInfo = new UserSession(userType,userZone,userName,userId);

        if (userType == null) {
            //todo ridirect to home page!!
        }

        Gson gson = new Gson();
        String res = gson.toJson(UserInfo);
        System.out.println(res);
        try (PrintWriter out = response.getWriter()) {
            out.print(res);
            out.flush();
        }
    }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);

        }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private static class UserSession {

        final private String userType;
        final private String userZone;
        final private String userName;
        final private String userId;

        public UserSession(String userType, String userZone, String userName, Long userId) {
            this.userType = userType;
            this.userZone = userZone;
            this.userName = userName;
            this.userId = userId.toString();
        }
    }
}
