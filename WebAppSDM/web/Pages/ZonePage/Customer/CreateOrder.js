var orderIsStatic;
var Items;
var Stores;
var ItemChosen;
var Discounts = [];


var StoreSum = "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/Customer/DynamicStores.html";
var DiscountMenu = "http://localhost:8080/WebAppSDM_war_exploded/Pages/ZonePage/All/DiscountPane.html";


function LinkCreateOrderForm() {
    linkOrderType(); //bol bar for stores and shop..
    linkSubmit();//when form is sent what to do
}


function linkSubmit() {
    $('#createOrderForm').submit(function (e) {
        e.preventDefault();

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
                    $('.main').empty().load(DiscountMenu,function () {linkDiscount(Discount);});
                }
                else
                {
                    //show all items(data is json)
                    var Stores = DiscountOrSum.Stores;
                    $('.main').empty().load(StoreSum,function () {linkOrderSum(Stores);});
                }
            }

        });
        return false;
    })
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

    var IndexUniq = "index"+index;
    var NextUniq = "next"+index;
    var BuyUniq = "buy"+index;

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
        '                        <p id="DiscountGet">'+ItemString+'</p>\n' +
        '                        <p class="AmountLeft">'+AmountLeft+' </p>\n' +
        '                        <input type="button" value="Next" id="'+NextUniq+'"><br><br>\n' +
        '                        <input type="button" value="Buy!" id="'+BuyUniq+'">\n' +
        '                        <input type="hidden" value="'+index+'" id="'+IndexUniq+'">\n' +
        '                        <input type="hidden" value="'+index+'" id="CurrentIndex">\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '</div>'));


    $('#NextUniq').onclick(function (IndexUniq) {

    });

    $('#BuyUniq').onclick(function (IndexUniq) {

    });
}



function linkOrderType() {

    $('#staticRadio').click(function(){
        if ($(this).is(':checked'))
        {
            orderIsStatic = true; //todo show here stores...
            $("#stores").prop("disabled", false);
            if (Stores == null) { //to no send over and over
                $('#ItemTitles').empty();
                getStoreCombo();
                LinkStores();
            }
            else
                fillStoreItem();
        }
    });

    $('#dynamicRadio').click(function(){
        if ($(this).is(':checked'))
        {
            $("#stores").prop("disabled", true);
            orderIsStatic = false;
                SetAllItems();
        }
    });
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
        var indexInStores = this.value - 1;
        $('#ItemTitles').empty();
        var ItemsInStore = Stores[indexInStores].Items;
        ItemsInStore.forEach(addItem);

    });
}

function fillStoreItem() {
    var indexInStores = $('#stores').val() - 1;
    $('#ItemTitles').empty();
    var ItemsInStore = Stores[indexInStores].Items;
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

