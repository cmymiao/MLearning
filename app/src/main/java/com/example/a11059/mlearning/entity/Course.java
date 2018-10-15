package com.example.a11059.mlearning.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 11059 on 2018/7/16.
 */

public class Course extends BmobObject {
    private Integer id;
    private String name;
    private BmobFile program;
    private BmobFile experiment;
    private BmobFile time;

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

    public BmobFile getProgram() {
        return program;
    }

    public void setProgram(BmobFile program) {
        this.program = program;
    }

    public BmobFile getExperiment() {
        return experiment;
    }

    public void setExperiment(BmobFile experiment) {
        this.experiment = experiment;
    }

    public BmobFile getTime() {
        return time;
    }

    public void setTime(BmobFile time) {
        this.time = time;
    }
}
