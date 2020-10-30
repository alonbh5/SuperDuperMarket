var CurUserName="";
var CurUserZone="";
var CurUserID="";
var CurUserType="";
var getUserTypeUrl = 'http://localhost:8080/WebAppSDM_war_exploded/getUserType';
var getZoneInfo = 'http://localhost:8080/WebAppSDM_war_exploded/GetZoneInfo';
var ServletRequestAttributeName = "infoType=";

var updateStoresList  = false;

var StoreOrderList = [];
var StoresList = [];
var storeTableURL = "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/All/StoreList.html";
var SingleStoreUrl =  "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/All/Store.html";
var SingleOrderUrl =  "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/All/Order.html";
var OrderTableURL =  "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Seller/SellerOrders.html";
var OrderItemsTableURL =  "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Seller/ItemFromOrder.html";
var OpenStoreURL ="http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Seller/OpenStore.html";
var BuyerOrderTableURL = "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Customer/BuyerOrders.html";
var FeedBackURL = "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Customer/AddFeedBack.html";

$(function () {//todo here you can block no good user..(if he type the url) redirct to

    $('#ItemsButton').on("click",function(){
        updateStoresList = false;
        ShowItems();
    });

    $('#StoreButton').on("click",function(){
        updateStoresList = true;
        ShowStores();
    });

    $.ajax({
        data: '',
        url: getUserTypeUrl,
        //while get "seller" or "customer" (with ")
        success: function (data) {
            /*
             data will arrive in the next form:
             {
             "userType":"seller",
             "userName":"Alon",
             "userId":"1",
             ,"userZone":"Tel Aviv"
             }
             */
            CurUserID = data.userId;
            CurUserName =data.userName;
            CurUserZone = data.userZone;
            CurUserType = data.userType;
            makeBars();

        }});
});

function makeBars() {
    if (CurUserType === "seller"){
        $('#MainBar').append(
            $('<a href="#" id="SellersOrderButton">Orders</a>').on("click",function(){
                updateStoresList = false;
                SellersOrder();
            })).append(
            $('<a href="#" id="ShowFeedbackButton">My FeedBacks</a>').on("click",function(){
                updateStoresList = false;
                ShowFeedback();
            })).append(
            $('<a href="#" id="OpenNewStoreButton">Open New Store</a>').on("click",function(){
                updateStoresList = false;
                OpenNewStore();
            }));
    }
    else { //case buyer
        $('#MainBar').append(
            $('<a href="#" id="MakeOrderButton">New Order</a>').on("click",function(){
                updateStoresList = false;
                MakeNewOrder();
            })).append(
            $('<a href="#" id="OrderHistoryButton">My Order History</a>').on("click",function(){
                updateStoresList = false;
                BuyerOrderHistory();
            }))}

    $('#MyHeader').text("Welcome To "+CurUserZone+"!");
}
//====================All====================
function ShowStores() {

    if (updateStoresList) {
        console.log("checking for new stores..");
        $.ajax({
            data: ServletRequestAttributeName + "stores",
            url: getZoneInfo,
            //while get "seller" or "customer" (with ")
            success: function (data) {
                /*
                data will arrive in array for each is the form:
                [{
                "locationCoordinate":{"x":3,"y":4},
                "StoreID":1,
                "profitFromShipping":0.0,
                "Name":"super baba",
                "PPK":30,
                "Owner":avi
                "Items":[
                            {"serialNumber":1,"Name":"Ketshop","PayBy":"AMOUNT","PriceInStore":20.0,"SoldCounter":0.0}
                            ,{"serialNumber":2,"Name":"Banana","PayBy":"WEIGHT","PriceInStore":10.0,"SoldCounter":0.0},
                            {"serialNumber":5,"Name":"Tomato","PayBy":"WEIGHT","PriceInStore":50.0,"SoldCounter":0.0}
                        ],
                "OrderHistory":[],
                "Discount":[
                            {"Name":"Balabait ishtagea !",
                            "DiscountOperator":"ONE_OF",
                            "itemToBuy":{"ID":1,"Name":"Ketshop","PayBy":"AMOUNT","Amount":1.0,"PricePerOne":0.0},
                            "AmountToBuy":1.0,
                            "StoreID":1,
                            "OfferedItem":[{"ID":5,"Name":"Tomato","PayBy":"WEIGHT","Amount":2.0,"PricePerOne":20.0}
                            ,{"ID":2,"Name":"Banana","PayBy":"WEIGHT","Amount":1.0,"PricePerOne":0.0}],
                            "IndexOfWantedItem":[],
                            "MaxAmount":0,
                            "i":0,
                            "AmountEntitled":{"name":"","value":0,"valid":true},
                            "AmountWanted":{"name":"","value":0,"valid":true}},
                           ]
                }]
                .
                .
                .
                ]
                */

                if (data.length !== StoresList.length)
                    StoresList = data;

                setStoreTable()
            }
        });

    }
}


