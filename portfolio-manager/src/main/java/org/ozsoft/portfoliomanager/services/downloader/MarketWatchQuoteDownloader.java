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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Stock quote downloader using web scraping from MarketWatch (real-time). <br />
 * <br />
 *
 * Updates the stock's last price, price change percentage and P/E ratio.
 *
 * @author Oscar Stigter
 */
public class MarketWatchQuoteDownloader extends QuoteDownloader {

    // private static final String URI = "http://www.marketwatch.com/m/quote/%s";
    private static final String URI = "http://www.marketwatch.com/investing/stock/%s";

    private static final Pattern TYPE_PATTERN = Pattern.compile("<meta name=\"instrumentType\" content=\"(.*?)\">");

    // TODO: Support after-hours and pre-market prices
    // TODO: Retrieve dividend rate from MarketWatch iso CCC list
    private static final Pattern STOCK_PATTERN = Pattern.compile(
            "<meta name=\"exchange\" content=\"(.*?)\">.*<meta name=\"price\" content=\"(.*?)\">.*<meta name=\"priceChangePercent\" content=\"(.*?)%\">.*<small class=\"kv__label\">P/E Ratio</small>\\s*<span class=\"kv__value kv__primary .*?\">(.*?)</span>",
            Pattern.DOTALL);

    private static final Pattern ETF_PATTERN = Pattern.compile(
            "<meta name=\"exchange\" content=\"(.*?)\">.*<meta name=\"price\" content=\"(.*?)\">.*<meta name=\"priceChangePercent\" content=\"(.*?)%\">",
            Pattern.DOTALL);

    private static final Logger LOGGER = LogManager.getLogger(MarketWatchQuoteDownloader.class);

    /**
     * Constructor.
     *
     * @param httpPageReader
     *            The HTTP page reader.
     */
    public MarketWatchQuoteDownloader(HttpPageReader httpPageReader) {
        super(httpPageReader);
    }

    @Override
    public boolean updateStock(Stock stock) {
        boolean isUpdated = false;
        // long startTime;
        // long duration;

        try {
            // LOGGER.debug("Requesting quote for " + stock);
            // startTime = System.currentTimeMillis();
            String symbol = stock.getSymbol().replace('-', '.');
            String content = httpPageReader.read(String.format(URI, symbol)).trim();
            // duration = System.currentTimeMillis() - startTime;
            // LOGGER.debug(String.format("Received quote for %s (%,.0f kB) in %,d ms", stock, content.length() / 1024.0, duration));

            // Determine instrument type.
            Matcher m = TYPE_PATTERN.matcher(content);
            if (m.find()) {
                String type = m.group(1);
                if (type.equals("Stock")) {
                    // Common or preferred stock.
                    // LOGGER.debug("Parsing quote for " + stock);
                    // startTime = System.currentTimeMillis();
                    m = STOCK_PATTERN.matcher(content);
                    if (m.find()) {
                        BigDecimal price = new BigDecimal(m.group(2));
                        if (!price.equals(stock.getPrice())) {
                            stock.setPrice(price);
                            stock.setChangePerc(Double.parseDouble(m.group(3).replaceAll("n/a", "0.0")));
                            stock.setPeRatio(Double.parseDouble(m.group(4).replaceAll("n/a", "-1.0")));
                            // String exchange = m.group(1);
                            // double yield = Double.parseDouble(m.group(5).replaceAll("n/a", "0.0"));
                            // double divRate = Double.parseDouble(m.group(6).replaceAll("n/a", "0.0"));
                            isUpdated = true;
                            // duration = System.currentTimeMillis() - startTime;
                            // LOGGER.debug(String.format("Parsed quote for %s (%,.0f kB) in %,d ms", stock, content.length() / 1024.0, duration));
                            // LOGGER.debug("Updated price of " + stock);
                        }
                    } else {
                        LOGGER.error("Failed to parse quote for stock " + stock);
                        // System.out.println(content);
                    }
                } else {
                    // ETF or fund.
                    // LOGGER.debug("Parsing quote for " + stock);
                    // startTime = System.currentTimeMillis();
                    m = ETF_PATTERN.matcher(content);
                    if (m.find()) {
                        BigDecimal price = new BigDecimal(m.group(2));
                        if (!price.equals(stock.getPrice())) {
                            stock.setPrice(price);
                            stock.setChangePerc(Double.parseDouble(m.group(3).replaceAll("n/a", "0.0")));
                            stock.setPeRatio(-1.0);
                            isUpdated = true;
                            // duration = System.currentTimeMillis() - startTime;
                            // LOGGER.debug(String.format("Parsed quote for %s (%,.0f kB) in %,d ms", stock, content.length() / 1024.0, duration));
                            // LOGGER.debug("Updated price of " + stock);
                        }
                    } else {
                        LOGGER.error("Failed to parse quote for ETF/fund " + stock);
                        // System.out.println(content);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error(String.format("Failed to retrieve quote for %s: %s", stock, e.getMessage()));
        }

        return isUpdated;
    }
}
