package com.example.a11059.mlearning.entity;

import org.litepal.crud.DataSupport;


public class HistoryL extends DataSupport{

    private String username;

    private String questionId;

    private String option;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestonId(String questonId) {
        this.questionId = questonId;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
