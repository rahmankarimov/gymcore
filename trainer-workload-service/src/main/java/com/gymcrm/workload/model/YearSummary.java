package com.gymcrm.workload.model;

import java.util.ArrayList;
import java.util.List;

public class YearSummary {
    private int year;
    private List<MonthSummary> monthsList = new ArrayList<>();

    public YearSummary() {
    }

    public YearSummary(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<MonthSummary> getMonthsList() {
        return monthsList;
    }

    public void setMonthsList(List<MonthSummary> monthsList) {
        this.monthsList = monthsList;
    }
}
