

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
            //true so it redirect the user.
            return true; //todo ajax?
        }
        // false so it doesn't redirect the user.
        return false;
    });
});