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
import java.util.Collection;
import java.util.Collections;
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
}
