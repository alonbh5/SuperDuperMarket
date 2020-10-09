var refreshRate = 2000; //milli seconds

$(function () {//todo here you can block no good user..(if he type the url) redirct to

    //

    $.ajax({
        data:'',
        url: 'http://localhost:8080/WebAppSDM_war_exploded/getUserType',
        //while get "seller" or "customer" (with ")
        success: function(data) {

            if (data.includes("seller")){
                $('#MainBar').append(
                    $('<a href="#" id="uploadButton">Upload New Zone</a>').on("click",function(){
                        HandelFile();
                    }));
            }
            else {
                console.log("noooo");
            }
        }

    });

    setInterval(ajaxUsersList, refreshRate);

});

function ajaxUsersList() {
    $.ajax({
        url: 'http://localhost:8080/WebAppSDM_war_exploded/UsersList',
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
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}
//todo change this to get dom
function HandelFile() {
    $('.main').empty().append("<form id='SendFile' action=\"../../upload\" enctype=\"multipart/form-data\" method=\"POST\">\n" +
        "    <input id='uploadButton' type=\"file\" value=\"Choose File\" accept=\"text/xml\" name=\"file1\"><br>\n" +
        "    <input id='submitButton' type=\"Submit\" value=\"Upload File\" ><br>\n" +
        "<label for='uploadButton'>Select File </label>"+
        "</form>");

    $('SendFile').submit(
        function () {
             $.ajax({
                data:this.serialize(),
                contentType:false,
                processData:false,
                url: 'http://localhost:8080/WebAppSDM_war_exploded/upload',
            //while get "seller" or "customer" (with ")
                success: function(data) {

                }});
            return false;});
   // $('#uploadButton').onclick($('#submitButton').setAttribute("disabled","false"));

}