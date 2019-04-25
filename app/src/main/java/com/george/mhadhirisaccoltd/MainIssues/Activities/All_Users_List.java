package com.george.mhadhirisaccoltd.MainIssues.Activities;

import java.util.Date;

/**
 * Created by George on 3/14/2019.
 */

public class All_Users_List extends com.george.mhadhirisaccoltd.postID {
    private String  user_id, rights;
    private Date time;

    public All_Users_List() {
    }

    public All_Users_List(String user_id, String rights, Date time) {
        this.user_id = user_id;
        this.rights = rights;
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
