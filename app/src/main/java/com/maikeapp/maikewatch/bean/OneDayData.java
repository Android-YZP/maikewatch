package com.maikeapp.maikewatch.bean;

/**
 * Created by JLJ on 2016/5/11.
 */
public class OneDayData {
    private String LoginName;
    private String MacAddress;

    private String SportsTime;//日期
    private int CompleteHour;//时间
    private int Steps;//步数
    private int CompletedSteps = 0;//总步数0
    private String type;//类型

    public OneDayData() {
    }

    public OneDayData(String date,int time, int step, String type) {
        this.SportsTime = date;
        this.CompleteHour = time;
        this.Steps = step;
        this.type = type;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String getSportsTime() {
        return SportsTime;
    }

    public void setSportsTime(String sportsTime) {
        SportsTime = sportsTime;
    }

    public int getCompleteHour() {
        return CompleteHour;
    }

    public void setCompleteHour(int completeHour) {
        CompleteHour = completeHour;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }

    public int getCompletedSteps() {
        return CompletedSteps;
    }

    public void setCompletedSteps(int completedSteps) {
        CompletedSteps = completedSteps;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
