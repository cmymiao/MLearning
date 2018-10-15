package com.example.a11059.mlearning.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by 11059 on 2018/7/16.
 */

public class Examination extends BmobObject {
    private Integer id;
    private String name;
    private String questionList;
    private Double difficulty;

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

    public String getQuestionList() {
        return questionList;
    }

    public void setQuestionList(String questionList) {
        this.questionList = questionList;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }
}
