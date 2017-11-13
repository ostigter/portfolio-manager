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
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Portfolio;
import org.ozsoft.portfoliomanager.domain.Results;
import org.ozsoft.portfoliomanager.domain.Transaction;
import org.ozsoft.portfoliomanager.util.MathUtils;

/**
 * Modal window to view portfolio statistics.
 *
 * @author Oscar Stigter
 */
public class StatisticsFrame extends JDialog {

    private static final long serialVersionUID = 6179505664369253923L;

    private static final String[] MONTHS = { null, "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
            "November", "December" };

    private static final int PERIOD_WIDTH = 24;

    private final JTextArea textArea;

    /**
     * Constructor.
     *
     * @param mainFrame
     *            The application's main window.
     */
    public StatisticsFrame(MainFrame mainFrame) {
        super(mainFrame, "Portfolio statistics", true);

        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (0.6 * screenSize.getWidth());
        int height = (int) (0.85 * screenSize.getHeight());
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(mainFrame);

        showStatistics();

        setVisible(true);
    }

    /**
     * Shows portfolio statistics (costbase, income, total return) over time.
     */
    private void showStatistics() {
        Configuration config = Configuration.getInstance();
        List<Transaction> transactions = new ArrayList<Transaction>(config.getTransactions());
        Calendar firstDay = getDay(transactions.get(0).getDate());
        Calendar lastDay = getDay(new Date().getTime());

        int month = 1 + firstDay.get(Calendar.MONTH);
        int quarter = (int) Math.ceil(month / 3.0);
        int year = firstDay.get(Calendar.YEAR);

        Results monthlyResult = new Results();
        Results quarterlyResult = new Results();
        Results annualResult = new Results();
        Results totalResult = new Results();

        int daysInMonth = 1;
        int totalDays = 1;
        BigDecimal currentCost = BigDecimal.ZERO;
        Map<Integer, BigDecimal> costPerDay = new TreeMap<Integer, BigDecimal>();
        Map<Integer, BigDecimal> overallCosts = new TreeMap<Integer, BigDecimal>();
        Map<String, BigDecimal> costPerStock = new HashMap<String, BigDecimal>();
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
                        BigDecimal income = tx.getNoOfShares().multiply(tx.getPrice()).subtract(tx.getCost());
                        monthlyResult.addIncome(income);
                        quarterlyResult.addIncome(income);
                        annualResult.addIncome(income);
                        totalResult.addIncome(income);
                        break;
                    case BUY:
                        BigDecimal costs = tx.getNoOfShares().multiply(tx.getPrice()).add(tx.getCost());
                        BigDecimal cps = costPerStock.get(symbol);
                        if (cps == null) {
                            costPerStock.put(symbol, costs);
                        } else {
                            costPerStock.put(symbol, cps.add(costs));
                        }
                        Integer sps = sharesPerStock.get(symbol);
                        if (sps == null) {
                            sharesPerStock.put(symbol, tx.getNoOfShares().intValue());
                        } else {
                            sharesPerStock.put(symbol, sps + tx.getNoOfShares().intValue());
                        }
                        BigDecimal dayCosts = costPerDay.get(daysInMonth);
                        if (dayCosts == null) {
                            costPerDay.put(daysInMonth, costs);
                        } else {
                            costPerDay.put(daysInMonth, dayCosts.add(costs));
                        }
                        BigDecimal dayCosts2 = overallCosts.get(totalDays);
                        if (dayCosts2 == null) {
                            overallCosts.put(totalDays, costs);
                        } else {
                            overallCosts.put(totalDays, dayCosts2.add(costs));
                        }
                        currentCost = currentCost.add(costs);
                        monthlyResult.addCosts(costs);
                        quarterlyResult.addCosts(costs);
                        annualResult.addCosts(costs);
                        totalResult.addCosts(costs);
                        break;
                    case SELL:
                        cps = costPerStock.get(symbol);
                        sps = sharesPerStock.get(symbol);
                        BigDecimal avgPrice;
                        if (cps != null && sps != null && sps > 0) {
                            avgPrice = MathUtils.divide(cps, new BigDecimal(sps));
                        } else {
                            throw new IllegalStateException(String.format("Invalid SELL transaction for stock '%s': non-existing position", symbol));
                        }
                        costs = tx.getNoOfShares().multiply(avgPrice);
                        dayCosts = costPerDay.get(daysInMonth);
                        if (dayCosts == null) {
                            costPerDay.put(daysInMonth, MathUtils.negate(costs));
                        } else {
                            costPerDay.put(daysInMonth, dayCosts.subtract(costs));
                        }
                        dayCosts2 = overallCosts.get(totalDays);
                        if (dayCosts2 == null) {
                            overallCosts.put(totalDays, MathUtils.negate(costs));
                        } else {
                            overallCosts.put(totalDays, dayCosts2.subtract(costs));
                        }
                        costPerStock.put(symbol, cps.subtract(costs));
                        sharesPerStock.put(symbol, sps - tx.getNoOfShares().intValue());
                        currentCost = currentCost.subtract(costs).subtract(tx.getCost());
                        break;
                }
            } else {
                totalDays++;
                day.add(Calendar.DAY_OF_YEAR, 1);
                if (1 + day.get(Calendar.MONTH) != month) {
                    BigDecimal sum = BigDecimal.ZERO;
                    for (Integer dayNr : costPerDay.keySet()) {
                        sum = sum.add(costPerDay.get(dayNr));
                    }
                    BigDecimal avgCost = MathUtils.divide(sum, new BigDecimal(daysInMonth));
                    textArea.append(String.format("%sAverage Costbase: $%,.0f, Income: $%,.0f\n", formatPeriod(month, year), avgCost,
                            monthlyResult.getIncome()));
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
                    textArea.append(String.format("\nQuarter %d, %d:\tAverage Costbase: $%,.0f, Income: $%,.0f\n\n", quarter, year, currentCost,
                            quarterlyResult.getIncome()));
                    quarterlyResult.clear();
                    quarter = (int) Math.ceil(month / 3.0);
                }
                if (day.get(Calendar.YEAR) > year) {
                    textArea.append(
                            String.format("%d:\t\t\tAverage Costbase: $%,.0f, Income: $%,.0f\n\n", year, currentCost, annualResult.getIncome()));
                    annualResult.clear();
                    year++;
                }
            }
        }

        textArea.append(
                String.format("%sAverage Costbase: $%,.0f, Income: $%,.0f\n", formatPeriod(month, year), currentCost, monthlyResult.getIncome()));
        textArea.append(String.format("\nQuarter %d, %d:\tAverage Costbase: $%,.0f, Income: $%,.0f\n", quarter, year, currentCost,
                quarterlyResult.getIncome()));
        textArea.append(String.format("\n%d:\t\t\tAverage Costbase: $%,.0f, Income: $%,.0f\n", year, currentCost, annualResult.getIncome()));

        BigDecimal sum = BigDecimal.ZERO;
        for (Integer dayNr : overallCosts.keySet()) {
            // textArea.append(String.format("Day %04d:\t\tCostbase: $%,.0f\n", dayNr, overallCosts.get(dayNr)));
            sum = sum.add(overallCosts.get(dayNr));
        }
        double years = totalDays / 365.0;
        if (years < 1.0) {
            years = 1.0; // to not extrapolate CAGR for less than a year
        }
        BigDecimal avgCost = MathUtils.divide(sum, new BigDecimal(totalDays));
        Portfolio portfolio = config.getPortfolio();
        BigDecimal totalReturn = portfolio.getTotalReturn();
        double totalReturnCAGR = (Math.pow(MathUtils.divide(totalReturn, avgCost).add(BigDecimal.ONE).doubleValue(), 1.0 / years) - 1.0) * 100.0;
        textArea.append(String.format("\nOverall:\t\tAverage Costbase: $%,.0f, Income: $%,.0f, Total Return: $%,.0f (%.2f %% CAGR)\n", avgCost,
                portfolio.getTotalIncome(), totalReturn, totalReturnCAGR));
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

    private static String formatPeriod(int month, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s, %d:", MONTHS[month], year));
        while (sb.length() < PERIOD_WIDTH) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
