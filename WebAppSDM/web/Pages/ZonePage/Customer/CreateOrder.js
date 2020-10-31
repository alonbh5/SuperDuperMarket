var orderIsStatic;
var Items;
var Stores;
var ItemChosen=false;
var Discounts = [];
var storesForFeedBack =[];

var StoreIdForFeedBack;

var orderIdForFeed;



var StoreSum = "http://localhost:8080/WebAppSDM/Pages/ZonePage/Customer/DynamicStores.html";
var DiscountMenu = "http://localhost:8080/WebAppSDM/Pages/ZonePage/All/DiscountPane.html";


function LinkCreateOrderForm() {
    linkOrderType(); //bol bar for stores and shop..
    linkSubmit();//when form is sent what to do
}


function linkSubmit() {
    $('#createOrderForm').submit(function (e) {
        e.preventDefault();

        var flag = false;
        var date = $('#datepicker').val();
        var x = $('#xInput').val();
        var y = $('#yInput').val();
        var choice = $('input[name="orderType"]:checked').val();
        if (date.length === 0)
            alert("Enter Date!");
        else
            if (x.length === 0 || y.length === 0)
                alert("Enter Location!");
            else
                if (choice == null)
                    alert("Pick Order Type!");
                else
                    if (ItemChosen == null)
                        alert("You Didn't Pick Items!");
                    else
                        flag=true;


        if (flag) {
            ItemChosen=false;
            $.ajax({
                data: $('#createOrderForm').serialize(), //{userdata: $('#createOrderForm').serialize(), infoType:"createOrder"},
                url: getZoneInfo,
                type: "POST",
                //while get "seller" or "customer" (with ")
                success: function (DiscountOrSum) {
                    /*
                     data will arrive in array for each is the form:
                     [
                     discounts:[{discount...}]
                     sumUp:[{DiscountOrSum...}]
                     ]
                     */
                    if (orderIsStatic) {
                        //show discount from store (data is json)
                        var Discount = DiscountOrSum;
                        showDiscountsMenu(Discount);
                    } else {
                        //show all items(data is json)
                        var Stores = DiscountOrSum.Stores;
                        $('.main').empty().load(StoreSum, function () {
                            linkOrderSum(Stores);
                        });
                    }
                },
                error: function (data) {
                    alert(data.responseText);
                }

            });
            return false;
        }
    });


}

function showDiscountsMenu(discounts ) {
    if (discounts.length === 0)
        AfterDiscounts();
    else {
        Discounts = [];
        $('.main').empty().load(DiscountMenu, function () {
            linkDiscount(discounts);
            $('#discountOn').on('click',function () {
                AfterDiscounts();
            });
        });
    }
}

function AfterDiscounts() {
        $.ajax({
            data: ServletRequestAttributeName+"finishOrder",
            type: "POST",
            url: getZoneInfo,

            success: function (data) { //this is order
                setBuyerOrder(data,true);

            }
        });
}

function aprroveOrder() {
    $.ajax({
        data: ServletRequestAttributeName+"approveOrder",
        type:"POST",
        url: getZoneInfo,
        success: function (data) {
            storesForFeedBack = data;
            askFeedBack();
        }
    });
}

function askFeedBack() {
    $('.main').empty().load(storeTableURL, function () {


        $.each(storesForFeedBack || [], function(index, store) {

         var aTag = '<a href="#" id="storeName'+index+'" value="'+store.Store.StoreID+'">'+store.Store.Name+'</a>';

            $('#storeBody').append($(' <tr>\n' +
                '    <td>'+store.Store.StoreID+'</td>\n' +
                '    <td>'+aTag +'\n' +
                '</td>\n' +
                '    <td>'+store.Store.Owner+'</td>\n' +
                '  </tr>'));

            var aID = "#storeName"+index;
            $(aID).on("click",function(){
                StoreIdForFeedBack =  parseInt($(this).attr("value"));
                showStoreFeedBack(index);


        });

    });
});
}

function showStoreFeedBack(index) {
    $('.main').empty().load(FeedBackURL, function () {

        $('#sendFeedBack').on('click',function () {

            var rate = $('input[name="rate"]:checked').val();
            var feed = $('#feed').val();
            if (rate !== null) {
                $.ajax({
                    data: {infoType: "addFeedback",Rate:rate,Feed:feed,orderID:orderIdForFeed,storeID:StoreIdForFeedBack},
                    type: "POST",
                    url: getZoneInfo,
                    success: function (data) {
                        if (data.length !== 0) {
                            storesForFeedBack = data;
                            askFeedBack();
                        }
                        else
                            $('.main').empty().append('<h3>No More Stores To Rate!</h3>')
                    }
                });


            }
            else
            {
                //todo error..
            }

        });

    });
}



