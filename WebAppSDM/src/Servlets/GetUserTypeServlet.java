package Servlets;

import Constants.Constants;
import Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetUserTypeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException {
        /*
        * this servlet is for ajax use by JS jQuery
        * its load response with user Type ('seller' or 'customer')
        */


        response.setContentType("text/html;charset=UTF-8");

        String userType = SessionUtils.getUserType(request);

        if (userType == null) {
            //todo ridirect to home page!!
        }

        //Gson gson = new Gson();
        //String res = gson.toJson(userType);

        response.getWriter().println(userType);
    }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);

        }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
