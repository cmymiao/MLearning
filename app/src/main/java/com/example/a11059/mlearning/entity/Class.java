package com.example.a11059.mlearning.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by 11059 on 2018/7/16.
 */

public class Class extends BmobObject {
    private String id;
    private String name;
    private String schedule;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
