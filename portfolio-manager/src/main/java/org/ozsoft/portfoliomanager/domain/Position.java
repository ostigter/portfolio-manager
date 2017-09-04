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
import java.math.MathContext;

import org.ozsoft.portfoliomanager.util.MathUtils;

/**
 * Position in a single stock, as part of a stock portfolio.
 *
 * @author Oscar Stigter
 */
public class Position implements Comparable<Position> {

    private static final BigDecimal MIN_COST = new BigDecimal("0.01");

    private final Configuration config = Configuration.getInstance();

    private final Stock stock;

    private BigDecimal noOfShares = BigDecimal.ZERO;

    private BigDecimal currentCost = BigDecimal.ZERO;

    private BigDecimal totalCost = BigDecimal.ZERO;

    private BigDecimal totalIncome = BigDecimal.ZERO;

    private BigDecimal realizedResult = BigDecimal.ZERO;

    private BigDecimal totalReturn = BigDecimal.ZERO;

    /**
     * Constructor.
     *
     * @param stock
     *            The stock.
     */
    public Position(Stock stock) {
        this.stock = stock;
    }

    /**
     * Returns the stock.
     *
     * @return The stock.
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Returns the currently owned number of shares.
     *
     * @return The number of shares.
     */
    public BigDecimal getNoOfShares() {
        return noOfShares;
    }

    /**
     * Sets the currently owned number of shares.
     *
     * @param noOfShares
     *            The number of shares.
     */
    public void setNoOfShares(BigDecimal noOfShares) {
        this.noOfShares = noOfShares;
    }

    /**
     * Returns the current costbase (open position only, otherwise 0).
     *
     * @return The current costbase.
     */
    public BigDecimal getCurrentCost() {
        return currentCost;
    }

    /**
     * Returns the current market value (open position only, otherwise 0).
     *
     * @return The current market value.
     */
    public BigDecimal getCurrentValue() {
        return noOfShares.multiply(stock.getPrice(), MathContext.DECIMAL64);
    }

    /**
     * Returns th current result (market value minus costbase; open position only, otherwise 0)
     *
     * @return The current result.
     */
    public BigDecimal getCurrentResult() {
        return getCurrentValue().subtract(getCurrentCost());
    }

    /**
     * Returns the current result as percentage of the costbase (open position only, otherwise 0).
     *
     * @return
     */
    public BigDecimal getCurrentResultPercentage() {
        BigDecimal currentInvestment = getCurrentCost();
        if (currentInvestment.signum() > 0) {
            return MathUtils.perc(getCurrentResult(), currentInvestment);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Returns the total costbase.
     *
     * @return The total costbase.
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getCostPerShare() {
        BigDecimal currentInvestment = getCurrentCost();
        if (currentInvestment.signum() > 0) {
            return MathUtils.divide(currentInvestment, noOfShares);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getAnnualIncome() {
        BigDecimal annualIncome = noOfShares.multiply(stock.getDivRate(), MathContext.DECIMAL64);
        if (config.isDeductIncomeTax()) {
            annualIncome = annualIncome.multiply(BigDecimal.ONE.subtract(Configuration.getIncomeTaxRate()), MathContext.DECIMAL64);
        }
        return annualIncome;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getYieldOnCost() {
        if (currentCost.signum() > 0) {
            return MathUtils.perc(getAnnualIncome(), currentCost);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getRealizedResult() {
        return realizedResult;
    }

    public BigDecimal getTotalReturn() {
        return getCurrentResult().add(totalReturn);
    }

    public BigDecimal getTotalReturnPercentage() {
        if (totalCost.signum() > 0) {
            // FIXME: Total return based on average costbase instead of total costbase.
            return MathUtils.perc(getTotalReturn(), totalCost);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Adds a transaction.
     *
     * @param tx
     *            The transacton.
     */
    public void addTransaction(Transaction tx) {
        switch (tx.getType()) {
            case BUY:
                noOfShares = noOfShares.add(tx.getNoOfShares());
                BigDecimal cost = tx.getNoOfShares().multiply(tx.getPrice()).add(tx.getCost());
                currentCost = currentCost.add(cost);
                totalCost = totalCost.add(cost);
                break;
            case SELL:
                if (tx.getNoOfShares().compareTo(noOfShares) > 0) {
                    throw new IllegalArgumentException("Cannot sell more shares than owned");
                }
                BigDecimal avgPrice = MathUtils.divide(currentCost, noOfShares);
                BigDecimal value = tx.getNoOfShares().multiply(avgPrice, MathContext.DECIMAL64);
                currentCost = currentCost.subtract(value);
                if (currentCost.compareTo(MIN_COST) < 0) {
                    // Round very low cost down to 0 to avoid rounding errors.
                    currentCost = BigDecimal.ZERO;
                }
                totalCost = totalCost.add(tx.getCost());
                BigDecimal profit = tx.getNoOfShares().multiply(tx.getPrice().subtract(avgPrice)).subtract(tx.getCost());
                realizedResult = realizedResult.add(profit);
                totalReturn = totalReturn.add(profit);
                noOfShares = noOfShares.subtract(tx.getNoOfShares());
                break;
            case DIVIDEND:
                BigDecimal income = tx.getNoOfShares().multiply(tx.getPrice());
                if (config.isDeductIncomeTax()) {
                    income = income.multiply(BigDecimal.ONE.subtract(Configuration.getIncomeTaxRate()));
                }
                totalIncome = totalIncome.add(income);
                totalReturn = totalReturn.add(income);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }
    }

    @Override
    public int compareTo(Position other) {
        return stock.compareTo(other.getStock());
    }

    @Override
    public String toString() {
        return stock.toString();
    }
}