function ShowItems() {
    $.ajax({
        data: ServletRequestAttributeName+"items",
        url: getZoneInfo,
        //while get "seller" or "customer" (with ")
        success: function (data) {
            /*
             data will arrive in array for each is the form:
             [
             {"serialNumber":1,
             "Name":"Toilet Paper",
             "PayBy":"AMOUNT",
             "AvgPrice":30.5,
             "NumOfSellingStores":2,
             "SoldCount":0},
             .
             .
             .
             ]
             */
            setItemTable();
            $.each(data || [], function(index, username) {
                //create a new <option> tag with a value in it and
                //appeand it to the #userslist (div with id=userslist) element
                SetItem(username);
            });
        }
    });
}
//====================Buyer====================
function MakeNewOrder() {
    //$('.main').empty().append(MakeOrderForm());
    $('.main').empty().load('http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Customer/CreateOrder.html',LinkCreateOrderForm);
    //LinkCreateOrderForm();
}
function BuyerOrderHistory() {
    $.ajax({
        data: ServletRequestAttributeName+"buyerOrders",
        url: getZoneInfo,

        success: function (data) {
            var orders = data; //
            var msg = "Empty! Buy Something "+CurUserName+"!";

            if (orders.length === 0)
                $('.main').empty().append($('<h3>'+msg+'</h3>'));
            else {
                $('.main').empty().load(BuyerOrderTableURL, function () {

                    $.each(orders || [], function (index, order) {
                        SetBuyerOrderForTable(index, order);
                    });
                });
            }
        }
    });
}

function SetBuyerOrderForTable(index, order) {



    var type = "Dynamic";
    if (order.isStatic)
        type = "Static";

    var aTag = '<a href="#" id="orderID'+index+'" value="'+index+'">'+order.m_OrderSerialNumber+'</a>';


    $('#OrderBody').append($('<tr>\n' +
        '        <td>'+aTag+'</td>\n' +
        '        <td>'+type+'</td>\n' +
        '        <td>'+order.m_Date+'</td>\n' +
        '        <td>'+order.m_TotalPrice+'</td>\n' +
        '    </tr>')); //todo add all the detel from order and OrderInfoFromTheStore


    var aID = "#orderID"+index;
    $(aID).on("click",function(){
        setBuyerOrder(order,false);
    })

    //todo back
}

