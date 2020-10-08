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
                        console.log("noooo"); //todo code here for file...
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