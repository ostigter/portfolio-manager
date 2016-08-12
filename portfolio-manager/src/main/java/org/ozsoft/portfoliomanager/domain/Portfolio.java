// This file is part of the 'portfolio-manager' (Portfolio Manager)
// project, an open source stock portfolio manager application
// written in Java.
//
// Copyright 2015 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stock portfolio, with transactions and (open/closed) positions.
 * 
 * @author Oscar Stigter
 */
public class Portfolio {

    private final List<Transaction> transactions;

    private final Map<Stock, Position> positions;

    private double currentCost;

    private double currentValue;

    private double totalCost;

    private double annualIncome;

    private double totalIncome;

    private double realizedResult;

    private double totalReturn;

    /**
     * Constructor.
     */
    public Portfolio() {
        transactions = new ArrayList<Transaction>();
        positions = new TreeMap<Stock, Position>();
    }

    /**
     * Returns all transactions.
     * 
     * @return The transactions.
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Adds a transaction.
     * 
     * @param transaction
     *            The transaction.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Returns all positions of currently or previously owned stocks.
     * 
     * @return The positions.
     */
    public Collection<Position> getPositions() {
        return positions.values();
    }

    /**
     * Returns the position in a specific stock.
     * 
     * @param stock
     *            The stock.
     * 
     * @return The position if any, otherwise {@code null}.
     */
    public Position getPosition(Stock stock) {
        return positions.get(stock);
    }

    /**
     * Returns the current costbase (of all open positions).
     * 
     * @return The current costbase.
     */
    public double getCurrentCost() {
        return currentCost;
    }

    /**
     * Returns he current market value (of all open positions).
     * 
     * @return The current market value.
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * Returns the current result (market value minus costbase of all open positions).
     * 
     * @return
     */
    public double getCurrentResult() {
        return currentValue - currentCost;
    }

