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

package org.ozsoft.portfoliomanager.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
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
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Service to update stocks.
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
//        updateStatistics();
        return updateAllPrices();
    }

    /**
     * Updates the price of a single stock.
     *
     * @param stock
     *                  The stock to update.
     * 
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
            long remoteTimestamp = httpPageReader.getFileLastModified(CCC_LIST_URI);
            if (remoteTimestamp > localTimestamp) {
                downloadCCCList(cccListFile);
            }
            // Update stock statistics from CCC list
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
                            BigDecimal divRate = new BigDecimal(row.getCell(DIV_RATE_COLUMN_INDEX).toString());
                            cell = row.getCell(DIV_GROWTH_COLUMN_INDEX);
                            BigDecimal divGrowth = (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? new BigDecimal(cell.toString())
                                    : new BigDecimal("-1.0");
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
     *                 The local CCC list file.
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
     *                   The stocks to update.
     *
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
}
