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

/**
 * Proprietary analysis results of a specific stock.
 * 
 * @author Oscar Stigter
 */
public class StockAnalysis implements Comparable<StockAnalysis> {

    private final Stock stock;

    private final double cagr10yr;

    private final double cagr5yr;

    private final double change1yr;

    private final double volatility;

    private final double high52wk;

    private final double low52wk;

    private final double currentPrice;

    private final double discount5yr;

    private final double discount1yr;

    private final double score;

    public StockAnalysis(Stock stock, double cagr10yr, double cagr5yr, double change1yr, double volatility, double high52wk, double low52wk,
            double currentPrice, double discount5yr, double discount1yr, double score) {
        this.stock = stock;
        this.cagr10yr = cagr10yr;
        this.cagr5yr = cagr5yr;
        this.change1yr = change1yr;
        this.volatility = volatility;
        this.high52wk = high52wk;
        this.low52wk = low52wk;
        this.currentPrice = currentPrice;
        this.discount5yr = discount5yr;
        this.discount1yr = discount1yr;
        this.score = score;
    }

    public Stock getStock() {
        return stock;
    }

    public double getCagr10yr() {
        return cagr10yr;
    }

    public double getCagr5yr() {
        return cagr5yr;
    }

    public double getChange1yr() {
        return change1yr;
    }

    public double getVolatility() {
        return volatility;
    }

    public double getHigh52wk() {
        return high52wk;
    }

    public double getLow52wk() {
        return low52wk;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getDiscount5yr() {
        return discount5yr;
    }

    public double getDiscount1yr() {
        return discount1yr;
    }

    public Double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("%-5s;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f;%.2f", stock.getSymbol(), cagr10yr, cagr5yr, change1yr, volatility,
                high52wk, low52wk, currentPrice, discount5yr, discount1yr, score);
    }

    @Override
    public int compareTo(StockAnalysis other) {
        return other.getScore().compareTo(score);
    }
}