    /**
     * Returs the current result percentage (current result divided by current costbase).
     * 
     * @return
     */
    public double getCurrentResultPercentage() {
        if (currentCost > 0.0) {
            return (getCurrentResult() / currentCost) * 100.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the total costbase (open and closed positions).
     * 
     * @return The total costbase.
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Returns the current annual income (based on open positions).
     * 
     * @return The annual income.
     */
    public double getAnnualIncome() {
        return annualIncome;
    }

    /**
     * Returns the total received income (open and closed positions).
     * 
     * @return The total received income.
     */
    public double getTotalIncome() {
        return totalIncome;
    }

    /**
     * Returns the current yield-on-cost (annual income divided by current costbase).
     * 
     * @return
     */
    public double getYieldOnCost() {
        if (currentCost > 0.0) {
            return (annualIncome / currentCost) * 100.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the all-time realized result (profit/loss from stock sales).
     * 
     * @return The realized result.
     */
    public double getRealizedResult() {
        return realizedResult;
    }

    /**
     * Returns the all-time total return (open and closed positions). <br />
     * <br />
     * 
     * Total Return = Current Result + Realized Result + Total Income
     * 
     * @return The total return.
     */
    public double getTotalReturn() {
        return totalReturn;
    }

    /**
     * Returns the all-time total return percentage (total return divided by total costbase).
     * 
     * @return The total return percentage.
     */
    public double getTotalReturnPercentage() {
        // FIXME: Total return based on average costbase instead of total costbase.
        if (totalCost > 0.0) {
            return (getTotalReturn() / totalCost) * 100.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Updates the portfolio based on the specified configuration (stocks and positions).
     * 
     * @param config
     *            The configuration.
     */
    public void update(Configuration config) {
        clear();

        // Update positions based on transactions.
        for (Transaction transaction : transactions) {
            String symbol = transaction.getSymbol();
            Stock stock = config.getStock(symbol);
            if (stock != null) {
                Position position = positions.get(stock);
                if (position == null) {
                    position = new Position(stock);
                    positions.put(stock, position);
                }
                position.addTransaction(transaction);
            }
        }

        // Update totals based on positions.
        for (Position pos : positions.values()) {
            currentCost += pos.getCurrentCost();
            currentValue += pos.getCurrentValue();
            totalCost += pos.getTotalCost();
            annualIncome += pos.getAnnualIncome();
            totalIncome += pos.getTotalIncome();
            realizedResult += pos.getRealizedResult();
            totalReturn += pos.getTotalReturn();
        }
    }

    /**
     * Prints portfolio statistics to the console (development-only).
     */
    public void printResults() {
        List<Transaction> transactions = new ArrayList<Transaction>(this.transactions);
        Calendar firstDay = getDay(transactions.get(0).getDate());
        Calendar lastDay = getDay(new Date().getTime());
        // DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        int month = 1 + firstDay.get(Calendar.MONTH);
        int quarter = (int) Math.ceil(month / 3.0);
        int year = firstDay.get(Calendar.YEAR);

        Results monthlyResult = new Results();
        Results quarterlyResult = new Results();
        Results annualResult = new Results();
        Results totalResult = new Results();

        int daysInMonth = 1;
        int totalDays = 1;
        double currentCost = 0.0;
        Map<Integer, Double> costPerDay = new TreeMap<Integer, Double>();
        Map<Integer, Double> overallCosts = new TreeMap<Integer, Double>();
        Map<String, Double> costPerStock = new HashMap<String, Double>();
        Map<String, Integer> sharesPerStock = new HashMap<String, Integer>();

        Calendar day = firstDay;
        while (!day.after(lastDay)) {
            monthlyResult.setDay(day);
            quarterlyResult.setDay(day);
            annualResult.setDay(day);
            totalResult.setDay(day);

            Transaction tx = getTransactionOnDay(transactions, day);
            if (tx != null) {
                String symbol = tx.getSymbol();
                switch (tx.getType()) {
                    case DIVIDEND:
                        double income = tx.getNoOfShares() * tx.getPrice();
                        income -= tx.getCost();
                        monthlyResult.addIncome(income);
                        quarterlyResult.addIncome(income);
                        annualResult.addIncome(income);
                        totalResult.addIncome(income);
                        break;
                    case BUY:
                        double costs = tx.getNoOfShares() * tx.getPrice() + tx.getCost();
                        Double cps = costPerStock.get(symbol);
                        if (cps == null) {
                            costPerStock.put(symbol, costs);
                        } else {
                            costPerStock.put(symbol, cps + costs);
                        }
                        Integer sps = sharesPerStock.get(symbol);
                        if (sps == null) {
                            sharesPerStock.put(symbol, tx.getNoOfShares());
                        } else {
                            sharesPerStock.put(symbol, sps + tx.getNoOfShares());
                        }
                        Double dayCosts = costPerDay.get(daysInMonth);
                        if (dayCosts == null) {
                            costPerDay.put(daysInMonth, costs);
                        } else {
                            costPerDay.put(daysInMonth, dayCosts + costs);
                        }
                        Double dayCosts2 = overallCosts.get(totalDays);
                        if (dayCosts2 == null) {
                            overallCosts.put(totalDays, costs);
                        } else {
                            overallCosts.put(totalDays, dayCosts2 + costs);
                        }
                        currentCost += costs;
                        monthlyResult.addCosts(costs);
                        quarterlyResult.addCosts(costs);
                        annualResult.addCosts(costs);
                        totalResult.addCosts(costs);
                        break;
                    case SELL:
                        cps = costPerStock.get(symbol);
                        sps = sharesPerStock.get(symbol);
                        double avgPrice = 0.0;
                        if (cps != null && sps != null && sps > 0) {
                            avgPrice = cps / sps;
                        } else {
                            throw new IllegalStateException(String.format("Invalid SELL transaction for stock '%s': non-existing position", symbol));
                        }
                        costs = tx.getNoOfShares() * avgPrice;
                        dayCosts = costPerDay.get(daysInMonth);
                        if (dayCosts == null) {
                            costPerDay.put(daysInMonth, -costs);
                        } else {
                            costPerDay.put(daysInMonth, dayCosts - costs);
                        }
                        dayCosts2 = overallCosts.get(totalDays);
                        if (dayCosts2 == null) {
                            overallCosts.put(totalDays, -costs);
                        } else {
                            overallCosts.put(totalDays, dayCosts2 - costs);
                        }
                        costPerStock.put(symbol, cps - costs);
                        sharesPerStock.put(symbol, sps - tx.getNoOfShares());
                        currentCost -= costs + tx.getCost();
                        break;
                }
            } else {
                totalDays++;
                day.add(Calendar.DAY_OF_YEAR, 1);
                if (1 + day.get(Calendar.MONTH) != month) {
                    double sum = 0.0;
                    for (Integer dayNr : costPerDay.keySet()) {
                        // System.out.format("### %02d-%02d-%04d: Cost: $%,.0f\n", dayNr, month, year, costPerDay.get(dayNr));
                        sum += costPerDay.get(dayNr);
                    }
                    double avgCost = sum / daysInMonth;
                    System.out.format("%02d-%d: Costbase: $%,.0f, Income: $%,.0f\n", month, year, avgCost, monthlyResult.getIncome());
                    monthlyResult.clear();
                    month = 1 + day.get(Calendar.MONTH);
                    costPerDay.clear();
                    daysInMonth = 1;
                } else {
                    daysInMonth++;
                }
                costPerDay.put(daysInMonth, currentCost);
                overallCosts.put(totalDays, currentCost);
                if ((int) Math.ceil(month / 3.0) != quarter) {
                    System.out.format("%d-Q%d: Costbase: $%,.0f, Income: $%,.0f\n", year, quarter, currentCost, quarterlyResult.getIncome());
                    quarterlyResult.clear();
                    quarter = (int) Math.ceil(month / 3.0);
                }
                if (day.get(Calendar.YEAR) > year) {
                    System.out.format("%d: Costbase: $%,.0f, Income: $%,.0f\n", year, currentCost, annualResult.getIncome());
                    annualResult.clear();
                    year++;
                }
            }
        }

        System.out.format("%02d-%d: Costbase: $%,.0f, Income: $%,.0f\n", month, year, currentCost, monthlyResult.getIncome());
        System.out.format("%d-Q%d: Costbase: $%,.0f, Income: $%,.0f\n", year, quarter, currentCost, quarterlyResult.getIncome());
        System.out.format("%d: Costbase: $%,.0f, Income: $%,.0f\n", year, currentCost, annualResult.getIncome());

        double sum = 0.0;
        for (Integer dayNr : overallCosts.keySet()) {
            // System.out.format("### %04d: Costbase: $%,.0f\n", dayNr, overallCosts.get(dayNr));
            sum += overallCosts.get(dayNr);
        }
        double avgCost = sum / totalDays;
        double totalReturnPerc = totalReturn / avgCost * 100.0;
        System.out.format("Overall: Costbase: $%,.0f, Income: $%,.0f, Total Return: $%,.0f (%.2f %%)\n", avgCost, totalResult.getIncome(),
                totalReturn, totalReturnPerc);
    }

    /**
     * Clears the portfolio.
     */
    private void clear() {
        positions.clear();
        currentCost = 0.0;
        currentValue = 0.0;
        totalCost = 0.0;
        annualIncome = 0.0;
        totalIncome = 0.0;
        realizedResult = 0.0;
        totalReturn = 0.0;
    }

    /**
     * Returns the next transaction on a specific day, or {@code null} if not found.
     * 
     * @param transactions
     *            The transactions (must be sorted by date).
     * @param day
     *            The day.
     * 
     * @return The transaction if found, otherwise {@code null}.
     */
    private static Transaction getTransactionOnDay(List<Transaction> transactions, Calendar day) {
        if (!transactions.isEmpty()) {
            if (getDay(transactions.get(0).getDate()).equals(day)) {
                return transactions.remove(0);
            }
        }
        return null;
    }

    /**
     * Returns the {@code Calendar} object of a timestamp, rounded to midnight that day (00:00).
     * 
     * @param timestamp
     *            The date as a timestamp in milliseconds.
     * 
     * @return The {@code Calendar} object.
     */
    private static Calendar getDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timestamp));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
