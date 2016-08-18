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

package org.ozsoft.portfoliomanager.services.downloader;

import java.io.IOException;
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

    private static final String URI = "http://www.marketwatch.com/m/quote/%s";

    // FIXME: Correctly handle price change percentage outside market hours.
    private static final Pattern PATTERN = Pattern.compile(
            "<div class=\"last\">\\s*\\$(.*?)\\s*</div>.*?<span class=\"ticker.*?\">\\s*(.*?) \\((.*?)%\\)\\s*</span>.*?<td class=\"label\">P/E:</td>.*?<td class=\"number\">(.*?)</td>",
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

        try {
            // LOGGER.debug("Requesting stock quote for " + stock);
            // long startTime = System.currentTimeMillis();
            String content = httpPageReader.read(String.format(URI, stock.getSymbol())).trim();
            // long duration = System.currentTimeMillis() - startTime;
            // LOGGER.debug(String.format("Received stock quote for %s (%,.0f kB) in %,d ms", stock, content.length() / 1024.0, duration));

            Matcher m = PATTERN.matcher(content);
            if (m.find()) {
                double price = Double.parseDouble(m.group(1));
                if (price != stock.getPrice()) {
                    // double change = Double.parseDouble(m.group(2).replaceAll("\\s", "").replaceAll("N/A", "0.0"));
                    stock.setPrice(price);
                    stock.setChangePerc(Double.parseDouble(m.group(3).replaceAll("N/A", "0.0")));
                    stock.setPeRatio(Double.parseDouble(m.group(4).replaceAll("N/A", "-1.0")));
                    isUpdated = true;
                    // LOGGER.debug("Updated stock price of " + stock);
                }
            } else {
                LOGGER.error("Failed to parse stock quote for " + stock);
                System.out.println(content);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to retrieve stock quote for %s: %s", stock, e.getMessage()));
        }

        return isUpdated;
    }
}