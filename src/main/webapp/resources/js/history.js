// selected account number handling
$(function() {
    $("#account_number").on("change",onChange);
                
    /**
     * Init DOM elements and send the service a request
     * 
     * @param {jQuery.Event} e Change event.
     */
    function onChange(e) {
        $("#account_number").prop('disabled',true);            
        $("#history").prop('hidden',false);
        $("#history").empty();
        $("#history").html("Loading...");
        $.getJSON( "service?account_number=" + e.currentTarget.value,onComplete);
    }
                
    /**
     * Process datas from service and generate a table from the datas
     * 
     * @param {Object[]} transactions List of tranactions infos.
     */
    function onComplete(transactions) {
        $("#history").empty();
                    
        if (transactions.length === 0)
            $("#history").html("Empty list");
                    
        else {
            var table = $('<table>',{border:1});
            var headers = ["Source/Target Account","Currency","Amount","Transfer Direction","Balance"];
            var row = $("<tr>");
                        
            $.each(headers, function (i, header) {
                row.append($('<th>', { text : header }));
            });
            table.append(row);
                        
            $.each(transactions, function (i, transaction) {
                row = $("<tr>");
                row.append($('<td>', { text : transaction.account_number.match(/.{8}/g).join("-") }));
                row.append($('<td>', { text : transaction.currency }));
                row.append($('<td>', { text : transaction.amount }));
                row.append($('<td>', { text : transaction.out ? "-" : "+" }));
                row.append($('<td>', { text : transaction.balance }));
                table.append(row);
            });

            $("#history").append(table);
        }
        $("#account_number").prop('disabled',false);  
    }
});
