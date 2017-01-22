package org.ozsoft.portfoliomanager.domain;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.ozsoft.portfoliomanager.test.TestUtils;

/**
 * JUnit test suite for the {@link Portfolio} class.
 *
 * @author Oscar Stigter
 */
public class PortfolioTest {

    /**
     * Performs the actual test.
     */
    @Test
    public void test() {
        Configuration config = Configuration.getInstance();
        config.setDeductIncomeTax(false);

        // Create new (empty) portfolio.
        Portfolio portfolio = new Portfolio();
        TestUtils.assertEquals(0.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(0.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(0.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(0.00, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(0.00, portfolio.getTotalCost());
        TestUtils.assertEquals(0.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(0.00, portfolio.getYieldOnCost());
        TestUtils.assertEquals(0.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(0.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(0.00, portfolio.getTotalReturnPercentage());

        // Add positions for some stocks.
        String symbol1 = "TST1";
        Stock stock1 = new Stock(symbol1, "Test Stock 1");
        config.addStock(stock1);
        String symbol2 = "TST2";
        Stock stock2 = new Stock(symbol2, "Test Stock 2");
        config.addStock(stock2);
        Assert.assertNull(portfolio.getPosition(stock1));
        Assert.assertNull(portfolio.getPosition(stock2));

        // BUY 100 stock 1 @ $20 ($5 costs)
        stock1.setPrice(new BigDecimal("20.00"));
        stock1.setDivRate(new BigDecimal("1.00"));
        portfolio.addTransaction(TestUtils.createTransaction(1, 1L, TransactionType.BUY, symbol1, 100, 20.00, 5.00));
        portfolio.update(config);
        TestUtils.assertEquals(2005.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(2000.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(-5.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(-0.25, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(2005.00, portfolio.getTotalCost());
        TestUtils.assertEquals(100.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(4.99, portfolio.getYieldOnCost());
        TestUtils.assertEquals(0.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(-5.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(-0.25, portfolio.getTotalReturnPercentage());

        // BUY 100 stock 2 @ $10 ($1 costs)
        stock2.setPrice(new BigDecimal("10.00"));
        stock2.setDivRate(new BigDecimal("0.25"));
        portfolio.addTransaction(TestUtils.createTransaction(2, 2L, TransactionType.BUY, symbol2, 100, 10.00, 1.00));
        portfolio.update(config);
        TestUtils.assertEquals(3006.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(3000.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(-6.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(-0.20, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(3006.00, portfolio.getTotalCost());
        TestUtils.assertEquals(125.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(4.16, portfolio.getYieldOnCost());
        TestUtils.assertEquals(0.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(-6.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(-0.20, portfolio.getTotalReturnPercentage());

        // DIVIDEND stock 1 100 @ $1.00
        stock1.setDivRate(new BigDecimal("1.00"));
        portfolio.addTransaction(TestUtils.createTransaction(3, 3L, TransactionType.DIVIDEND, symbol1, 100, 1.00, 0.00));
        portfolio.update(config);
        TestUtils.assertEquals(3006.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(3000.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(-6.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(-0.20, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(3006.00, portfolio.getTotalCost());
        TestUtils.assertEquals(125.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(4.16, portfolio.getYieldOnCost());
        TestUtils.assertEquals(100.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(94.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(3.13, portfolio.getTotalReturnPercentage());

        // Recalculate with subtracted dividend tax
        config.setDeductIncomeTax(true);
        portfolio.update(config);
        TestUtils.assertEquals(0.85 * 125.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(0.85 * 100.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(79.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(2.63, portfolio.getTotalReturnPercentage());

        // Disable dividend tax subtraction again
        config.setDeductIncomeTax(false);

        // SELL stock 2 100 @ $15 ($2 costs)
        portfolio.addTransaction(TestUtils.createTransaction(4, 4L, TransactionType.SELL, symbol2, 100, 15.00, 2.00));
        portfolio.update(config);
        TestUtils.assertEquals(2005.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(2000.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(-5.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(-0.25, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(3008.00, portfolio.getTotalCost());
        TestUtils.assertEquals(100.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(4.99, portfolio.getYieldOnCost());
        TestUtils.assertEquals(100.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(592.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(19.68, portfolio.getTotalReturnPercentage());

        // Add a SELL transaction for stock 1
        portfolio.addTransaction(TestUtils.createTransaction(5, 5L, TransactionType.SELL, symbol1, 100, 25.00, 5.00));
        portfolio.update(config);
        TestUtils.assertEquals(0.00, portfolio.getCurrentCost());
        TestUtils.assertEquals(0.00, portfolio.getCurrentValue());
        TestUtils.assertEquals(0.00, portfolio.getCurrentResult());
        TestUtils.assertEquals(0.00, portfolio.getCurrentResultPercentage());
        TestUtils.assertEquals(3013.00, portfolio.getTotalCost());
        TestUtils.assertEquals(0.00, portfolio.getAnnualIncome());
        TestUtils.assertEquals(0.00, portfolio.getYieldOnCost());
        TestUtils.assertEquals(100.00, portfolio.getTotalIncome());
        TestUtils.assertEquals(1087.00, portfolio.getTotalReturn());
        TestUtils.assertEquals(36.08, portfolio.getTotalReturnPercentage());
    }
}
