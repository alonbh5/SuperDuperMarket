var CurUserName="";
var CurUserZone="";
var CurUserID="";
var CurUserType="";
var getUserTypeUrl = 'http://localhost:8080/WebAppSDM_war_exploded/getUserType';
var getZoneInfo = 'http://localhost:8080/WebAppSDM_war_exploded/GetZoneInfo';
var ServletRequestAttributeName = "infoType=";


$(function () {//todo here you can block no good user..(if he type the url) redirct to

    $('#ItemsButton').on("click",function(){
            ShowItems();
    });

    $('#StoreButton').on("click",function(){
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
                SellersOrder();
            })).append(
            $('<a href="#" id="ShowFeedbackButton">My FeedBacks</a>').on("click",function(){
                ShowFeedback();
            })).append(
            $('<a href="#" id="OpenNewStoreButton">Open New Store</a>').on("click",function(){
                OpenNewStore();
            }));
    }
    else { //case buyer
        $('#MainBar').append(
            $('<a href="#" id="MakeOrderButton">New Order</a>').on("click",function(){
                MakeNewOrder();
            })).append(
            $('<a href="#" id="OrderHistoryButton">My Order History</a>').on("click",function(){
                BuyerOrderHistory();
            }))}

    $('#MyHeader').text("Welcome To "+CurUserZone+"!");
}
//====================All====================
function ShowStores() {
    $.ajax({
        data: ServletRequestAttributeName+"stores",
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
        }
    });
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

}
function BuyerOrderHistory() {
    $.ajax({
        data: ServletRequestAttributeName+"buyerOrders",
        url: getZoneInfo,

        success: function (data) {
        }
    });
}
//====================seller====================
function SellersOrder() {
    $.ajax({
        data: ServletRequestAttributeName+"sellerOrders",
        url: getZoneInfo,

        success: function (data) {
        }
    });
}
function OpenNewStore() {

}
function ShowFeedback() {
    $.ajax({
        data: ServletRequestAttributeName+"feedbacks",
        url: getZoneInfo,
        //while get "seller" or "customer" (with ")
        success: function (data) {
        }
    });
}
//------------------------------helper---------------------------------------
function setItemTable() {

    $('.main').empty().append(table);

}

function SetItem(item){
    $('#accountBody').append(' <tr>\\n" +\n' +
        '        "                        <td class=\"column1\">'+item.serialNumber+'</td>\\n" +\n' +
        '        "                        <td class=\"column2\">'+item.Name+'</td>\\n" +\n' +
        '        "                        <td class=\"column3\">'+item.PayBy+'</td>\\n" +\n' +
        '        "                        <td class=\"column4\">'+item.NumOfSellingStores+'</td>\\n" +\n' +
        '        "                        <td class=\"column5\">'+item.AvgPrice+'</td>\\n" +\n' +
        '        "                        <td class=\"column6\">'+item.SoldCount+'</td>\\n" +\n' +
        '        "                    </tr>\\n" +');
}

var table = "<div class=\"limiter\">\n" +
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
    "                    <tbody id='accountBody'>\n" +
    "                    </tbody>\n" +
    "                </table>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "    </div>\n" +
    "</div>";