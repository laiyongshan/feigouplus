package com.example.youhe.youhecheguanjiaplus.logic;

import java.io.Serializable;
import java.util.HashMap;

public class Task implements Serializable {

    private static final long serialVersionUID = -2237924610020436869L;

    private int taskId;
    private HashMap taskParam;

    public Task(int taskId, HashMap params) {
        this.taskId = taskId;
        this.taskParam = params;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public HashMap getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(HashMap taskParam) {
        this.taskParam = taskParam;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }


}