function  setBuyerOrder(order,fromCreate) {
    $('.main').empty().load(SingleOrderUrl, function () {
        var type = "Dynamic";
        if (order.isStatic)
            type = "Static";
        var OrderStores = order.Stores;
        var OrderItems = order.ItemsInOrder;

    /* public final Long m_OrderSerialNumber;
    public final Date m_Date;
    public final List<StoreInOrderInfo> Stores;
    public List<ItemInOrderInfo> ItemsInOrder;
    public final CustomerInfo customer.Location;
    public final Double m_TotalPrice;
    public final Double m_ShippingPrice;
    public final Double m_ItemsPrice;
    public final Integer m_amountOfItems;
    public final boolean isStatic;*/
        orderIdForFeed =order.m_OrderSerialNumber;

        $('#numberTitle').append(order.m_OrderSerialNumber);

        $('#OrderType').append(type);
        $('#CustomerName').append(order.m_OrderSerialNumber);
        $('#Shipping').append(order.m_ShippingPrice);
        $('#PriceNoShipping').append(order.m_ItemsPrice);
        $('#Total').append(order.m_TotalPrice);
        $('#OrderDate').append(order.m_Date);
        $('#OrderID').append(order.m_OrderSerialNumber);
        $('#OrderLocation').append(order.customer.Location);

        $.each(OrderStores || [], function (index, store) {
            $('#OrderStores').append('<tr>\n' +
                '                <td>'+store.Store.StoreID+'</td>\n' +
                '                <td>'+store.Store.Name+'</td>\n' +
                '                <td>'+store.Store.PPK+'</td>\n' +
                '                <td>'+store.DistanceFromUser+'</td>\n' +
                '                <td>'+store.ShippingCost+'</td>\n' +
                '            </tr>');
        });

        $.each(OrderItems || [], function (index, item) {
            var ans = "No";
            if (item.FromSale)
                ans="Yes";

            var storeDet = item.FromStoreName +" #"+item.FromStoreID;

            $('#OrderItems').append(' <tr>\n' +
                '                <td>'+item.serialNumber+'</td>\n' +
                '                <td>'+item.Name+'</td>\n' +
                '                <td>'+item.PayBy+'</td>\n' +
                '                <td>'+storeDet+'</td>\n' +
                '                <td>'+item.amountBought+'</td>\n' +
                '                <td>'+item.PricePerUint+'</td>\n' +
                '                <td>'+item.TotalPrice+'</td>\n' +
                '                <td>'+ans+'</td>\n' +
                '            </tr>');
        });

        if (fromCreate) {

            $('.main').append($('<button/>')
                .text('Approve!')
                .click(function () {
                    aprroveOrder();
                }));

            $('.main').append($('<button/>')
                .text('Cancel!')
                .click(function () {
                    $('.main').empty().append('<h2>Order Was Canceled!</h2>');
                }));
        }

    });
}
//====================seller====================
function SellersOrder() {
    $.ajax({
        data: ServletRequestAttributeName+"sellerOrders",
        url: getZoneInfo,

        success: function (data) {

            StoreOrderList = data;
            if (data.length === 0)
                $('.main').empty().append($('<h3>You Dont Own Any Stores In This Zone</h3>'));
            else
                setStoreOrderTable();
        }
    });
}
function OpenNewStore() {
    OpenStoreFlag = false;
    $('.main').empty().load(OpenStoreURL,function () {

        $.ajax({
            data: ServletRequestAttributeName+"items",
            url: getZoneInfo,

            success: function (data) {
                /*
                 data will arrive in array for each is the form:
                 [
                 {"serialNumber":1,
                 "Name":"Toilet Paper",
                 "PayBy":"AMOUNT",
                 "AvgPrice":30.5,
                 "NumOfSellingStores":2,
                 "SoldCount":0},
                 .
                 .
                 .
                 ]
                 */
                OpenStoreFlag = false;
                $.each(data || [], function(index, item) {
                    OpenStoreItem(item);
                });

            }
        });

        linkSubmitOfCreateStore();

    });
}

function linkSubmitOfCreateStore() {
    $('#OpenStoreForm').submit(function (e) {
        e.preventDefault();

        var flag = false;
        var ppk = $('#PPK').val();
        var storeName = $('#sname').val();
        var x = $('#xInput').val();
        var y = $('#yInput').val();
        if (ppk.length === 0)
            alert("Enter PPK!");
        else if (x.length === 0 || y.length === 0)
            alert("Enter Location!");
        else if (storeName.length === 0)
            alert("Pick Store Name!");
        else if (!OpenStoreFlag)
            alert("You Didn't Pick Items For Store!");
        else
            flag = true;


        if (flag) {
            ItemChosen = false;
            $.ajax({
                data: $('#OpenStoreForm').serialize(), //{userdata: $('#createOrderForm').serialize(), infoType:"createOrder"},
                url: getZoneInfo,
                type: "POST",
                success: function (data) {

                }

            });
            return false;
        }
    });
}

var OpenStoreFlag = false;
function OpenStoreItem(item) {
    var amount = "<input type=\"number\" class=\"quantity\" name=\""+item.serialNumber+"\" step=\"any\" min=\"0.1\" >";
    var id = item.serialNumber;

    $('#ItemTitles').append('<tr>\n' +
        '            <td value="'+item.serialNumber+'">'+item.serialNumber+'</td>\n' +
        '            <td value="'+item.serialNumber+'">'+item.Name+'</td>\n' +
        '            <td value="'+item.serialNumber+'">'+item.PayBy+'</td>\n' +
        '            <td value="'+item.serialNumber+'" id="amountFromUser">'+amount+'</td>\n' +
        '        </tr>');

    $('.quantity').on("change",function () {
        console.log("OpenStoreFlag Change to True");
        OpenStoreFlag = true;
    })
}

function ShowFeedback() {
    $.ajax({
        data: ServletRequestAttributeName+"feedbacks",
        url: getZoneInfo,
        //while get "seller" or "customer" (with ")
        success: function (data) {
            //makeFeedBackMenu(data)
        }
    });
}
//------------------------------helper---------------------------------------
function setItemTable() {

    $('.main').empty().append(table);

}

