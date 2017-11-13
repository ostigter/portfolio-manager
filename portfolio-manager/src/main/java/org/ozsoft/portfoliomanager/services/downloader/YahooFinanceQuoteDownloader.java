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

package org.ozsoft.portfoliomanager.services.downloader;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Stock quote downloader using Yahoo Finance's API (CSV, delayed). <br />
 * <br />
 *
 * Updates the stock's last price, price change percentage and P/E ratio.
 *
 * @author Oscar Stigter
 */
public class YahooFinanceQuoteDownloader extends QuoteDownloader {

    private static final String URI = "https://download.finance.yahoo.com/d/quotes.csv?s=%s&f=l1p2r";

    private static final Logger LOGGER = LogManager.getLogger(YahooFinanceQuoteDownloader.class);

    /**
     * Constructor.
     *
     * @param httpPageReader
     *            The HTTP page reader.
     */
    public YahooFinanceQuoteDownloader(HttpPageReader httpPageReader) {
        super(httpPageReader);
    }

    @Override
    public boolean updateStock(Stock stock) {
        boolean isUpdated = false;

        try {
            // LOGGER.debug("Requesting stock quote for " + stock);
            String line = httpPageReader.read(String.format(URI, stock.getSymbol())).trim();
            // LOGGER.debug(String.format("CSV line for '%s': '%s'", stock, line));
            String[] fields = line.split(",");
            if (fields.length == 3) {
                try {
                    BigDecimal price = new BigDecimal(fields[0]);
                    if (!price.equals(stock.getPrice())) {
                        stock.setPrice(price);
                        // stock.setChangePerc(fields[2].equals("N/A") ? 0.0 : Double.parseDouble(fields[1].replaceAll("[\"%]", "")));
                        stock.setPeRatio(fields[2].equals("N/A") ? -1.0 : Double.parseDouble(fields[2]));
                        isUpdated = true;
                        LOGGER.debug("Updated stock price of " + stock);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Could not parse stock quote for %s: '%s'", stock, line), e);
                }
            } else {
                LOGGER.error(String.format("Invalid number of CSV fields returned for %s: '%s'", stock, line));
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to retrieve stock quote for %s: %s", stock, e.getMessage()));
        }

        return isUpdated;
    }
}
