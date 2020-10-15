package Servlets;

import Constants.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import course.java.sdm.classesForUI.*;
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
    public final String BuyerOrderHistoryRequest = "buyerOrders";
    public final String SellerOrderHistoryRequest = "sellerOrders";
    public final String ItemsRequest = "items";
    public final String SellerFeedbackRequest = "feedbacks";
    public final String AccentWalletRequest = "wallet";

    public final String CreateOrderRequest = "createOrder";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //todo post for getting new store or order or waller

        response.setContentType("application/json");
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
        String CurUserZone = SessionUtils.getUserCurZone(request); //return null if no session
        String userRequest = request.getParameter(Constants.USER_REQUEST);

        if (CurUserZone != null) {
            SuperDuperMarketSystem sdmByZone = MainSDM.getSDMByZone(CurUserZone);

            switch (userRequest) {
                case CreateOrderRequest:
                    CreatOrderAndSendItems(request,response,sdmByZone);
                    break;
                default:
                    // code block //todo error
            }
        }
        else {
            String CurUserName = SessionUtils.getUserName(request);
            if (userRequest.equals(AccentWalletRequest) && CurUserName!=null)
                SendUserWallet(request,response,MainSDM);
            else {
                //todo error
            }
        }
    }

    private void CreatOrderAndSendItems(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        String userDate = request.getParameter("datepicker");
        String OrderType = request.getParameter("orderType");
        if (OrderType.toLowerCase().equals("static")){
            try{
            String storeIdChosen = request.getParameter("stores");
            StoreInfo storeInfoByID = sdmByZone.getStoreInfoByID(Long.parseLong(storeIdChosen));
            //todo return all item from that store...
            Gson gson = new Gson();
            String ItemsJson = gson.toJson(storeInfoByID.Items);
            System.out.println(ItemsJson);
            PrintWriter out = response.getWriter();
            out.print(ItemsJson);
            out.flush();
            }
            catch (IOException e) {
            e.printStackTrace();
        }

        }
        else {
            Collection<ItemInfo> listOfAllItems;
            try {
                listOfAllItems = sdmByZone.getListOfAllItems();
                Gson gson = new Gson();
                String ItemsJson = gson.toJson(listOfAllItems);
                System.out.println(ItemsJson);
                PrintWriter out = response.getWriter();
                out.print(ItemsJson);
                out.flush();
            } catch (NoValidXMLException | IOException e) {
                e.printStackTrace();
            }
            //todo return all items     
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         * this servlet is for ajax use by JS jQuery
         * its load response with AJAX Info By user request -  :
         * if it related to Zone
         */
        response.setContentType("application/json");
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
        String CurUserZone = SessionUtils.getUserCurZone(request); //return null if no session
        String userRequest = request.getParameter(Constants.USER_REQUEST);

        if (CurUserZone != null) {
            SuperDuperMarketSystem sdmByZone = MainSDM.getSDMByZone(CurUserZone);

            switch (userRequest) {
                case StoreRequest:
                    SendStoreAjax(request,response,sdmByZone);
                    break;
                case BuyerOrderHistoryRequest:
                    SendBuyerOrderHistoryAjax(request,response,sdmByZone);
                    break;
                case ItemsRequest:
                    SendItemsAjax(request,response,sdmByZone);
                    break;
                case SellerFeedbackRequest:
                    SendSellerFeedBackAjax(request,response,MainSDM,CurUserZone);
                    break;
                case AccentWalletRequest:
                    SendUserWallet(request,response,MainSDM);
                    break;
                case SellerOrderHistoryRequest:
                    SendSellerOrderHistoryAjax(request,response,MainSDM);
                    break;
                default:
                    // code block //todo error
            }
        }
        else {
            String CurUserName = SessionUtils.getUserName(request);
            if (userRequest.equals(AccentWalletRequest) && CurUserName!=null)
                SendUserWallet(request,response,MainSDM);
            else {
                //todo error
            }
        }
    }

    private void SendSellerOrderHistoryAjax(HttpServletRequest request, HttpServletResponse response, MainSystem mainSDM) {
    }

    private void SendUserWallet(HttpServletRequest request, HttpServletResponse response, MainSystem mainSDM) {
        try {
            String CurUserName = SessionUtils.getUserName(request);
            WalletInfo Wallet = mainSDM.getWalletByUser(CurUserName);
            Gson gson = new Gson();
            String WalletJson = gson.toJson(Wallet);
            System.out.println(WalletJson);
            PrintWriter out = response.getWriter();
            out.print(WalletJson);
            out.flush();
        } catch (IOException e) {
            //todo error
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

    private void SendSellerFeedBackAjax(HttpServletRequest request, HttpServletResponse response, MainSystem mainSDM,String Zone) {
        try {
            String CurUserName = SessionUtils.getUserName(request);
            Collection<FeedBackInfo> allFeedback = mainSDM.getSellerFeedbackByZone(Zone,CurUserName);
            Gson gson = new Gson();
            String allFeedbackJson = gson.toJson(allFeedback);
            System.out.println(allFeedbackJson);
            PrintWriter out = response.getWriter();
            out.print(allFeedbackJson);
            out.flush();
        } catch (IOException e) {
            //todo error
        }
    }

    private void SendBuyerOrderHistoryAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {
        try {
            String CurUserName = SessionUtils.getUserName(request);
            Collection<OrderInfo> allOrders = mainSDM.getListOfAllOrderByUser(CurUserName); //todo now its by zone and not all?
            Gson gson = new Gson();
            String allOrdersJson = gson.toJson(allOrders);
            System.out.println(allOrdersJson);
            PrintWriter out = response.getWriter();
            out.print(allOrdersJson);
            out.flush();
        } catch (IOException e) {
            //todo error
        }
    }

    private void SendStoreAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem mainSDM) {
        try {
            Collection<StoreInfo> allStores = mainSDM.getListOfAllStoresInSystem();
            Gson gson = new Gson();
            String allStoresJson = gson.toJson(allStores);
            System.out.println(allStoresJson);
            PrintWriter out = response.getWriter();
            out.print(allStoresJson);
            out.flush();
        } catch (NoValidXMLException | IOException e) {
            //todo error
        }
    }
}
