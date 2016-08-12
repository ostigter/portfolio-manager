package org.ozsoft.portfoliomanager.domain;

import org.junit.Assert;
import org.junit.Test;
import org.ozsoft.portfoliomanager.test.TestUtil;

/**
 * JUnit test suite for the {@link Portfolio} class.
 * 
 * @author Oscar Stigter
 */
public class PortfolioTest {

    /** Comparison delta for monetary values (doubles). */
    private static final double DELTA = 0.01;

    /**
     * Performs the actual test.
     */
    @Test
    public void test() {
        Configuration config = Configuration.getInstance();
        config.setDeductIncomeTax(false);

        // Create new (empty) portfolio.
        Portfolio portfolio = new Portfolio();
        Assert.assertEquals(0.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(0.00, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalReturnPercentage(), DELTA);

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
        stock1.setPrice(20.00);
        stock1.setDivRate(1.00);
        portfolio.addTransaction(TestUtil.createTransaction(1, 1L, TransactionType.BUY, symbol1, 100, 20.00, 5.00));
        portfolio.update(config);
        Assert.assertEquals(2005.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(2000.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(-5.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(-0.25, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(2005.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(100.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.99, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(-5.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(-0.25, portfolio.getTotalReturnPercentage(), DELTA);

        // BUY 100 stock 2 @ $10 ($1 costs)
        stock2.setPrice(10.00);
        stock2.setDivRate(0.25);
        portfolio.addTransaction(TestUtil.createTransaction(2, 2L, TransactionType.BUY, symbol2, 100, 10.00, 1.00));
        portfolio.update(config);
        Assert.assertEquals(3006.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(3000.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(-6.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(-0.20, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3006.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(125.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.16, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(-6.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(-0.20, portfolio.getTotalReturnPercentage(), DELTA);

        // DIVIDEND stock 1 100 @ $1.00
        stock1.setDivRate(1.00);
        portfolio.addTransaction(TestUtil.createTransaction(3, 3L, TransactionType.DIVIDEND, symbol1, 100, 1.00, 0.00));
        portfolio.update(config);
        Assert.assertEquals(3006.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(3000.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(-6.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(-0.20, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3006.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(125.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.16, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(94.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(3.13, portfolio.getTotalReturnPercentage(), DELTA);

        // Recalculate with subtracted dividend tax
        config.setDeductIncomeTax(true);
        portfolio.update(config);
        Assert.assertEquals(0.85 * 125.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(0.85 * 100.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(79.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(2.63, portfolio.getTotalReturnPercentage(), DELTA);

        // Disable dividend tax subtraction again
        config.setDeductIncomeTax(false);

        // SELL stock 2 100 @ $15 ($2 costs)
        portfolio.addTransaction(TestUtil.createTransaction(4, 4L, TransactionType.SELL, symbol2, 100, 15.00, 2.00));
        portfolio.update(config);
        Assert.assertEquals(2005.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(2000.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(-5.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(-0.25, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3008.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(100.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.99, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(592.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(19.68, portfolio.getTotalReturnPercentage(), DELTA);

        // Add a SELL transaction for stock 1
        portfolio.addTransaction(TestUtil.createTransaction(5, 5L, TransactionType.SELL, symbol1, 100, 25.00, 5.00));
        portfolio.update(config);
        Assert.assertEquals(0.00, portfolio.getCurrentCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentValue(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentResult(), DELTA);
        Assert.assertEquals(0.00, portfolio.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3013.00, portfolio.getTotalCost(), DELTA);
        Assert.assertEquals(0.00, portfolio.getAnnualIncome(), DELTA);
        Assert.assertEquals(0.00, portfolio.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, portfolio.getTotalIncome(), DELTA);
        Assert.assertEquals(1087.00, portfolio.getTotalReturn(), DELTA);
        Assert.assertEquals(36.08, portfolio.getTotalReturnPercentage(), DELTA);
    }
}
