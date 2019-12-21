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
import org.ozsoft.portfoliomanager.services.downloader.YahooFinanceQuoteDownloader;

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

        YahooFinanceQuoteDownloader downloader = new YahooFinanceQuoteDownloader();

        List<Quote> prices = downloader.getHistoricPrices(stock);
        List<Quote> dividends = downloader.getDividendPayouts(stock);

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
}