function linkDiscount(Discount) {

    $.each(Discount || [], function(index, cur) {
        console.log("Discount is" + cur);

        addDiscount(cur);
    });
}

function linkOrderSum(Stores) {

    $.each(Stores || [], function(index, cur) {
        console.log("Store is" + cur);

        addStoreToTable(cur);
    });

    $('.main').append($('<button/>')
        .text('Continue')
        .click(function () {
            showDiscountsMenu();
        }));



}

function addStoreToTable(cur){
    $('#OrderStores').append($('<tr>\n' +
        '<td>'+cur.Store.StoreID+'</td>\n' +
        '<td>'+cur.Store.Name+'</td>\n' +
        '<td>'+cur.Store.PPK+'</td>\n' +
        '<td>'+cur.DistanceFromUser+'</td>\n' +
        '<td>'+cur.Store.locationCoordinate+'</td>\n' +
        '<td>'+cur.ShippingCost+'</td>\n' +
        '<td>'+cur.AmountOfItems+'</td>\n' +
        '<td>$'+cur.PriceOfItems+'</td>\n' +
        '</tr>'))
}

function addDiscount(discount) {
    /*
    public final String Name;
    public final String DiscountOperator;
    public final OfferItemInfo itemToBuy;
    public final Double AmountToBuy;
    public final Long StoreID;
    public final List<OfferItemInfo> OfferedItem;
    public final List<Integer> IndexOfWantedItem = new ArrayList<>();
    */

    var index = Discounts.length;
    Discounts.push(discount);
    var because = "Because You Bought: "+discount.AmountToBuy + " " +discount.itemToBuy.Name  ;
    var AmountLeft = "Amount Left: "+discount.AmountEntitled.value;
    var DiscountType = discount.DiscountOperator;

    if (AmountLeft <= 0) {
        AmountLeft = 0;
    }

    var IndexUniq = "index"+index; //index in ?
    var NextUniq = "next"+index; //next button
    var BuyUniq = "buy"+index; //buy button
    var CurrentItemIndexUniq = "current"+index;
    var maxAmountUniq = "max"+index;
    var DiscountGetUniq = "DiscountGet"+index;

    var Item = discount.OfferedItem[0];
    var ItemString = "Get "+Item.Amount+ " "+ Item.Name + ' (' + Item.ID + ') For: '+Item.PricePerOne+"$ Per One";

    $('#DiscountBody').append($('<div class="column">\n' +
        '        <div class="card">\n' +
        '            <div class="flip-card">\n' +
        '                <div class="flip-card-inner">\n' +
        '                    <div class="flip-card-front">\n' +
        '                        <h3 class="DiscountName">'+discount.Name+'</h3>\n' +
        '                        <p id="Because">'+because+'</p>\n' +
        '                        <p class="AmountLeft">'+AmountLeft+'</p>\n' +
        '                    </div>\n' +
        '                    <div class="flip-card-back">\n' +
        '                        <h3 class="DiscountType">'+DiscountType+'</h3>\n' +
        '                        <p id="'+DiscountGetUniq+'">'+ItemString+'</p>\n' +
        '                        <p class="AmountLeft">'+AmountLeft+' </p>\n' +
        '                        <input type="button" value="Next" id="'+NextUniq+'"><br><br>\n' +
        '                        <input type="button" value="Buy!" id="'+BuyUniq+'">\n' +
        '                        <input type="hidden" value="'+index+'" id="'+IndexUniq+'">\n' +
        '                        <input type="hidden" value="0" id="'+CurrentItemIndexUniq+'">\n' +
        '                        <input type="hidden" value="'+discount.MaxAmount+'" id="'+maxAmountUniq+'">\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '</div>'));


    var nextName = '#'+NextUniq;
    $(nextName).on('click',function () {
        var x1='#'+CurrentItemIndexUniq;
        var x2 = '#'+maxAmountUniq;
        var x3 = '#'+IndexUniq;

        var nextIndex = parseInt($(x1).val()) + 1;
        var IndexInThisArray =  parseInt($(x3).val());
        nextIndex = nextIndex % Discounts[IndexInThisArray].OfferedItem.length;

        var curItem = Discounts[IndexInThisArray].OfferedItem[nextIndex];
        var curItemString = "Get "+curItem.Amount+ " "+ curItem.Name + ' (' + curItem.ID + ') For: '+curItem.PricePerOne+"$ Per One";

        var x4= "#"+DiscountGetUniq;
        var x5= '#'+CurrentItemIndexUniq;
        $(x5).val(nextIndex);
        $(x4).empty().append(curItemString);
    });

    var buyName = '#'+BuyUniq;
    $(buyName).on('click',function () {

        var x1 = '#'+CurrentItemIndexUniq;
        var wantedItemIndex = parseInt($(x1).val());

        $.ajax({
            data: {infoType:"addDiscount",indexInArray:index,IndexOfItemWanted:wantedItemIndex},
            type: "POST",
            url: getZoneInfo,

            success: function (data) {
                showDiscountsMenu(data);
            }
        });
    });
}



function linkOrderType() {

    $('#staticRadio').click(function(){
        ItemChosen = false;
        if ($(this).is(':checked'))
        {
            orderIsStatic = true; //todo show here stores...
            $("#stores").prop("disabled", false);
            if (Stores === null || $('#stores').val() === null) { //to no send over and over
                $('#ItemTitles').empty();
                getStoreCombo();
                LinkStores();
            }
            else
                fillStoreItem();
        }
    });

    $('#dynamicRadio').click(function(){
        ItemChosen = false;
        if ($(this).is(':checked'))
        {
            $("#stores").prop("disabled", true);
            orderIsStatic = false;
                SetAllItems();
        }

    });
}

function changeItemChosen() {
    $('.quantity').on("change",function () {
        console.log("ItemChosen Change to True");
        ItemChosen = true;
    })
}

function  getStoreCombo(){

        $.ajax({
            data: ServletRequestAttributeName+"stores",
            url: getZoneInfo,
            //while get "seller" or "customer" (with ")
            success: function (data) {

                Stores = data;

                $.each(data, function(i, store) {
                    $('#stores').append('<option value='+store.StoreID+'>'+store.StoreID+' - '+store.Name+'</option>'); //todo make sure its like this! balue is store id
                });
            }
        });
}

function LinkStores() {
    $('#stores').on('change', function() {
        var indexInStores = parseInt($('#stores').val());
        var theStore;

        for (var i =0; i < Stores.length ; i++)
            if (indexInStores === Stores[i].StoreID)
                theStore = Stores[i];
        $('#ItemTitles').empty();
        var ItemsInStore = theStore.Items;
        ItemsInStore.forEach(addItem);

    });
}

function fillStoreItem() {
    var indexInStores = parseInt($('#stores').val());
    var theStore;

    for (var i =0; i < Stores.length ; i++)
        if (indexInStores === Stores[i].StoreID)
            theStore = Stores[i];

    $('#ItemTitles').empty();
    var ItemsInStore = theStore.Items;
    ItemsInStore.forEach(addItem);
}

function addItem(item, index, array) {

    var amount = "<input type=\"number\" class=\"quantity\" name=\""+item.serialNumber+"\" step=\"any\" min=\"0.1\" >";
    var price= item.PriceInStore;
    if (price == null)
        price = "-";

    if (item.PayBy.toString().toLowerCase() === "amount")
        amount = "<input type=\"number\" class=\"quantity\" name=\""+item.serialNumber+"\"  min=\"1\" >";

    $('#ItemTitles').append('<tr>\n' +
        '            <td value="'+item.serialNumber+'">'+item.serialNumber+'</td>\n' +
        '            <td value="'+item.serialNumber+'">'+item.Name+'</td>\n' +
        '            <td value="'+item.serialNumber+'">'+item.PayBy+'</td>\n' +
        '            <td value="'+item.serialNumber+'">'+price+'</td>\n' +
        '            <td value="'+item.serialNumber+'" id="amountFromUser">'+amount+'</td>\n' +
        '        </tr>');

    changeItemChosen();
}

function SetAllItems() {

    if (Items == null) {
        $.ajax({
            data: ServletRequestAttributeName + "items",
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
                Items = data;
                $('#ItemTitles').empty();
                Items.forEach(addItem);
            }
        });
    }
    else {
        $('#ItemTitles').empty();
        Items.forEach(addItem);
    }
}

function BindItemsClick() {
    $('td').on('click',function (item) {       //todo show how much you want?
        console.log($(this).attr("value")); //todo this is how to get itemId...
    });
}

