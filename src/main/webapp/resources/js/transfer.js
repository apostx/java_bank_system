// transfer permission
$(function() {
    if($("#transfer_form").length <= 0)
        return;
                
    $("#transfer_form").submit(onClick);
                
    function onClick() {
        if(confirm("Are you sure?!")) {
            var f = $("#transfer_form");
            var value = f.find("input[name=target_account_number_formatted]").val().replace(/-/g,"");
            f.find("input[name=target_account_number]").val(value);
        } else
            event.preventDefault();
    }
});


