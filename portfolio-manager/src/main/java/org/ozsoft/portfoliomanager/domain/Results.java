package org.ozsoft.portfoliomanager.domain;

import java.math.BigDecimal;
import java.util.Calendar;

import org.ozsoft.portfoliomanager.util.MathUtils;

public class Results {

    private int noOfDays;

    private BigDecimal costs;

    private BigDecimal income;

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

    public BigDecimal getCosts() {
        return costs;
    }

    public BigDecimal getAverageCosts() {
        if (noOfDays > 0) {
            return MathUtils.divide(costs, new BigDecimal(noOfDays));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void addCosts(BigDecimal cost) {
        this.costs = this.costs.add(cost);
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void addIncome(BigDecimal income) {
        this.income = this.income.add(income);
    }

    public void clear() {
        noOfDays = 0;
        costs = BigDecimal.ZERO;
        income = BigDecimal.ZERO;
    }
}
