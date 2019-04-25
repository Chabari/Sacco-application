package com.george.mhadhirisaccoltd.MainIssues.Loans;

import java.util.Date;

/**
 * Created by sikanga on 25/02/2019.
 */

public class LoanList extends com.george.mhadhirisaccoltd.postID {
    private String amount,user_id;
    private Date timeStamp;

    public LoanList() {
    }

    public LoanList(String amount, String user_id, Date timeStamp) {
        this.amount = amount;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
