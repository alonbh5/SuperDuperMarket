var orderIsStatic;
var date;
var Store;

function MakeOrderForm (stores) {
    return $("<form id='createOrderForm'></form>")
        .append('<li>\n' +
            '\t\t<label for="datepicker">Please Choose Order Date :</label>\n' +
            '        <input type="date" id="datepicker" name="datepicker">\n' +
            '        </li><br>\n' +
            '        \n' +
            '        <li>   \n' +
            '        <label for="orderType">Please Choose Order Type:</label>\n' +
            '        <input  type="radio" id="staticRadio" name="orderType" value="static">\n' +
            '        <label for="staticRadio">Static Order</label>\n' +
            '        <input type="radio" id="dynamicRadio" name="orderType" value="costumer">\n' +
            '        <label for="dynamicRadio">Dynamic Order</label><br>\n' +
            '        </li><br>\n' +
            '        \n' +
            '     \t<li>\n' +
            '        <label for="stores">Choose a Store:</label>\n' +
            '   \t    <select name="stores" id="stores">\n' +
            '        <option value="1">Volvo</option>\n' +
            '        <!--<option value="saab">Saab</option>\n' +
            '        <option value="opel">Opel</option>\n' +
            '        <option value="audi">Audi</option>-->\n' +
            '  \t    </select>\n' +
            '        </li>'+' <br><br>\n' +
            '    <input type="submit" value="Continue">\n' +
            '<input type="hidden" name="infoType" value="createOrder">'+
            '</form>');

    // .append('<label for="stores">Choose a Store:</label>')
        //.append('<select name="stores" id="stores">') //add <option value="saab">Saab</option>
}

function LinkCreateOrderForm() {
    linkOrderType();

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

function addStore(store) {
    /*<option value="volvo">Volvo</option>\n' +
            '        <!--<option value="saab">Saab</option>\n' +
            '        <option value="opel">Opel</option>\n' +
            '        <option value="audi">Audi</option>-->\n' +*/
    $('#stores').append('<option value='+store.id+'>'+store.id+' - '+store.name+'</option>'); //todo make sure its like this! balue is store id
}

function linkOrderType() {
    $('#staticRadio').on('ifChecked', function(event){
        orderIsStatic = true; //todo show here stores...
    });

    $('#dynamicRadio').on('ifChecked', function(event){
        orderIsStatic = false;
    });
}
