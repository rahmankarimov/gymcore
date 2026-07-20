package com.gymcrm.workload.model;

public class MonthSummary {
    private int month;
    private int trainingsSummaryDuration;

    public MonthSummary() {
    }

    public MonthSummary(int month, int trainingsSummaryDuration) {
        this.month = month;
        this.trainingsSummaryDuration = trainingsSummaryDuration;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTrainingsSummaryDuration() {
        return trainingsSummaryDuration;
    }

    public void setTrainingsSummaryDuration(int trainingsSummaryDuration) {
        this.trainingsSummaryDuration = trainingsSummaryDuration;
    }
}
