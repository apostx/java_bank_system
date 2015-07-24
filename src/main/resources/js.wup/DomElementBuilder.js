wup = wup || {};
wup.DomElementBuilder = wup.DomElementBuilder || {};

! function() {
    "use strict";
    
    var t = wup.DomElementBuilder;

    t.fillTable = function(jqueryTable,headers,data) {
        
        
        
        $.each(transactions, function (i, transaction) {
                            row = $("<tr>");
                            row.append($('<td>', { text : transaction.account_number.match(/.{8}/g).join("-") }));
                            row.append($('<td>', { text : transaction.currency }));
                            row.append($('<td>', { text : transaction.amount }));
                            row.append($('<td>', { text : transaction.out ? "-" : "+" }));
                            row.append($('<td>', { text : transaction.balance }));
                            table.append(row);
                        });
    };
};
