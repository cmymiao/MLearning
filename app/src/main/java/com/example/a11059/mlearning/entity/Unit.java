package com.example.a11059.mlearning.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by 11059 on 2018/7/16.
 */

public class Unit extends BmobObject {
    private Integer id;
    private String name;
    private Integer courseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}
