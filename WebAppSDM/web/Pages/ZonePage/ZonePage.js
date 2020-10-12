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
        if (!updateArea)
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
                OrderHistory();
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
        }
    });
}
//====================Buyer====================
function MakeNewOrder() {

}
function OrderHistory() {
    $.ajax({
        data: ServletRequestAttributeName+"buyerOrders",
        url: getZoneInfo,

        success: function (data) {
        }
    });
}
//====================seller====================
function SellersOrder() {

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

