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
import org.ozsoft.portfoliomanager.services.downloader.MarketWatchQuoteDownloader;
import org.ozsoft.portfoliomanager.services.downloader.QuoteDownloader;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Thread that updates a single stock. <br />
 * <br />
 * 
 * Gets the current stock quote from the MarketWatch and Morningstar's value rating.
 * 
 * @author Oscar Stigter
 */
public class StockUpdater extends Thread {

    private static final String MORNINGSTAR_QUOTE_URI = "http://www.morningstar.com/stocks/%s/%s/quote.html";

    private static final Pattern MORNINGSTAR_QUOTE_PATTERN = Pattern.compile("\"starRating\":([0-9])");

    private static final Logger LOGGER = LogManager.getLogger(StockUpdater.class);

    private final Stock stock;

    private final HttpPageReader httpPageReader;

    private final QuoteDownloader downloader;

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

        // TODO: Automatic failover to other quote downloaders.
        downloader = new MarketWatchQuoteDownloader(httpPageReader);
        // downloader = new YahooFinanceQuoteDownloader(httpPageReader);
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
        // Get stock quote.
        isUpdated = downloader.updateStock(stock);

        // Get Morningstar value rating (if rated).
        try {
            int starRating = -1;
            String exchangeId = (stock.getExchange() == Exchange.NYSE) ? "xnys" : "xnas";
            String content = httpPageReader.read(String.format(MORNINGSTAR_QUOTE_URI, exchangeId, stock.getSymbol()));
            Matcher m = MORNINGSTAR_QUOTE_PATTERN.matcher(content);
            if (m.find()) {
                starRating = Integer.parseInt(m.group(1));
            }
            stock.setStarRating(starRating);
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to retrieve Morningstar value rating for %s: %s", stock, e.getMessage()));
        }

        isFinished = true;
    }
}
