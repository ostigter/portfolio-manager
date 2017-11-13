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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Stock quote downloader using the Google Finance API.<br />
 * <br />
 *
 * Updates the stock's last price, timestamp and price change percentage. <br />
 * <br />
 * 
 * Uses real-time intraday prices with an accuracy of 1 minute, or the last closing price outside market hours. <br />
 * <br />
 * 
 * <b>WARNING:</b> This API is not officially supported by Google, so it's reliability and durability cannot be guaranteed!
 * 
 * @author Oscar Stigter
 */
public class GoogleFinanceQuoteDownloader extends QuoteDownloader {

    private static final String INTRADAY_PRICES_URL = "http://finance.google.com/finance/getprices?q=%s&p=%dd&i=60&f=d,c";

    private static final Logger LOGGER = LogManager.getLogger(GoogleFinanceQuoteDownloader.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);

    /**
     * Constructor.
     * 
     * @param httpPageReader
     *            The HTTP page reader.
     */
    public GoogleFinanceQuoteDownloader(HttpPageReader httpPageReader) {
        super(httpPageReader);
    }

    @Override
    public boolean updateStock(Stock stock) {
        boolean isUpdated = false;

        try {
            String content = httpPageReader.read(String.format(INTRADAY_PRICES_URL, stock.getSymbol(), 1));

            long startTime = 0L;
            Date date = null;
            BigDecimal price = null;

            for (String line : content.split("\\n|\\r\\n|\\r")) {
                // System.out.println(line);
                if (line.charAt(0) == 'a') {
                    String[] fields = line.split(",");
                    startTime = Long.parseLong(fields[0].substring(1)) * 1000L;
                    date = new Date(startTime);
                    price = new BigDecimal(fields[1]);
                } else if (date != null) {
                    String[] fields = line.split(",");
                    long timestamp = startTime + Long.parseLong(fields[0]) * 60000L;
                    date = new Date(timestamp);
                    price = new BigDecimal(fields[1]);
                }
            }

            if (price != null) {
                BigDecimal oldPrice = stock.getPrice();
                if (price.compareTo(oldPrice) != 0) {
                    Calendar now = Calendar.getInstance();
                    Calendar lastDate = Calendar.getInstance();
                    lastDate.setTimeInMillis(stock.getTimestamp());
                    if (now.get(Calendar.DAY_OF_YEAR) != lastDate.get(Calendar.DAY_OF_YEAR)) {
                        // Different day, so update previous price with last known price.
                        stock.setPrevPrice(oldPrice);
                    }

                    // Update price and timestamp.
                    stock.setPrice(price);
                    stock.setTimestamp(now.getTimeInMillis());
                    isUpdated = true;

                    // LOGGER.debug(String.format("Updated %s: %s: $ %,.2f", stock, DATE_FORMAT.format(date), price));
                }
            }

        } catch (IOException e) {
            LOGGER.error(String.format("Failed to retrieve quote for %s: %s", stock, e.getMessage()));
        }

        return isUpdated;
    }
}
