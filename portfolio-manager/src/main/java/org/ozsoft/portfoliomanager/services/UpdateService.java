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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.ozsoft.portfoliomanager.domain.ClosingPrice;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.StockAnalysis;
import org.ozsoft.portfoliomanager.domain.StockPerformance;
import org.ozsoft.portfoliomanager.domain.TimeRange;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Service to update and analyze stocks.
 * 
 * @author Oscar Stigter
 */
public class UpdateService {

    private static final String CCC_LIST_URI = "http://www.dripinvesting.org/Tools/U.S.DividendChampions.xls";

    private static final String SHEET_NAME = "All CCC";

    private static final int SYMBOL_COLUMN_INDEX = CellReference.convertColStringToIndex("B");

    private static final int YEARS_GROWTH_COLUMN_INDEX = CellReference.convertColStringToIndex("D");

    private static final int DIV_RATE_COLUMN_INDEX = CellReference.convertColStringToIndex("R");

    private static final int DIV_GROWTH_COLUMN_INDEX = CellReference.convertColStringToIndex("AN");

    private static final String HISTORICAL_CLOSINGS_URL = "http://ichart.finance.yahoo.com/table.csv?s=%s";

    // private static final String HISTORICAL_DIVIDENDS_URL = "http://ichart.finance.yahoo.com/table.csv?s=%s&g=v";

    private static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger LOGGER = LogManager.getLogger(UpdateService.class);

    private final Configuration config = Configuration.getInstance();

    private final HttpPageReader httpPageReader = new HttpPageReader();

    /**
     * Updates all stock data.
     * 
     * @return The number of updated stocks.
     */
    public int updateAllStockData() {
        // LOGGER.debug("Updating all stock data");
        updateStatistics();
        return updateAllPrices();
    }

    /**
     * Updates the price of a single stock.
     * 
     * @param stock
     *            The stock to update.
     * @return True if the stock was updated (price changed), otherwise false.
     */
    public boolean updatePrice(Stock stock) {
        boolean isUpdated = false;

        StockUpdater stockUpdater = new StockUpdater(stock, httpPageReader);
        stockUpdater.start();
        try {
            stockUpdater.join();
            isUpdated = stockUpdater.isUpdated();
        } catch (InterruptedException e) {
            // Safe to ignore.
        }

        return isUpdated;
    }

    /**
     * Analyzes all stocks based on their historic performance and current valuation (price only).
     * 
     * @return Message indicating the result of the analysis.
     */
    public String analyzeAllStocks() {
        // LOGGER.debug("Analyzing all stocks");
        List<StockAnalysis> analyses = new ArrayList<StockAnalysis>();
        for (Stock stock : config.getStocks()) {
            analyses.add(analyzeStock(stock));
        }
        Collections.sort(analyses);

        String resultMessage = null;

        // Write analysis results to CSV file.
        File file = config.getAnalysisResultFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(
                    "Symbol; 10-yr CAGR; 5-yr CAGR; 1-yr Change; Volatility; 52-wk High; 52-wk Low; Current Price; 5-yr Discount; 1-yr Discount; Score");
            writer.newLine();
            for (StockAnalysis analysis : analyses) {
                writer.write(analysis.toString());
                writer.newLine();
            }
            resultMessage = String.format("Analyzed %d stocks, output written to '%s'", analyses.size(), file.getAbsolutePath());
            LOGGER.info(resultMessage);

        } catch (IOException e) {
            resultMessage = String.format("Could not write stock analysis results to file '%s'", file.getAbsolutePath());
            LOGGER.error(resultMessage, e);
        }

