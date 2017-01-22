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

package org.ozsoft.portfoliomanager.domain;

import java.math.BigDecimal;

/**
 * Stock transaction.
 *
 * @author Oscar Stigter
 */
public class Transaction implements Comparable<Transaction> {

    private int id;

    private long date;

    private String symbol;

    private TransactionType type;

    private BigDecimal noOfShares;

    private BigDecimal price;

    private BigDecimal cost;

    /**
     * Returns the transaction ID.
     *
     * @return The transaction ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the transaction ID.
     *
     * @param id
     *            The transaction ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the transaction date.
     *
     * @return The transaction date as timestamp in milliseconds.
     */
    public long getDate() {
        return date;
    }

    /**
     * Sets the transaction date.
     *
     * @param date
     *            The transaction date as timestamp in milliseconds.
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Returns the stock's ticker symbol.
     *
     * @return The stock's ticker symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the stock's ticker symbol.
     *
     * @param symbol
     *            The stock's ticker symbol.
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the transaction type.
     *
     * @return The transaction type.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the transaction type.
     *
     * @param type
     *            The transaction type.
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Returns the number of shares.
     *
     * @return The number of shares.
     */
    public BigDecimal getNoOfShares() {
        return noOfShares;
    }

    /**
     * Sets the number of shares.
     *
     * @param noOfShares
     *            The number of shares.
     */
    public void setNoOfShares(BigDecimal noOfShares) {
        this.noOfShares = noOfShares;
    }

    /**
     * Returns the price per share.
     *
     * @return The price per share.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price per share.
     *
     * @param price
     *            The price per share.
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the transaction costs (e.g. brokerage fees; if any).
     *
     * @return The transaction costs.
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Sets the transaction costs (e.g. brokerage fees; if any).
     *
     * @param cost
     *            The transaction costs.
     */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(date).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Transaction) {
            Transaction tx = (Transaction) other;
            return symbol.equals(tx.getSymbol()) && date == tx.getDate();
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Transaction other) {
        if (date < other.getDate()) {
            return -1;
        } else if (date > other.getDate()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s %d $%.2f $%.2f", id, date, symbol, type, noOfShares, price, cost);
    }
}
