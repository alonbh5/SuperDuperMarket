package Servlets;

import Constants.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import course.java.sdm.classesForUI.*;
import course.java.sdm.engine.Customer;
import course.java.sdm.engine.MainSystem;
import course.java.sdm.engine.SuperDuperMarketSystem;
import course.java.sdm.exceptions.ItemIsNotSoldAtAllException;
import course.java.sdm.exceptions.NoValidXMLException;
import course.java.sdm.exceptions.PointOutOfGridException;
import course.java.sdm.exceptions.StoreDoesNotSellItemException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
                    CreatOrder(request,response,sdmByZone,MainSDM);
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

    private void CreatOrder(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        String userDate = request.getParameter("datepicker");
        String OrderType = request.getParameter("orderType");
        Customer curCustomer = mainSDM.getCustomer(usernameFromParameter);
        List<ItemInOrderInfo> wantedItems = new ArrayList<>();
        String amountWanted;

        int x= Integer.parseInt(request.getParameter("LocX"));
        int y= Integer.parseInt(request.getParameter("LocY"));
        curCustomer.setCurrentLocation(new Point(x,y));

        try {
            
            Date date=new SimpleDateFormat("yyyy-MM-dd").parse(userDate);
            
        if (OrderType.toLowerCase().equals("static")){

            String storeIdChosen = request.getParameter("stores");
            StoreInfo storeInfoByID = sdmByZone.getStoreInfoByID(Long.parseLong(storeIdChosen));



            for (ItemInStoreInfo cur : storeInfoByID.Items) {
                amountWanted = request.getParameter(cur.serialNumber.toString());
                if (!amountWanted.isEmpty())
                    wantedItems.add(new ItemInOrderInfo(cur,Double.parseDouble(amountWanted)));
            }


            List<DiscountInfo> discountInfos = sdmByZone.CreateTempStaticOrderAndGetDiscounts(wantedItems, storeInfoByID, curCustomer, date);
            Gson gson = new Gson();
            String allItemsJson = gson.toJson(discountInfos);
            System.out.println(allItemsJson);
            PrintWriter out = response.getWriter();
            out.print(allItemsJson);
            out.flush();
        }        
        else {

            Collection<ItemInfo> listOfAllItems = sdmByZone.getListOfAllItems();

            for (ItemInfo cur : listOfAllItems) {
                amountWanted = request.getParameter(cur.serialNumber.toString());
                if (!amountWanted.isEmpty())
                    wantedItems.add(new ItemInOrderInfo(cur,Double.parseDouble(amountWanted)));
            }

            OrderInfo dynamicOrderInfoBeforeDiscounts = sdmByZone.getDynamicOrderInfoBeforeDiscounts(wantedItems, curCustomer, date);
            Gson gson = new Gson();
            String allItemsJson = gson.toJson(dynamicOrderInfoBeforeDiscounts);
            System.out.println(allItemsJson);
            PrintWriter out = response.getWriter();
            out.print(allItemsJson);
            out.flush();


            //todo return all items     
        }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (StoreDoesNotSellItemException e) {
            e.printStackTrace();
        } catch (PointOutOfGridException e) {
            e.printStackTrace();
        } catch (NoValidXMLException e) {
            e.printStackTrace();
        } catch (ItemIsNotSoldAtAllException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                    SendSellerOrderHistoryAjax(request,response,sdmByZone);
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

    private void SendSellerOrderHistoryAjax(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone) {
        try {
            String UserName = SessionUtils.getUserName(request);;
            Collection<StoreInfo> allStores = sdmByZone.getListOfAllStoresInSystem();
            List<StoreInfo> OnlyStoreFromUser = new ArrayList<>();

            for (StoreInfo cur : allStores)
                if (cur.isOwnerName(UserName))
                    OnlyStoreFromUser.add(cur);

            for (StoreInfo curStore : OnlyStoreFromUser)
                for (OrderInfo curOrder : curStore.OrderHistory)
                        curOrder.makeDynamic(curStore);

            Gson gson = new Gson();
            String sellerStoresJson = gson.toJson(OnlyStoreFromUser);
            System.out.println(sellerStoresJson);
            PrintWriter out = response.getWriter();
            out.print(sellerStoresJson);
            out.flush();
        } catch (NoValidXMLException | IOException e) {
            //todo error
        }
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
