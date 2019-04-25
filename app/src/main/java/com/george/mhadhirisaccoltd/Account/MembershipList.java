package com.george.mhadhirisaccoltd.Account;

import java.util.Date;

/**
 * Created by George on 3/9/2019.
 */

public class MembershipList extends com.george.mhadhirisaccoltd.postID {
    private String user_id;
    private Date timeStamp;

    public MembershipList() {
    }

    public MembershipList(String user_id, Date timeStamp) {
        this.user_id = user_id;
        this.timeStamp = timeStamp;
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