function setStoreTable() {

    $('.main').empty().load(storeTableURL,function () {

        $.each(StoresList || [], function(index, store) {
            //create a new <option> tag with a value in it and
            //appeand it to the #userslist (div with id=userslist) element
            SetStore(index,store);
        });



        if (updateStoresList)
            setTimeout(ShowStores, 2000);
    });


}

function SetItem(item){
    $('#itemBody').append(' <tr>\\n" +\n' +
        '        "                        <td class=\"column1\">'+item.serialNumber+'</td>\\n" +\n' +
        '        "                        <td class=\"column2\">'+item.Name+'</td>\\n" +\n' +
        '        "                        <td class=\"column3\">'+item.PayBy+'</td>\\n" +\n' +
        '        "                        <td class=\"column4\">'+item.NumOfSellingStores+'</td>\\n" +\n' +
        '        "                        <td class=\"column5\">'+item.AvgPrice+'</td>\\n" +\n' +
        '        "                        <td class=\"column6\">'+item.SoldCount+'</td>\\n" +\n' +
        '        "                    </tr>\\n" +');
}

function SetStore(index,store){


    var aTag = '<a href="#" id="store'+index+'" value="'+index+'">'+store.Name+'</a>';


    $('#storeBody').append($(' <tr>\n' +
        '    <td>'+store.StoreID+'</td>\n' +
        '    <td>'+aTag +'\n' +
        '</td>\n' +
        '    <td>'+store.Owner+'</td>\n' +
        '  </tr>'));

    var aID = "#store"+index;
    $(aID).on("click",function(){
        updateStoresList = false;
        var indexInArray =  $(this).attr("value");
        makeStoreInfo(StoresList[indexInArray]);
    })
}

function setStoreOrderTable (stores) {
    $('.main').empty().load(storeTableURL,function () {

        $.each(StoreOrderList || [], function(index, store) {
            SetStoreForOrder(index,store);
        });
    });
}

function SetStoreForOrder(index,store) {
    var aTag = '<a href="#" id="store'+index+'" value="'+index+'">'+store.Name+'</a>';


    $('#storeBody').append($(' <tr>\n' +
        '    <td>'+store.StoreID+'</td>\n' +
        '    <td>'+aTag +'\n' +
        '</td>\n' +
        '    <td>'+store.Owner+'</td>\n' +
        '  </tr>'));

    var aID = "#store"+index;
    $(aID).on("click",function(){
        var indexInArray =  $(this).attr("value");
        makeOrderTable(StoreOrderList[indexInArray]);
    })
}

function makeOrderTable(store) {
    var orders = store.OrderHistory; //
    //todo make table -> click to see only one order..back

    if (orders.length === 0)
        $('.main').empty().append($('<h3>Order List In Store Is Empty!</h3>'));
    else {
        $('.main').empty().load(OrderTableURL, function () {

            $.each(orders || [], function (index, order) {
                SetOrderForTable(index, order, store.StoreID);
            });
        });
    }

    $('.main').append($('<button/>')
        .text('Back')
        .click(function () {
            SellersOrder();
        }));
}

function SetOrderForTable(index,order,id) {

    /* public final Long m_OrderSerialNumber;
    public final Date m_Date;
    public final List<StoreInOrderInfo> Stores;
    public List<ItemInOrderInfo> ItemsInOrder;
    public final CustomerInfo customer;
    public final Double m_TotalPrice;
    public final Double m_ShippingPrice;
    public final Double m_ItemsPrice;
    public final Integer m_amountOfItems;
    //public final Double Distance;
    //public final Integer StaticPPK;
    public final boolean isStatic;*/

    var type = "Dynamic";
    var OrderInfoFromTheStore;
    var amountItems = order.ItemsInOrder.length;

    var theStores = order.Stores;

    $.each(theStores || [], function(index, store) {
        if (store.Store.StoreID === id)
            OrderInfoFromTheStore = store;
    });

    if (order.isStatic)
        type = true;

    var aTag = '<a href="#" id="items'+index+'" value="'+index+'">'+amountItems+'</a>';


    $('#OrderBody').append($('<tr>\n' +
        '        <td>'+order.m_OrderSerialNumber+'</td>\n' +
        '        <td>'+type+'</td>\n' +
        '        <td>'+order.m_Date+'</td>\n' +
        '        <td>'+order.customer.name+'</td>\n' +
        '        <td>'+order.customer.Location+'</td>\n' +
        '        <td>'+aTag+'</td>\n' +
        '        <td>'+OrderInfoFromTheStore.PriceOfItems+'</td>\n' +
        '        <td>'+OrderInfoFromTheStore.ShippingCost+'</td>\n' +
        '    </tr>')); //todo add all the detel from order and OrderInfoFromTheStore


    var aID = "#items"+index;
    $(aID).on("click",function(){
        setOrderItemsTable(order.ItemsInOrder);
    })
}

