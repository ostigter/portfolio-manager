package org.ozsoft.portfoliomanager.domain;

import java.util.Calendar;

public class Results {

    private int noOfDays;

    private double costs;

    private double income;

    private Calendar lastDay;

    public Results() {
        lastDay = Calendar.getInstance();
        lastDay.set(Calendar.YEAR, -4000); // 4000 BC (prehistoric)
        clear();
    }

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setDay(Calendar day) {
        if (day.after(lastDay)) {
            noOfDays++;
            lastDay.setTime(day.getTime());
        }
    }

    public double getCosts() {
        return costs;
    }

    public double getAverageCosts() {
        if (noOfDays > 0) {
            return costs / noOfDays;
        } else {
            return 0.0;
        }
    }

    public void addCosts(double cost) {
        costs += cost;
    }

    public double getIncome() {
        return income;
    }

    public void addIncome(double income) {
        this.income += income;
    }

    public void clear() {
        noOfDays = 0;
        // costs = 0.0;
        income = 0.0;
    }
}
