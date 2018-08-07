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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.ozsoft.portfoliomanager.util.MathUtils;

/**
 * Stock performance during a specific time range.
 *
 * @author Oscar Stigter
 */
public class StockPerformance {

    private static final double MILLISECONDS_PER_YEAR = 365.0 * 24.0 * 60.0 * 60.0 * 1000.0;

    private BigDecimal startPrice;

    private BigDecimal endPrice;

    private BigDecimal lowPrice;

    private BigDecimal highPrice;

    private BigDecimal change;

    private BigDecimal changePerc;

    private BigDecimal volatility;

    private BigDecimal totalDividends;

    private double years;

    public StockPerformance(List<Quote> allPrices, List<Quote> dividends, TimeRange dateFilter) {
        // Find closing prices during specified period.
        List<Quote> prices = new ArrayList<Quote>();
        Date fromDate = dateFilter.getFromDate();
        for (Quote price : allPrices) {
            if (price.getDate().after(fromDate)) {
                prices.add(price);
            }
        }
        Collections.sort(prices);

        // Calculate total amount of received dividend payments during period.
        totalDividends = BigDecimal.ZERO;
        for (Quote payment : dividends) {
            if (payment.getDate().after(fromDate)) {
                totalDividends = totalDividends.add(payment.getPrice());
            }
        }

        // Calculate price statisics.
        int count = prices.size();
        startPrice = prices.get(0).getPrice();
        endPrice = prices.get(count - 1).getPrice();
        lowPrice = new BigDecimal(99999);
        highPrice = BigDecimal.ZERO;
        change = endPrice.subtract(startPrice);
        changePerc = MathUtils.perc(change, startPrice);
        volatility = BigDecimal.ZERO;
        BigDecimal slope = MathUtils.divide(change, new BigDecimal(count));
        for (int i = 0; i < count; i++) {
            BigDecimal p = prices.get(i).getPrice();
            if (p.compareTo(lowPrice) < 0) {
                lowPrice = p;
            }
            if (p.compareTo(highPrice) > 0) {
                highPrice = p;
            }
            BigDecimal avg = startPrice.add(new BigDecimal(i).multiply(slope));
            volatility = volatility.add(MathUtils.divide(MathUtils.abs(p, avg), p).multiply(MathUtils.HUNDRED));
        }
        volatility = MathUtils.divide(volatility, new BigDecimal(count));

        // Determine actual duration based on the stock's history.
        Date firstDate = prices.get(0).getDate();
        Date lastDate = prices.get(count - 1).getDate();
        years = (lastDate.getTime() - firstDate.getTime()) / MILLISECONDS_PER_YEAR;
    }

    public double getStartPrice() {
        return startPrice.doubleValue();
    }

    public double getEndPrice() {
        return endPrice.doubleValue();
    }

    public double getLowPrice() {
        return lowPrice.doubleValue();
    }

    public double getHighPrice() {
        return highPrice.doubleValue();
    }

    public double getChange() {
        return change.doubleValue();
    }

    public double getChangePerc() {
        return changePerc.doubleValue();
    }

    public double getVolatility() {
        return volatility.doubleValue();
    }

    public double getCagr() {
        if (years < 1.0) {
            return MathUtils.divide(endPrice.add(totalDividends), startPrice).doubleValue();
        } else {
            return (Math.pow(MathUtils.divide(endPrice.add(totalDividends), startPrice).doubleValue(), 1.0 / years) - 1.0) * 100.0;
        }
    }

    public double getDiscount() {
        BigDecimal discount = MathUtils.perc(highPrice.subtract(endPrice, MathContext.DECIMAL64),
                highPrice.subtract(lowPrice, MathContext.DECIMAL64));
        if (discount.signum() < 0) {
            return 0.0;
        } else {
            return discount.doubleValue();
        }
    }
}
