package org.ozsoft.portfoliomanager.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Quote;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.StockAnalysis;
import org.ozsoft.portfoliomanager.domain.StockPerformance;
import org.ozsoft.portfoliomanager.domain.TimeRange;

/**
 * Service for analyzing stocks.
 *
 * @author Oscar Stigter
 */
public class AnalyzeService {

    private static final Logger LOGGER = LogManager.getLogger(UpdateService.class);

    private final Configuration config;

    public AnalyzeService() {
        config = Configuration.getInstance();
    }

    /**
     * Analyzes a stock based on its historic performance and current valuation (price only).
     *
     * @param stock
     *                  The stock to analyze.
     * @return The stock analysis result.
     */
    public StockAnalysis analyzeStock(Stock stock) {
        LOGGER.debug(String.format("Analyzing %s", stock));

        // Get all historical closing prices.
        List<Quote> prices = null;
//		try {
//			prices = StockBase.getHistoricPrices(stock.getSymbol());
//		} catch (IOException e) {
//			LOGGER.error("Could not retrieve historic closing prices for " + stock, e);
//		}

        // Get all historical dividend payments.
        List<Quote> dividends = null;
//		try {
//			dividends = StockBase.getHistoricDividends(stock.getSymbol());
//		} catch (IOException e) {
//			LOGGER.error("Could not retrieve historic closing prices for " + stock, e);
//		}

        StockPerformance perf10yr = new StockPerformance(prices, dividends, TimeRange.TEN_YEAR);
        StockPerformance perf5yr = new StockPerformance(prices, dividends, TimeRange.FIVE_YEAR);
        StockPerformance perf1yr = new StockPerformance(prices, dividends, TimeRange.ONE_YEAR);

        double score = 20.0 + perf10yr.getCagr() + perf5yr.getCagr() - 12.0 + 2.0 * (perf5yr.getCagr() - perf10yr.getCagr())
                - 0.5 * (perf10yr.getVolatility() - 10.0) + 0.5 * perf1yr.getDiscount();

        StockAnalysis analysis = new StockAnalysis(stock, perf10yr.getCagr(), perf5yr.getCagr(), perf1yr.getChangePerc(), perf10yr.getVolatility(),
                perf1yr.getHighPrice(), perf1yr.getLowPrice(), perf1yr.getEndPrice(), perf5yr.getDiscount(), perf1yr.getDiscount(), score);

        return analysis;
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

    // /**
    // * Retrieves the historic closing prices for a stock.
    // *
    // * @param stock
    // * The stock.
    // * @return The historic closing prices.
    // */
    // private List<Quote> getQuotes(Stock stock) {
    // String symbol = stock.getSymbol();
    // String uri = String.format(HISTORICAL_PRICES_URL, symbol);
    // List<Quote> prices = new ArrayList<Quote>();
    // boolean inHeader = true;
    // try {
    // String response = httpPageReader.read(uri);
    // for (String line : response.split("\n")) {
    // if (inHeader) {
    // inHeader = false;
    // } else {
    // String[] fields = line.split(",");
    // if (fields.length == 7) {
    // try {
    // Date date = DATE_FORMAT_SHORT.parse(fields[0]);
    // BigDecimal value = new BigDecimal(fields[6]);
    // prices.add(new Quote(date, value));
    // } catch (ParseException e) {
    // LOGGER.error(String.format("Could not parse price quote: '%s'", line), e);
    // }
    // }
    // }
    // }
    // } catch (IOException e) {
    // LOGGER.error(String.format("Could retrieve historical prices for '%s'",
    // symbol), e);
    // }
    //
    // return prices;
    // }
    //
    // /**
    // * Retrieves the historic dividends of a stock.
    // *
    // * @param stock
    // * The stock.
    // *
    // * @return The historic dividends.
    // */
    // public List<Quote> getDividends(Stock stock) {
    // String symbol = stock.getSymbol();
    // String uri = String.format(HISTORICAL_DIVIDENDS_URL, symbol);
    // List<Quote> prices = new ArrayList<Quote>();
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
    // BigDecimal value = new BigDecimal(fields[1]);
    // prices.add(new Quote(date, value));
    // } catch (ParseException e) {
    // LOGGER.error(String.format("Could not parse price quote: '%s'", line));
    // }
    // }
    // }
    // }
    // } catch (IOException e) {
    // LOGGER.error(String.format("Could retrieve historical dividend payments for
    // '%s': %s", symbol, e.getMessage()));
    // e.printStackTrace(System.err);
    // }
    //
    // return prices;
    // }
}
