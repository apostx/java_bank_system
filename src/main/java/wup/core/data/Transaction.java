package wup.core.data;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * It's a data class to read datas from database and to create json
 */
public class Transaction {
    @JsonProperty("account_number")
    private String _accountNumber;

    @JsonProperty("currency")
    private String _currency;

    @JsonProperty("amount")
    private int _amount;

    @JsonProperty("out")
    private boolean _out;

    @JsonProperty("balance")
    private int _balance;

    public Transaction(){}

    public Transaction(String accountNumber,String currency,int amount,boolean out,int balance) {
        _accountNumber = accountNumber;
        _currency = currency;
        _amount = amount;
        _out = out;
        _balance = balance;
    }
}
