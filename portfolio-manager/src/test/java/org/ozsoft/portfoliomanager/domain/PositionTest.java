package org.ozsoft.portfoliomanager.domain;

import org.junit.Assert;
import org.junit.Test;
import org.ozsoft.portfoliomanager.test.TestUtil;

/**
 * JUnit test suite for the {@link Position} class.
 * 
 * @author Oscar Stigter
 */
public class PositionTest {

    /** Comparison delta for monetary values (doubles). */
    private static final double DELTA = 0.01;

    /**
     * Performs the actual test.
     */
    @Test
    public void test() {
        Configuration.getInstance().setDeductIncomeTax(false);

        String symbol = "TST";
        Stock stock = new Stock(symbol, "Test Stock");
        stock.setPrice(10.00);
        stock.setDivRate(1.00);

        // Initial (empty) position.
        Position position = new Position(stock);
        Assert.assertEquals(0, position.getNoOfShares());
        Assert.assertEquals(0.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(0.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(0.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(0.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(0.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(0.00, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(0.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(0.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(0.00, position.getTotalReturnPercentage(), DELTA);

        // BUY 100 @ $20 ($5 costs)
        stock.setPrice(20.00);
        position.addTransaction(TestUtil.createTransaction(1, 1L, TransactionType.BUY, symbol, 100, 20.00, 5.00));
        Assert.assertEquals(100, position.getNoOfShares());
        Assert.assertEquals(2005.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(2000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(-5.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(2005.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(100.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.99, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(0.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(-5.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(-0.25, position.getTotalReturnPercentage(), DELTA);

        // DIVIDEND 100 @ $1.00
        position.addTransaction(TestUtil.createTransaction(2, 2L, TransactionType.DIVIDEND, symbol, 100, 1.00, 0.00));
        Assert.assertEquals(100, position.getNoOfShares());
        Assert.assertEquals(2005.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(2000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(-5.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(2005.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(100.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.99, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(+95.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(+4.74, position.getTotalReturnPercentage(), DELTA);

        // Price drops to $10
        stock.setPrice(10.00);
        Assert.assertEquals(100, position.getNoOfShares());
        Assert.assertEquals(2005.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(1000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(-1005.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(-50.12, position.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(2005.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(100.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(4.99, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(-905.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(-45.14, position.getTotalReturnPercentage(), DELTA);

        // BUY another 100 @ $10 ($5 costs)
        position.addTransaction(TestUtil.createTransaction(3, 3L, TransactionType.BUY, symbol, 100, 10.00, 5.00));
        Assert.assertEquals(200, position.getNoOfShares());
        Assert.assertEquals(3010.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(2000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(-1010.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(-33.55, position.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3010.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(200.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(6.64, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(-910.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(-30.23, position.getTotalReturnPercentage(), DELTA);

        // Price raises to $20 again
        stock.setPrice(20.00);
        Assert.assertEquals(200, position.getNoOfShares());
        Assert.assertEquals(3010.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(4000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(+990.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(+32.89, position.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3010.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(200.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(6.64, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(100.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(+1090.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(+36.21, position.getTotalReturnPercentage(), DELTA);

        // DIVIDEND 200 @ $1.25
        stock.setDivRate(1.25);
        position.addTransaction(TestUtil.createTransaction(4, 4L, TransactionType.DIVIDEND, symbol, 200, 1.25, 0.00));
        Assert.assertEquals(200, position.getNoOfShares());
        Assert.assertEquals(3010.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(4000.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(+990.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(+32.89, position.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3010.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(250.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(8.31, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(350.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(+1340.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(+44.52, position.getTotalReturnPercentage(), DELTA);

        // SELL 200 @ $20 ($10 costs)
        position.addTransaction(TestUtil.createTransaction(5, 5L, TransactionType.SELL, symbol, 200, 20.00, 10.00));
        Assert.assertEquals(0, position.getNoOfShares());
        Assert.assertEquals(0.00, position.getCurrentCost(), DELTA);
        Assert.assertEquals(0.00, position.getCurrentValue(), DELTA);
        Assert.assertEquals(0.00, position.getCurrentResult(), DELTA);
        Assert.assertEquals(0.00, position.getCurrentResultPercentage(), DELTA);
        Assert.assertEquals(3020.00, position.getTotalCost(), DELTA);
        Assert.assertEquals(0.00, position.getAnnualIncome(), DELTA);
        Assert.assertEquals(0.00, position.getYieldOnCost(), DELTA);
        Assert.assertEquals(350.00, position.getTotalIncome(), DELTA);
        Assert.assertEquals(+1330.00, position.getTotalReturn(), DELTA);
        Assert.assertEquals(+44.04, position.getTotalReturnPercentage(), DELTA);
    }
}
