var Version = 0;
var refreshRate = 2000; //milli seconds
var updateArea = false;
var getAreasUrl = "http://localhost:8080/WebAppSDM_war_exploded/getAreas";
var getUserTypeUrl = 'http://localhost:8080/WebAppSDM_war_exploded/getUserType';
var UserListUrl = 'http://localhost:8080/WebAppSDM_war_exploded/UsersList';
var getZoneInfo = 'http://localhost:8080/WebAppSDM_war_exploded/GetZoneInfo';
var ServletRequestAttributeName = "infoType=";

//todo I need to if areas are changes!

$(function () {//todo here you can block no good user..(if he type the url) redirct to

    $('#HomeButton').on("click",function(){
        if (!updateArea)
             SetAres();
    });

    $('#AddedButton').on("click",function(){
        ShowAccount();
    });

    SetAres();

    $.ajax({
        data:'',
        url: getUserTypeUrl,
        //while get "seller" or "customer" (with ")
        success: function(data) {

            /*
             data will arrive in the next form:
             {
             "userType":"seller",
             "userName":"Alon",
             "userId":"1",
             ,"userZone":"Tel Aviv"
             }
             */

            if (data.userType.includes("seller")){
                $('#MainBar').append(
                    $('<a href="#" id="uploadButton">Upload New Zone</a>').on("click",function(){
                        HandelFile();
                    }));
            }
            else {
                //todo
            }
        }

    });

    setInterval(ajaxUsersList, refreshRate);

});

function ShowAccount() {
    SetAccount();

    $.ajax({
        data: ServletRequestAttributeName+"wallet",
        url: getZoneInfo,
        //while get "seller" or "customer" (with ")
        success: function (data) { //todo
            /*
             data will arrive in array for each is the form:
             {
             "Balance":0.0,
             "AllTransactions":[
             ...
             ]
             }

             */

            //todo append to accountBody (ID) /*<tr>\n" +
            //         "                        <td class=\"column1\">2017-09-29 01:22</td>\n" +
            //         "                        <td class=\"column2\">200398</td>\n" +
            //         "                        <td class=\"column3\">iPhone X 64Gb Grey</td>\n" +
            //         "                        <td class=\"column4\">$999.00</td>\n" +
            //         "                        <td class=\"column5\">1</td>\n" +
            //         "                        <td class=\"column6\">$999.00</td>\n" +
            //         "                    </tr>\n" +
            //         "                    \n" +
            //         "\n" +*/
            $("#balance").text(data.Balance);
            $.each(data.AllTransactions || [], SetTransatcion(index, transction));
        }
    });
}

function SetAccount() {
    $('.main').empty().append("<div class=\"limiter\">\n" +
        "    <div class=\"container-table100\">\n" +
        "        <div class=\"wrap-table100\"><div class=\"balanceDiv\"> <br> <h1 id=\"balance\">150</h1><br><br></div>\n" +
        "            <div class=\"table100\">\n" +
        "                <table>\n" +
        "                    <thead>\n" +
        "                    <tr class=\"table100-head\">\n" +
        "                        <th class=\"column1\">Transaction ID</th>\n" +
        "                        <th class=\"column2\">Type</th>\n" +
        "                        <th class=\"column3\">Date</th>\n" +
        "                        <th class=\"column4\">Amount</th>\n" +
        "                        <th class=\"column5\">Balance Before</th>\n" +
        "                        <th class=\"column6\">Balance After</th>\n" +
        "                    </tr>\n" +
        "                    </thead>\n" +
        "                    \n" +
        "                    <tbody id='accountBody'>\n" +
        "                    </tbody>\n" +
        "                </table>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>");
}

function SetTransatcion(index, transction) {

$('#accountBody').append(' <tr>\\n" +\n' +
    '        "                        <td class=\\"column1\\">transction.SerialNumber</td>\\n" +\n' +
    '        "                        <td class=\\"column2\\">transction.transactionMethod</td>\\n" +\n' +
    '        "                        <td class=\\"column3\\">transction.date</td>\\n" +\n' +
    '        "                        <td class=\\"column4\\">transction.AmountOfTransaction</td>\\n" +\n' +
    '        "                        <td class=\\"column5\\">transction.BalanceBefore</td>\\n" +\n' +
    '        "                        <td class=\\"column6\\">transction.BalanceAfter</td>\\n" +\n' +
    '        "                    </tr>\\n" +');
}


function SetAres() {
    $('.main').empty().append('<h2>Areas Information</h2><br>\n' +
        '        <table id="AreasTable">\n' +
        '            <tr>\n' +
        '                <th>User Name</th>\n' +
        '                <th>Area Name</th>\n' +
        '                <th>Amount Of Stores</th>\n' +
        '                <th>Amount Of Items</th>\n' +
        '                <th>Amount Of Orders</th>\n' +
        '                <th>Average Order Price</th>\n' +
        '            </tr>\n' +
        '        </table>');
    //todo set interval for update
    updateArea=true;
    Version = 0;
    setTimeout(showAreas,refreshRate);
}



function showAreas() {

    $.ajax({
        data: "areasVersion=" +Version,
        url: getAreasUrl,
        dataType: 'json',
        success: function (data) {
            /*
             data will arrive in the next form:
             {
                "entries": [
                    {
                        "chatString":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "chatString":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
            if (data.version !== Version) {
                Version = data.version;
                appendToAreas(data.entries);
            }

        }

    });

    if (updateArea)
        setTimeout(showAreas,refreshRate);
}

function appendToAreas(entries) {

    // add the relevant entries
    $.each(entries || [], appendAreaEntry);
}

function appendAreaEntry(index, entry) {
    var entryElement = createAreaEntry(entry);
    $("#AreasTable").append(entryElement);
}

function createAreaEntry(entry) {

    var zone=entry.Zone;
    return $('<tr>\n' +
        '       <td>'+entry.UserName+'</td>\n' +
        '       <td><a href="http://localhost:8080/WebAppSDM_war_exploded/LocalZones?zoneSelected='+entry.Zone+'">'+entry.Zone+'</a></td>\n' +
        '       <td>'+entry.AmountOfItems+'</td>\n' +
        '       <td>'+entry.AmountOfStores+'</td>\n' +
        '       <td>'+entry.AmountOfOrder+'</td>\n' +
        '       <td>'+entry.AvgOrderPriceWithoutShipping+'</td>\n' +
        '     </tr>')
}

function ajaxUsersList() {
    $.ajax({
        url: UserListUrl,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}
//todo change this to get dom
function HandelFile() {
    //todo unset interval for update
    updateArea=false;

    $('.main').empty().append("<form id='SendFile' action=\"../../upload\" enctype=\"multipart/form-data\" method=\"POST\">\n" +
        "    <input id='uploadButton' type=\"file\" value=\"Choose File\" accept=\"text/xml\" name=\"file1\"><br>\n" +
        "    <input id='submitButton' type=\"Submit\" value=\"Upload File\" ><br>\n" +
        "</form>");

    $('#SendFile').submit(function (e) {

        e.preventDefault();
        var form = $('form')[0]; // You need to use standard javascript object here
        var formData = new FormData(form);
        $.ajax({
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            url: this.action,
            error: function (data) {
                $('#EndLine').text(data)
            },
            success: function (data) {
                $('#EndLine').text(data)
            }

        });
        return false;
    });
}




   // $('#uploadButton').onclick($('#submitButton').setAttribute("disabled","false"));