function setOrderItemsTable(items) {
    $('.main').empty().load(OrderItemsTableURL,function () {

        $.each(items || [], function(index, item) {
            SetOrderItem(index,item);
        });

        $('.main').append($('<button/>')
            .text('Back')
            .click(function () {
                updateStoresList = false;
                SellersOrder();
            }));
    });
}

function SetOrderItem(index,item) {
    /*public final Long serialNumber;
    public final Boolean FromSale;
    public final String Name;
    public final String PayBy;
    public final Long FromStoreID;
    public final String FromStoreName;
    public Double amountBought;
    public final Double PricePerUint;
    public final Double TotalPrice;*/
    var sale = "No";
    if (item.FromSale)
        sale = "Yes";

    $('#OrderItemsBody').append($('<tr>\n' +
        '        <td>'+item.serialNumber+'</td>\n' +
        '        <td>'+item.Name+'</td>\n' +
        '        <td>'+item.PayBy+'</td>\n' +
        '        <td>'+item.PricePerUint+'</td>\n' +
        '        <td>'+item.amountBought+'</td>\n' +
        '        <td>'+item.TotalPrice+'</td>\n' +
        '        <td>'+sale+'</td>\n' +
        '    </tr>'));


}

function makeStoreInfo(store){

    /*public final Point locationCoordinate;
    public final String Owner;
     public final String locationPoint;
    public final Long StoreID;
    public final Double profitFromShipping;
    public final Double ProfitFromItems
    public final List<ItemInStoreInfo> Items;
    public final List<OrderInfo> OrderHistory ;
    public final List<DiscountInfo> Discount;
    public final String Name;
    public final Integer PPK;*/

    var storeItems = store.Items;
    var Orders = store.OrderHistory.length;

    $('.main').empty().load(SingleStoreUrl,function () {

        $('#NameTitle').append(store.Name);
        $('#StoreID').append(store.StoreID);
        $('#StoreName').append(store.Name);
        $('#OwnerName').append(store.Owner);
        $('#Location').append(store.locationPoint);
        $('#PPK').append(store.PPK);
        $('#ItemsProfit').append(store.ProfitFromItems);
        $('#ShippingProfit').append(store.profitFromShipping);
        $('#TotalOrders').append(Orders);


        $.each(storeItems || [], function(index, item) {

            $('#StoreItems').append($('<tr>\n' +
                '                <td>'+item.serialNumber+'</td>\n' +
                '                <td>'+item.Name+'</td>\n' +
                '                <td>'+item.PayBy+'</td>\n' +
                '                <td>'+item.SoldCounter+'</td>\n' +
                '                <td>'+item.PriceInStore+'</td>\n' +
                '            </tr>'))
        });

        $('.main').append($('<button/>')
            .text('Back')
            .click(function () {
                updateStoresList = true;
                ShowStores();
            }));

    });


}

var table = "<div class=\"limiter\">\n" + //todo this with ajax?
    "    <div class=\"container-table100\">\n" +
    "        <div class=\"wrap-table100\">\n" +
    "            <div class=\"table100\">\n" +
    "                <table>\n" +
    "                    <thead>\n" +
    "                    <tr class=\"table100-head\">\n" +
    "                        <th class=\"column1\">Serial Number</th>\n" +
    "                        <th class=\"column2\">Name</th>\n" +
    "                        <th class=\"column3\">Purchase Method </th>\n" +
    "                        <th class=\"column4\">Selling Stores</th>\n" +
    "                        <th class=\"column5\">Average Price</th>\n" +
    "                        <th class=\"column6\">Sold Counter</th>\n" +
    "                    </tr>\n" +
    "                    </thead>\n" +
    "                    \n" +
    "                    <tbody id='itemBody'>\n" +
    "                    </tbody>\n" +
    "                </table>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "    </div>\n" +
    "</div>";

