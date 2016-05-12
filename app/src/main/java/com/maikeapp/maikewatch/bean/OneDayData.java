package com.maikeapp.maikewatch.bean;

/**
 * Created by JLJ on 2016/5/11.
 */
public class OneDayData {
    private String date;//日期
    private int time;//时间
    private int step;//步数
    private String type;//类型

    public OneDayData() {
    }

    public OneDayData(String date,int time, int step, String type) {
        this.date = date;
        this.time = time;
        this.step = step;
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
