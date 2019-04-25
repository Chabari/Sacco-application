package com.george.mhadhirisaccoltd.MainIssues.Loans;

import java.util.Date;

/**
 * Created by George on 3/10/2019.
 */

public class LoanApprovalList extends com.george.mhadhirisaccoltd.postID {
    private String user_id;
    private Date time;

    public LoanApprovalList() {
    }

    public LoanApprovalList(String user_id, Date time) {
        this.user_id = user_id;
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
