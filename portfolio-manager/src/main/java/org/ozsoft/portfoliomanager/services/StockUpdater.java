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

package org.ozsoft.portfoliomanager.services;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Exchange;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Thread that updates the share price of a single stock. <br />
 * <br />
 * Gets the latest (delayed) share price from the Yahoo! Finance API and Morningstar's value rating.
 * 
 * @author Oscar Stigter
 */
public class StockUpdater extends Thread {

    private static final String YAHOO_STOCK_QUOTE_URI = "http://download.finance.yahoo.com/d/quotes.csv?s=%s&f=l1p2rd";

    private static final String MORNINGSTAR_QUOTE_URI = "http://www.morningstar.com/stocks/%s/%s/quote.html";

    private static final Pattern MORNINGSTAR_QUOTE_PATTERN = Pattern.compile("\"starRating\":([0-9])");

    private static final Logger LOGGER = LogManager.getLogger(StockUpdater.class);

    private final Stock stock;

    private final HttpPageReader httpPageReader;

    private boolean isFinished = false;

    private boolean isUpdated = false;

    /**
     * Constructor.
     * 
     * @param stock
     *            The stock.
     * @param httpPageReader
     *            The {@link HttpPageReader} (possibly shared).
     */
    public StockUpdater(Stock stock, HttpPageReader httpPageReader) {
        this.stock = stock;
        this.httpPageReader = httpPageReader;
    }

    /**
     * Returns wheher the update operation has finished.
     * 
     * @return {@code true} if finished, otherwise {@code false}.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Returns whether the stock was updated (based on it's current price).
     * 
     * @return {@code true} if updated, otherwise {@code false}.
     */
    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public void run() {
        try {
            // Get stock quote from Yahoo Finance API
            // LOGGER.debug("Requesting stock quote for " + stock);
            String line = httpPageReader.read(String.format(YAHOO_STOCK_QUOTE_URI, stock.getSymbol())).trim();
            // LOGGER.debug(String.format("CSV line for '%s': '%s'", stock, line));
            String[] fields = line.split(",");
            if (fields.length == 4) {
                try {
                    double price = Double.parseDouble(fields[0]);
                    if (price != stock.getPrice()) {
                        stock.setPrice(price);
                        stock.setChangePerc(fields[2].equals("N/A") ? 0.0 : Double.parseDouble(fields[1].replaceAll("[\"%]", "")));
                        stock.setPeRatio(fields[2].equals("N/A") ? -1.0 : Double.parseDouble(fields[2]));
                        stock.setDivRate(fields[3].equals("N/A") ? 0.0 : Double.parseDouble(fields[3]));
                        isUpdated = true;
                        // LOGGER.debug(String.format("Updated stock price of '%s'", stock));
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Could not parse stock quote for %s: '%s'", stock, line), e);
                }
            } else {
                LOGGER.error(String.format("Invalid number of CSV fields returned for %s: '%s'", stock, line));
            }

            // Get Morningstar value rating (if rated).
            int starRating = -1;
            String exchangeId = (stock.getExchange() == Exchange.NYSE) ? "xnys" : "xnas";
            String content = httpPageReader.read(String.format(MORNINGSTAR_QUOTE_URI, exchangeId, stock.getSymbol()));
            Matcher m = MORNINGSTAR_QUOTE_PATTERN.matcher(content);
            if (m.find()) {
                starRating = Integer.parseInt(m.group(1));
            }
            stock.setStarRating(starRating);

        } catch (IOException e) {
            LOGGER.error("Failed to retrieve full stock quote for " + stock, e);
        }

        isFinished = true;
    }
}
