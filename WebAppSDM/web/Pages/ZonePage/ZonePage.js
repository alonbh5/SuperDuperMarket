var CurUserName="";
var CurUserZone="";
var CurUserID="";
var CurUserType="";
var getUserTypeUrl = 'http://localhost:8080/WebAppSDM_war_exploded/getUserType';
$(function () {//todo here you can block no good user..(if he type the url) redirct to

    $.ajax({
        data: '',
        url: getUserTypeUrl,
        //while get "seller" or "customer" (with ")
        success: function (mydata) {
            /*
             data will arrive in the next form:
             {
             "userType":"seller",
             "userName":"Alon",
             "userId":"1",
             ,"userZone":"Tel Aviv"
             }
             */
            CurUserID = mydata.userId;
            CurUserName =mydata.userName;
            CurUserZone = mydata.userZone;
            CurUserType = mydata.userType

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
    }});
});
//====================Buyer====================
function MakeNewOrder() {

}
function OrderHistory() {

}
//====================seller====================
function SellersOrder() {

}
function OpenNewStore() {

}
function ShowFeedback() {

}

