package com.example.a11059.mlearning.entity;

import org.litepal.crud.DataSupport;

public class SequenceGroupL extends DataSupport {

    private String username;

    private int groupNo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }
}