        return resultMessage;
    }

    /**
     * Analyzes a single stock and prints the results to the console.
     * 
     * @param stock
     *            The stock to analyze.
     */
    public void printStockAnalysis(Stock stock) {
        StockAnalysis analysis = analyzeStock(stock);
        System.out.format("\nAnalysis for %s:\n\n", analysis.getStock());
        System.out.format("10-year CAGR:        %+.2f %%\n", analysis.getCagr10yr());
        System.out.format(" 5-year CAGR:        %+.2f %%\n", analysis.getCagr5yr());
        System.out.format(" 1-year change:      %+.2f %%\n", analysis.getChange1yr());
        System.out.format("10-year volatility:   %.2f %%\n", analysis.getVolatility());
        System.out.format("52-week high:       $ %.2f\n", analysis.getHigh52wk());
        System.out.format("52-week low:        $ %.2f\n", analysis.getLow52wk());
        System.out.format("Current price:      $ %.2f\n", analysis.getCurrentPrice());
        System.out.format(" 5-year discount:    %.2f %%\n", analysis.getDiscount5yr());
        System.out.format(" 1-year discount:    %.2f %%\n", analysis.getDiscount1yr());
        System.out.format("Score:               %.2f\n", analysis.getScore());
    }

    /**
     * Updates all stocks based on the latest version of David Fish' CCC list (Excel sheet, updated monthly). <br />
     * <br />
     * 
     * Checks for a newer version of the CCC list and updates the stocks only if present. <br />
     * <br />
     * 
     * Uses the Apache POI framework to parse the Excel sheet.
     */
    private void updateStatistics() {
        // Download CCC list if missing or newer available
        File cccListFile = config.getCCCListFile();
        long localTimestamp = (cccListFile.exists()) ? cccListFile.lastModified() : -1L;
        try {
            // LOGGER.debug("Checking for new version of CCC list");
            long remoteTimestamp = httpPageReader.getFileLastModified(CCC_LIST_URI);
            if (remoteTimestamp > localTimestamp) {
                downloadCCCList(cccListFile);
            }
            // Update stock statistics from CCC list
            // LOGGER.debug("Updating stock statistics");
            try {
                Workbook workbook = WorkbookFactory.create(cccListFile);
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                int count = 0;
                for (Row row : sheet) {
                    if (row.getRowNum() > 5) {
                        Cell cell = row.getCell(SYMBOL_COLUMN_INDEX);
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            String symbol = cell.getStringCellValue();
                            int yearsDivGrowth = (int) Math.floor(row.getCell(YEARS_GROWTH_COLUMN_INDEX).getNumericCellValue());
                            double divRate = row.getCell(DIV_RATE_COLUMN_INDEX).getNumericCellValue();
                            cell = row.getCell(DIV_GROWTH_COLUMN_INDEX);
                            double divGrowth = (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? cell.getNumericCellValue() : -1.0;

                            Stock stock = config.getStock(symbol);
                            if (stock != null) {
                                stock.setYearsDivGrowth(yearsDivGrowth);
                                stock.setDivRate(divRate);
                                stock.setDivGrowth(divGrowth);
                                count++;
                            }
                        }
                    }
                }
                LOGGER.info(String.format("Statistics updated for %d stocks", count));

            } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
                LOGGER.error("Failed to process CCC list", e);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to download CCC list", e);
        }
    }

    /**
     * Downloads the latest version of David Fish' CCC list (Excel sheet).
     * 
     * @param file
     *            The local CCC list file.
     */
    private void downloadCCCList(File file) {
        LOGGER.debug("Downloading latest version of the CCC list");
        InputStream is = null;
        OutputStream os = null;
        try {
            is = httpPageReader.downloadFile(CCC_LIST_URI);
            os = new BufferedOutputStream(new FileOutputStream(file));
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            LOGGER.error("Failed to download CCC list", e);
            file.delete();
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Updates all stock prices.
     * 
     * @return The number of updated stocks.
     */
    private int updateAllPrices() {
        return updatePrices(config.getStocks());
    }

    /**
     * Updates real-time prices for the specified stocks. <br />
     * <br />
     * Every stock is updated by its own {@see StockUpdater} thread for maximum performance (less total duration, at the cost of a large CPU and
     * network I/O burst).
     * 
     * @param stocks
     *            The stocks to update.
     * @return The number of updated stocks.
     */
    public int updatePrices(Set<Stock> stocks) {
        LOGGER.debug(String.format("Updating %d stock prices", stocks.size()));

        Set<StockUpdater> updaters = new HashSet<StockUpdater>();
        for (Stock stock : stocks) {
            StockUpdater updater = new StockUpdater(stock, httpPageReader);
            updaters.add(updater);
            updater.start();
        }

        int updatedCount = 0;
        for (StockUpdater updater : updaters) {
            try {
                updater.join();
                if (updater.isUpdated()) {
                    updatedCount++;
                }
            } catch (InterruptedException e) {
                // Safe to ignore.
            }
        }

        LOGGER.info(String.format("%s stocks updated", updatedCount));

        return updatedCount;
    }

    /**
     * Analyzes a stock based on its historic performance and current valuation (price only).
     * 
     * @param stock
     *            The stock to analyze.
     * @return The stock analysis result.
     */
    private StockAnalysis analyzeStock(Stock stock) {
        List<ClosingPrice> prices = getClosingPrices(stock);

        StockPerformance perf10yr = new StockPerformance(prices, TimeRange.TEN_YEAR);
        StockPerformance perf5yr = new StockPerformance(prices, TimeRange.FIVE_YEAR);
        StockPerformance perf1yr = new StockPerformance(prices, TimeRange.ONE_YEAR);

        double score = 20.0 + perf10yr.getCagr() + perf5yr.getCagr() - 12.0 + 2.0 * (perf5yr.getCagr() - perf10yr.getCagr())
                - 0.5 * (perf10yr.getVolatility() - 10.0) + 0.5 * perf1yr.getDiscount();

        StockAnalysis analysis = new StockAnalysis(stock, perf10yr.getCagr(), perf5yr.getCagr(), perf1yr.getChangePerc(), perf10yr.getVolatility(),
                perf1yr.getHighPrice(), perf1yr.getLowPrice(), perf1yr.getEndPrice(), perf5yr.getDiscount(), perf1yr.getDiscount(), score);

        return analysis;
    }

    /**
     * Retrieves the historic closing prices for a stock.
     * 
     * @param stock
     *            The stock.
     * @return The historic closing prices.
     */
    private List<ClosingPrice> getClosingPrices(Stock stock) {
        String symbol = stock.getSymbol();
        String uri = String.format(HISTORICAL_CLOSINGS_URL, symbol);
        List<ClosingPrice> prices = new ArrayList<ClosingPrice>();
        boolean inHeader = true;
        try {
            String response = httpPageReader.read(uri);
            for (String line : response.split("\n")) {
                if (inHeader) {
                    inHeader = false;
                } else {
                    String[] fields = line.split(",");
                    if (fields.length == 7) {
                        try {
                            Date date = DATE_FORMAT_SHORT.parse(fields[0]);
                            double value = Double.parseDouble(fields[6]);
                            prices.add(new ClosingPrice(date, value));
                        } catch (ParseException e) {
                            LOGGER.error(String.format("Could not parse price quote: '%s'", line), e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Could retrieve historical prices for '%s'", symbol), e);
        }

        return prices;
    }
    // /**
    // * Retrieves the historic dividends of a stock.
    // *
    // * @param stock
    // * The stock.
    // *
    // * @return The historic dividends.
    // */
    // private List<ClosingPrice> getDividends(Stock stock) {
    // String symbol = stock.getSymbol();
    // String uri = String.format(HISTORICAL_DIVIDENDS_URL, symbol);
    // List<ClosingPrice> prices = new ArrayList<ClosingPrice>();
    // boolean inHeader = true;
    // try {
    // String response = httpPageReader.read(uri);
    // for (String line : response.split("\n")) {
    // if (inHeader) {
    // inHeader = false;
    // } else {
    // String[] fields = line.split(",");
    // if (fields.length == 2) {
    // try {
    // Date date = DATE_FORMAT_SHORT.parse(fields[0]);
    // double value = Double.parseDouble(fields[1]);
    // prices.add(new ClosingPrice(date, value));
    // } catch (ParseException e) {
    // LOGGER.error(String.format("ERROR: Could not parse price quote: '%s'\n", line);
    // }
    // }
    // }
    // }
    // } catch (IOException e) {
    // LOGGER.error(String.format("ERROR: Could retrieve historical dividend payments for '%s': %s\n", symbol,
    // e.getMessage());
    // e.printStackTrace(System.err);
    // }
    //
    // return prices;
    // }
}
