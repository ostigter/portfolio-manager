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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The applcation's configuration. <br />
 * <br />
 *
 * Implemented as singleton for easy use.
 *
 * @author Oscar Stigter
 */
public class Configuration {

    private static final File DATA_DIR = new File("data");

    private static final File CCC_LIST_FILE = new File(DATA_DIR, "CCC_list.xls");

    private static final File PORTFOLIO_FILE = new File(DATA_DIR, "portfolio.json");

    private static final File ANALYSIS_RESULT_FILE = new File(DATA_DIR, "stock_analysis.csv");

    private static final BigDecimal INCOME_TAX_RATE = new BigDecimal("0.15");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final int MAX_BACKUP_COUNT = 5;

    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);

    private static Configuration config;

    private boolean roundTotals = true;

    private boolean showClosedPositions = false;

    private boolean deductIncomeTax = false;

    private final TreeMap<String, Stock> stocks;

    private final List<Transaction> transactions;

    /**
     * Constructor.
     */
    private Configuration() {
        stocks = new TreeMap<String, Stock>();
        transactions = new ArrayList<Transaction>();

        Locale.setDefault(Locale.US);
    }

    /**
     * Returns the singleton instance.
     *
     * @return The singleton instance.
     */
    public static Configuration getInstance() {
        if (config == null) {
            Configuration.createDailyBackup();
            config = Configuration.load();
        }
        return config;
    }

    /**
     * Returns the latest downloaded CCC list file (Excel sheet), or {@code null} if not present (stock data has never been updated).
     *
     * @return The CCC list file.
     */
    public File getCCCListFile() {
        return CCC_LIST_FILE;
    }

    /**
     * Returns the latest generated analysis result file, or {@code null} if not present (analysis has never been run).
     *
     * @return The analysis result file.
     */
    public File getAnalysisResultFile() {
        return ANALYSIS_RESULT_FILE;
    }

    /**
     * Returns all stocks, sorted by their name.
     *
     * @return All stocks.
     */
    public Set<Stock> getStocks() {
        return new TreeSet<Stock>(stocks.values());
    }

    /**
     * Returns all currently owned stocks (open postions).
     *
     * @return
     */
    public Set<Stock> getOwnedStocks() {
        Set<Stock> ownedStocks = new TreeSet<Stock>();
        for (Position position : getPortfolio().getPositions()) {
            if (showClosedPositions || position.getNoOfShares().signum() > 0) {
                ownedStocks.add(position.getStock());
            }
        }
        return ownedStocks;
    }

    /**
     * Returns a stock based on its symbol.
     *
     * @param symbol
     *            The stock's symbol.
     *
     * @return The stock if found, otherwise {@code null}.
     */
    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }

    /**
     * Adds a new stock. <br />
     * <br />
     *
     * Only adds a stock if is new, based on its symbol.
     *
     * @param stock
     *            The stock.
     *
     * @return {@code true} if the stock was added, otherwise {@code false}.
     */
    public boolean addStock(Stock stock) {
        String symbol = stock.getSymbol();
        if (!stocks.containsKey(symbol)) {
            stocks.put(symbol, stock);
            LOGGER.info("Added stock: " + stock);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Permenently deletes a stock (including all data).
     *
     * @param stock
     *            The stock.
     *
     * @return {@code true} if the stock was deleted, otherwise {@code false}.
     */
    public boolean deleteStock(Stock stock) {
        String symbol = stock.getSymbol();
        if (stocks.containsKey(symbol)) {
            stocks.remove(symbol);
            LOGGER.info("Deleted stock: " + stock);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns all transactions, sorted by date.
     *
     * @return The transactions.
     */
    public List<Transaction> getTransactions() {
        // TODO: Only sort and update IDs when needed (dirty flag).
        Collections.sort(transactions);

        // Update transaction IDs (incremental, sorted by date).
        int id = 1;
        for (Transaction transaction : transactions) {
            transaction.setId(id++);
        }

        return Collections.unmodifiableList(transactions);
    }

    /**
     * Adds a transaction.
     *
     * @param transaction
     *            The transaction.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Deletes a transaction.
     *
     * @param transaction
     *            The transaction.
     */
    public void deleteTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    /**
     * Returns the current stock portfolio.
     *
     * @return The portfolio.
     */
    public Portfolio getPortfolio() {
        Portfolio portfolio = new Portfolio();
        for (Transaction transaction : getTransactions()) {
            portfolio.addTransaction(transaction);
        }
        portfolio.update(this);
        return portfolio;
    }

    /**
     * Returns whether a position (open or closed) exists for the specified stock.
     *
     * @param stock
     *            The stock.
     *
     * @return {@code true} is a position exists, otherwise {@code false}.
     */
    public boolean hasPosition(Stock stock) {
        String symbol = stock.getSymbol();
        for (Transaction tx : transactions) {
            if (tx.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether to round monetary totals values (do not display cents).
     *
     * @return whether to round monetary totals
     */
    public boolean isRoundTotals() {
        return roundTotals;
    }

    /**
     * Sets whether to round monetary totals values (do not display cents).
     *
     * @param roundTotals
     *            {@code true} if values should be rounded, otherwise {@code false}.
     */
    public void setRoundTotals(boolean roundTotals) {
        this.roundTotals = roundTotals;
    }

    /**
     * Returns whether to show closed (sold) positions.
     *
     * @return Whether to show closed positions.
     */
    public boolean getShowClosedPositions() {
        return showClosedPositions;
    }

    /**
     * Sets wether to show closed (sold) positions.
     *
     * @param showClosedPositions
     *            Whether to show closed positions.
     */
    public void setShowClosedPositions(boolean showClosedPositions) {
        this.showClosedPositions = showClosedPositions;
    }

    /**
     * Returns the income tax rate.
     *
     * @return The income tax rate.
     */
    public static BigDecimal getIncomeTaxRate() {
        // TODO: Make income tax rate configurable (globally and/or per stock).
        return INCOME_TAX_RATE;
    }

    /**
     * Returns whether to automatically deduct income tax from received income.
     *
     * @return Whether to deduct income tax.
     */
    public boolean isDeductIncomeTax() {
        return deductIncomeTax;
    }

    /**
     * Sets whether to automatically deduct income tax from received income.
     *
     * @param deductIncomeTax
     *            Whether to deduct income tax.
     */
    public void setDeductIncomeTax(boolean deductIncomeTax) {
        this.deductIncomeTax = deductIncomeTax;
    }

    /**
     * Loads the configuration from file.
     *
     * @return The configuration.
     */
    private static Configuration load() {
        if (DATA_DIR.isDirectory()) {
            Gson gson = new GsonBuilder().create();
            if (PORTFOLIO_FILE.isFile()) {
                try (Reader reader = new BufferedReader(new FileReader(PORTFOLIO_FILE))) {
                    config = gson.fromJson(reader, Configuration.class);
                    LOGGER.debug("Configuration loaded");
                } catch (IOException e) {
                    LOGGER.error("Could not read data file: " + PORTFOLIO_FILE.getAbsolutePath(), e);
                }
            }
        }

        if (config == null) {
            config = new Configuration();
            LOGGER.debug("Created new/default configuration");
        }

        return config;
    }

    /**
     * Saves the configuration to file.
     */
    public static void save() {
        if (!DATA_DIR.exists()) {
            DATA_DIR.mkdirs();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new BufferedWriter(new FileWriter(PORTFOLIO_FILE))) {
            gson.toJson(config, writer);
            LOGGER.debug("Configuration saved");
        } catch (IOException e) {
            LOGGER.error("Could not write data file: " + PORTFOLIO_FILE.getAbsolutePath(), e);
        }
    }

    private static void createDailyBackup() {
        if (PORTFOLIO_FILE.exists()) {
            String timestamp = DATE_FORMAT.format(new Date());
            String backupFileName = PORTFOLIO_FILE.getName() + "." + timestamp;
            File backupFile = new File(DATA_DIR, backupFileName);
            if (!backupFile.exists()) {
                try {
                    Files.copy(PORTFOLIO_FILE.toPath(), backupFile.toPath());
                    LOGGER.debug("Daily backup created");
                    cleanBackups();
                } catch (IOException e) {
                    LOGGER.error("Could not create backup of portfolio file", e);
                }
            }
        }
    }

    private static void cleanBackups() {
        Set<File> backupFiles = new TreeSet<File>();
        for (File file : DATA_DIR.listFiles()) {
            if (file.getName().startsWith(PORTFOLIO_FILE.getName() + ".")) {
                backupFiles.add(file);
            }
        }

        while (backupFiles.size() > MAX_BACKUP_COUNT) {
            File file = backupFiles.iterator().next();
            if (file.delete()) {
                LOGGER.debug("Deleted old backup file: " + file.getName());
            } else {
                LOGGER.error("Could not delete old backup file: " + file.getName());
            }
            backupFiles.remove(file);
        }
    }
}
