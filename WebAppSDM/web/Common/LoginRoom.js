

$(function() {

    $('#loginSubmitForm').submit(function () {

        var curForm = $(this);
        var FormUrl = curForm.attr('action');
        var FormType = curForm.attr('method');

        if ($('#UserNameTextBox').val().length <= 0)
            $('#ErrorMassage').text('Please Enter User Name!');
        else if (!$('#seller').is(':checked') && !$('#costumer').is(':checked'))
            $('#ErrorMassage').text('Please Select User Type!');
        else {

            $.ajax({
                data: curForm.serialize(),
                url: FormUrl,
                type:FormType,
                error: function () {
                    console.error("Failed to submit");
                },
                success: function (r) {
                    //do not add the user string to the chat area
                    //since it's going to be retrieved from the server
                    //$("#result h1").text(r);
                }
            });

            //$("#UserNameTextBox").val("");

        }
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});