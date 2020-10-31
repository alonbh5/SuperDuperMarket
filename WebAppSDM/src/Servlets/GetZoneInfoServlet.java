package Servlets;

import Constants.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import course.java.sdm.classesForUI.*;
import course.java.sdm.engine.Customer;
import course.java.sdm.engine.MainSystem;
import course.java.sdm.engine.SuperDuperMarketSystem;
import course.java.sdm.exceptions.*;

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
    public final String AddMoney = "addMoney";
    public final String OpenStore = "openStore";
    public final String AddFeedback = "addFeedback";
    public final String DiscountAdd = "addDiscount";
    public final String FinishOrder = "finishOrder"; //add all discount..
    public final String ApproveOrder = "approveOrder";
    public final String CreateOrderRequest = "createOrder";
    public final String GiveNotification = "notify";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //todo post for getting new store or order or waller

        response.setContentType("application/json");
        MainSystem MainSDM = ServletUtils.getMainSystem(getServletContext());
        String CurUserZone = SessionUtils.getUserCurZone(request); //return null if no session
        String userRequest = request.getParameter(Constants.USER_REQUEST);

        String CurUserName = SessionUtils.getUserName(request);
        if (userRequest.equals(AccentWalletRequest) && CurUserName!=null)
            SendUserWallet(request,response,MainSDM);
        else {
            if (userRequest.equals(AddMoney) && CurUserName!=null)
                SendCharge(request,response,MainSDM);
            else
            if (CurUserZone != null) {
                SuperDuperMarketSystem sdmByZone = MainSDM.getSDMByZone(CurUserZone);

                switch (userRequest) {
                    case CreateOrderRequest:
                        CreatOrder(request,response,sdmByZone,MainSDM);
                        break;
                    case DiscountAdd:
                        SendDiscountInfo(request,response,sdmByZone,MainSDM);
                        break;
                    case FinishOrder:
                        FinishOrder(request,response,sdmByZone,MainSDM);
                        break;
                    case ApproveOrder:
                        ApproveBuyerOrder(request,response,sdmByZone,MainSDM);
                        break;
                    case AddFeedback:
                        AddFeedbacks(request,response,sdmByZone,MainSDM);
                        break;
                    case OpenStore:
                        addNewStore(request,response,sdmByZone,MainSDM);
                        break;
                    default:
                        // code block //todo error
                }
            }
            else {
                    //todo error?
            }
        }


    }

    private void addNewStore(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        String Zone = SessionUtils.getUserCurZone(request);
        String StoreName =  (request.getParameter("sname")).trim();
        Integer PPK =  Integer.parseInt((request.getParameter("PPK")).trim());
        int x= Integer.parseInt(request.getParameter("LocX"));
        int y= Integer.parseInt(request.getParameter("LocY"));
        Point location = new Point(x,y);

        String wantedPrice;
        List<ItemInStoreInfo> items = new ArrayList<>();

        try {
            for (ItemInfo cur : sdmByZone.getListOfAllItems()) {
                wantedPrice = request.getParameter(cur.serialNumber.toString());
                if (!wantedPrice.isEmpty())
                    items.add(new ItemInStoreInfo(cur.serialNumber,Double.parseDouble(wantedPrice)));
            }

        StoreInfo newStore = new StoreInfo(location, null,0d,items,
                null,null,StoreName,PPK,usernameFromParameter,0d);

        mainSDM.AddStore(newStore,Zone);

        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        PrintWriter writer = response.getWriter() ;
        writer.println("Done!");
        writer.flush();

        } catch (NoValidXMLException e) {
            e.printStackTrace();
        } catch (DuplicateItemInStoreException e) {
            e.printStackTrace();
        } catch (DuplicatePointOnGridException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter outy = null;
            try {
                outy = response.getWriter();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            outy.println("There Is A Store At This Location!");
        } catch (StoreDoesNotSellItemException e) {
            e.printStackTrace();
        } catch (NegativePriceException e) {
            e.printStackTrace();
        } catch (StoreItemNotInSystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void AddFeedbacks(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {

        String usernameFromParameter = SessionUtils.getUserName(request);
        Integer rate =  Integer.valueOf(request.getParameter("Rate"));
        String feed =  (request.getParameter("Feed")).trim();
        Long orderid =  Long.valueOf(request.getParameter("orderID"));
        Long storeid =  Long.valueOf(request.getParameter("storeID"));
        String Zone = SessionUtils.getUserCurZone(request);
        FeedBackInfo newFeed = new FeedBackInfo(rate,feed);
        List<StoreInOrderInfo> storeLeft= mainSDM.addFeedback(usernameFromParameter,newFeed,Zone,orderid,storeid);

        Gson gson = new Gson();
        String Stores = gson.toJson(storeLeft);
        System.out.println(Stores);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print(Stores);
        out.flush();

    }

    private void ApproveBuyerOrder(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        Customer customer = mainSDM.getCustomer(usernameFromParameter);
        try {
            OrderInfo orderForStores = sdmByZone.ApproveOrder(customer);
            Gson gson = new Gson();
            String Stores = gson.toJson(orderForStores.Stores);
            System.out.println(Stores);
            PrintWriter out = response.getWriter();
            out.print(Stores);
            out.flush();
        } catch (OrderIsNotForThisCustomerException | IOException e) {
            e.printStackTrace();
        }
    }

    private void FinishOrder(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        Customer customer = mainSDM.getCustomer(usernameFromParameter);
        OrderInfo orderSumUp = null;
        try {
            orderSumUp = sdmByZone.addDiscounts(customer);
        Gson gson = new Gson();
        String order = gson.toJson(orderSumUp);
        System.out.println(order);
        PrintWriter out = response.getWriter();
        out.print(order);
        out.flush();
        } catch (OrderIsNotForThisCustomerException | IOException e) {
            e.printStackTrace();
        }
    }

    private void SendDiscountInfo(HttpServletRequest request, HttpServletResponse response, SuperDuperMarketSystem sdmByZone, MainSystem mainSDM) {
        System.out.println("tait");
        Integer indexInArray = Integer.valueOf(request.getParameter("indexInArray"));
        Integer IndexOfItemWanted =  Integer.valueOf(request.getParameter("IndexOfItemWanted"));
        String usernameFromParameter = SessionUtils.getUserName(request);
        List<DiscountInfo> discountInfos = mainSDM.addDiscount(usernameFromParameter,IndexOfItemWanted,indexInArray);
        Gson gson = new Gson();
        String allItemsJson = gson.toJson(discountInfos);
        System.out.println(allItemsJson);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print(allItemsJson);
        out.flush();

    }

    private void SendCharge(HttpServletRequest request, HttpServletResponse response, MainSystem mainSDM) {
        String usernameFromParameter = SessionUtils.getUserName(request);
        Double amountToAdd = Double.valueOf(request.getParameter("money"));
        String dateInput = request.getParameter("date");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mainSDM.AddMoney(usernameFromParameter,amountToAdd,date);
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
        } catch (DuplicatePointOnGridException e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter outy = response.getWriter();
                outy.println("There Is A Store At This Location!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (WrongPayingMethodException e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter outy = response.getWriter();
                outy.println("Peek At least 1 Items!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
                case GiveNotification:
                    SendNotification(request,response,MainSDM);
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
                if (userRequest.equals(GiveNotification))
                    SendNotification(request,response,MainSDM);
                else{
                    //todo error
                }

            }
        }
    }

    private void SendNotification(HttpServletRequest request, HttpServletResponse response, MainSystem MainSDM) {
        try {
            String UserName = SessionUtils.getUserName(request);
            //Integer size =  Integer.parseInt((request.getParameter("curSize")).trim());
            Integer size =  SessionUtils.getSellerNotify(request);
            if (size== null)
                size =0;
            List<String> notification = MainSDM.getAllNotification(UserName,size);
            size+=notification.size();
            request.getSession(false).setAttribute(Constants.NOTIFYSEEN,size); //added seen..
            Gson gson = new Gson();
            String addNotification = gson.toJson(notification);
            System.out.println(addNotification);
            PrintWriter out = response.getWriter();
            out.print(addNotification);
            out.flush();
        } catch (IOException e) {
            //todo error
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
            Collection<OrderInfo> allOrders = mainSDM.getListOfAllOrderByUser(CurUserName);
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
