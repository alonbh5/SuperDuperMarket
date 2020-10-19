package Servlets;

import Constants.Constants;
import Utils.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import course.java.sdm.engine.*;


public class LoginServlet extends HttpServlet {

    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...


    //private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...

    private final String SIGN_UP_URL = "Index.html";
    private final String WELCOME_ROOM_URL = "Pages/WelcomeRoom/WelcomeRoom.html";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        Long userIDFromSession = SessionUtils.getUserId(request); //return null if no session
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());

        if (userIDFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            String userTypeFromParameter = request.getParameter(Constants.USERTYPE);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter -
                //redirect back to the index page
                //this return an HTTP code back to the browser telling it to load
                response.sendRedirect(SIGN_UP_URL);
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                usernameFromParameter = usernameFromParameter.toLowerCase();
                usernameFromParameter = usernameFromParameter.substring(0, 1).toUpperCase() + usernameFromParameter.substring(1);

                synchronized (this) {
                    if (MainSDM.isUserExists(usernameFromParameter)) { //this time only one name for user..
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        // username already exists, forward the request back to index.jsp
                        // with a parameter that indicates that an error should be displayed
                        // the request dispatcher obtained from the servlet context is one that MUST get an absolute path (starting with'/')
                        // and is relative to the web app root
                        // see this link for more details:
                        // http://timjansen.github.io/jarfiller/guide/servlet25/requestdispatcher.xhtml
                        request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                        getServletContext().getRequestDispatcher(SIGN_UP_URL).forward(request, response);//todo error!!!
                    } else {
                        //add the new user to the users list
                        Person newUser;
                        if (userTypeFromParameter.equals("seller"))
                            newUser=MainSDM.addNewSeller(usernameFromParameter);
                        else
                            newUser=MainSDM.addNewBuyer(usernameFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(Constants.USERNAME, newUser.getName());
                        request.getSession(false).setAttribute(Constants.USERID,newUser.getId());
                        request.getSession(false).setAttribute(Constants.USERTYPE,userTypeFromParameter);
                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());

                        if (userTypeFromParameter.equals("seller"))
                            response.sendRedirect(WELCOME_ROOM_URL); //todo one
                        else
                            response.sendRedirect(WELCOME_ROOM_URL); //todo two
                    }
                }
            }
        } else {
            //user is already logged in
            response.sendRedirect(WELCOME_ROOM_URL);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
