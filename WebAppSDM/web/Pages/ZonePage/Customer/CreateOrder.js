var orderIsStatic;
var Items;
var Stores;
var ItemChosen;

function MakeOrderForm (stores) {
    return ('<form action="http://localhost:8080/WebAppSDM_war_exploded/GetZoneInfo" method="post">\n' +
        '    <li>\n' +
        '        <label for="datepicker">Please Choose Order Date :</label>\n' +
        '        <input type="date" id="datepicker" name="datepicker">\n' +
        '    </li>\n' +
        '    <br>\n' +
        '    <li>\n' +
        '        <h4 for="orderType">Please Choose Order Type:</h4>\n' +
        '        <input  type="radio" id="staticRadio" name="orderType" value="static">\n' +
        '        <label for="staticRadio">Static Order</label>\n' +
        '        <input type="radio" id="dynamicRadio" name="orderType" value="dynamic">\n' +
        '        <label for="dynamicRadio">Dynamic Order</label><br>\n' +
        '    </li>\n' +
        '    <br>\n' +
        '    <li>\n' +
        '        <label for="stores">Choose a Store:</label>\n' +
        '        <select name="stores" id="stores" >\n' +
        '        <option disabled selected value> -- Select a Store -- </option>\n' +
        '        </select>\n' +
        '    </li>\n' +
        '    <br>\n' +
        '\n' +
        '    <table id="OrderItems">\n' +
        '        <thead>\n' +
        '        <tr>\n' +
        '            <th>Item ID</th>\n' +
        '            <th>Item Name</th>\n' +
        '            <th>Purchase By</th>\n' +
        '            <th>Price</th>\n' +
        '            <th>Wanted Amount</th>\n' +
        '        </tr>\n' +
        '        </thead>\n' +
        '        <tbody id="ItemTitles">\n' +
        '        </tbody>\n' +
        '\n' +
        '\n' +
        '\n' +
        '    </table>\n' +
        '\n' +
        '    <br><br>\n' +
        '    <input type="submit" value="Continue">\n' +
        '    <input type="hidden" name="infoType" value="createOrder"> <!--for Servlet To Know what to do-->\n' +
        '</form>');

    // .append('<label for="stores">Choose a Store:</label>')
        //.append('<select name="stores" id="stores">') //add <option value="saab">Saab</option>
}

function LinkCreateOrderForm() {
    linkOrderType(); //bol bar for stores and shop..
    linkSubmit();//when form is sent what to do

}


function linkSubmit() {
    $('#createOrderForm').submit(function () {
        $.ajax({
            data: $('#createOrderForm').serialize(), //{userdata: $('#createOrderForm').serialize(), infoType:"createOrder"},
            url: getZoneInfo,
            type: "POST",
            //while get "seller" or "customer" (with ")
            success: function (data) {
                /*
                 data will arrive in array for each is the form:
                 [

                 */
                if (orderIsStatic) {
                    //show items from store (data is json)
                }
                else
                {
                    //show all items(data is json)
                }
            }
        });
    })
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

    var amount = "<input type=\"number\" id=\"quantity\" name=\"quantity\" step=\"any\" min=\"0.1\" >";
    var price= item.PriceInStore;
    if (price == null)
        price = "-";

    if (item.PayBy.toString().toLowerCase() === "amount")
        amount = "<input type=\"number\" id=\"quantity\" name=\"quantity\"  min=\"1\" >";

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

