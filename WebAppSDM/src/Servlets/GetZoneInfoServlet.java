package Servlets;

import Constants.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import course.java.sdm.classesForUI.ItemInfo;
import course.java.sdm.engine.MainSystem;
import course.java.sdm.engine.SuperDuperMarketSystem;
import course.java.sdm.exceptions.NoValidXMLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

public class GetZoneInfoServlet extends HttpServlet {

    public final String StoreRequest = "stores";
    public final String orderHistoryRequest = "orders";
    public final String ItemsRequest = "items";
    public final String SellerFeedbackRequest = "feedbacks";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //todo post for getting new store or order
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         * this servlet is for ajax use by JS jQuery
         * its load response with AJAX Info By user request -  :
         * if it related to Zone
         */
        response.setContentType("application/json");
        String CurUserZone = SessionUtils.getUserCurZone(request); //return null if no session

        if (CurUserZone != null) {
            MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
            SuperDuperMarketSystem sdmByZone = MainSDM.getSDMByZone(CurUserZone);
            String userRequest = request.getParameter(Constants.USER_REQUEST);

            switch (userRequest) {
                case StoreRequest:
                    SendStoreAjax(request,response,sdmByZone);
                    break;
                case orderHistoryRequest:
                    SendOrderHistoryAjax(request,response,sdmByZone);
                    break;
                case ItemsRequest:
                    SendItemsAjax(request,response,sdmByZone);
                    break;
                case SellerFeedbackRequest:
                    SendSellerFeedBackAjax(request,response,sdmByZone);
                    break;
                default:
                    // code block //todo error
            }
        }
    }

    private void SendItemsAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {

        try {
            Collection<ItemInfo> allItems = mainSDM.getListOfAllItems();
            Gson gson = new Gson();
            String allItemsJson = gson.toJson(allItems);
            System.out.println(allItemsJson);
            PrintWriter out = response.getWriter();
            out.print(allItemsJson);
            out.flush();
        } catch (NoValidXMLException | IOException e) {
            //todo error
        }
    }

    private void SendSellerFeedBackAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {
    }

    private void SendOrderHistoryAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {
    }

    private void SendStoreAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {
    }
}
