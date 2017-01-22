package org.ozsoft.portfoliomanager.domain;

import java.math.BigDecimal;

import org.junit.Test;
import org.ozsoft.portfoliomanager.test.TestUtils;

/**
 * JUnit test suite for the {@link Position} class.
 *
 * @author Oscar Stigter
 */
public class PositionTest {

    /**
     * Performs the actual test.
     */
    @Test
    public void test() {
        Configuration.getInstance().setDeductIncomeTax(false);

        String symbol = "TST";
        Stock stock = new Stock(symbol, "Test Stock");
        stock.setPrice(new BigDecimal("10.00"));
        stock.setDivRate(new BigDecimal("1.00"));

        // Initial (empty) position.
        Position position = new Position(stock);
        TestUtils.assertEquals(0, position.getNoOfShares());
        TestUtils.assertEquals(0.00, position.getCurrentCost());
        TestUtils.assertEquals(0.00, position.getCurrentValue());
        TestUtils.assertEquals(0.00, position.getCurrentResult());
        TestUtils.assertEquals(0.00, position.getTotalCost());
        TestUtils.assertEquals(0.00, position.getAnnualIncome());
        TestUtils.assertEquals(0.00, position.getYieldOnCost());
        TestUtils.assertEquals(0.00, position.getTotalIncome());
        TestUtils.assertEquals(0.00, position.getTotalReturn());
        TestUtils.assertEquals(0.00, position.getTotalReturnPercentage());

        // BUY 100 @ $20 ($5 costs)
        stock.setPrice(new BigDecimal("20.00"));
        position.addTransaction(TestUtils.createTransaction(1, 1L, TransactionType.BUY, symbol, 100, 20.00, 5.00));
        TestUtils.assertEquals(100, position.getNoOfShares());
        TestUtils.assertEquals(2005.00, position.getCurrentCost());
        TestUtils.assertEquals(2000.00, position.getCurrentValue());
        TestUtils.assertEquals(-5.00, position.getCurrentResult());
        TestUtils.assertEquals(2005.00, position.getTotalCost());
        TestUtils.assertEquals(100.00, position.getAnnualIncome());
        TestUtils.assertEquals(4.99, position.getYieldOnCost());
        TestUtils.assertEquals(0.00, position.getTotalIncome());
        TestUtils.assertEquals(-5.00, position.getTotalReturn());
        TestUtils.assertEquals(-0.25, position.getTotalReturnPercentage());

        // DIVIDEND 100 @ $1.00
        position.addTransaction(TestUtils.createTransaction(2, 2L, TransactionType.DIVIDEND, symbol, 100, 1.00, 0.00));
        TestUtils.assertEquals(100, position.getNoOfShares());
        TestUtils.assertEquals(2005.00, position.getCurrentCost());
        TestUtils.assertEquals(2000.00, position.getCurrentValue());
        TestUtils.assertEquals(-5.00, position.getCurrentResult());
        TestUtils.assertEquals(2005.00, position.getTotalCost());
        TestUtils.assertEquals(100.00, position.getAnnualIncome());
        TestUtils.assertEquals(4.99, position.getYieldOnCost());
        TestUtils.assertEquals(100.00, position.getTotalIncome());
        TestUtils.assertEquals(+95.00, position.getTotalReturn());
        TestUtils.assertEquals(+4.74, position.getTotalReturnPercentage());

        // Price drops to $10
        stock.setPrice(new BigDecimal("10.00"));
        TestUtils.assertEquals(100, position.getNoOfShares());
        TestUtils.assertEquals(2005.00, position.getCurrentCost());
        TestUtils.assertEquals(1000.00, position.getCurrentValue());
        TestUtils.assertEquals(-1005.00, position.getCurrentResult());
        TestUtils.assertEquals(-50.12, position.getCurrentResultPercentage());
        TestUtils.assertEquals(2005.00, position.getTotalCost());
        TestUtils.assertEquals(100.00, position.getAnnualIncome());
        TestUtils.assertEquals(4.99, position.getYieldOnCost());
        TestUtils.assertEquals(100.00, position.getTotalIncome());
        TestUtils.assertEquals(-905.00, position.getTotalReturn());
        TestUtils.assertEquals(-45.14, position.getTotalReturnPercentage());

        // BUY another 100 @ $10 ($5 costs)
        position.addTransaction(TestUtils.createTransaction(3, 3L, TransactionType.BUY, symbol, 100, 10.00, 5.00));
        TestUtils.assertEquals(200, position.getNoOfShares());
        TestUtils.assertEquals(3010.00, position.getCurrentCost());
        TestUtils.assertEquals(2000.00, position.getCurrentValue());
        TestUtils.assertEquals(-1010.00, position.getCurrentResult());
        TestUtils.assertEquals(-33.55, position.getCurrentResultPercentage());
        TestUtils.assertEquals(3010.00, position.getTotalCost());
        TestUtils.assertEquals(200.00, position.getAnnualIncome());
        TestUtils.assertEquals(6.64, position.getYieldOnCost());
        TestUtils.assertEquals(100.00, position.getTotalIncome());
        TestUtils.assertEquals(-910.00, position.getTotalReturn());
        TestUtils.assertEquals(-30.23, position.getTotalReturnPercentage());

        // Price raises to $20 again
        stock.setPrice(new BigDecimal("20.00"));
        TestUtils.assertEquals(200, position.getNoOfShares());
        TestUtils.assertEquals(3010.00, position.getCurrentCost());
        TestUtils.assertEquals(4000.00, position.getCurrentValue());
        TestUtils.assertEquals(+990.00, position.getCurrentResult());
        TestUtils.assertEquals(+32.89, position.getCurrentResultPercentage());
        TestUtils.assertEquals(3010.00, position.getTotalCost());
        TestUtils.assertEquals(200.00, position.getAnnualIncome());
        TestUtils.assertEquals(6.64, position.getYieldOnCost());
        TestUtils.assertEquals(100.00, position.getTotalIncome());
        TestUtils.assertEquals(+1090.00, position.getTotalReturn());
        TestUtils.assertEquals(+36.21, position.getTotalReturnPercentage());

        // DIVIDEND 200 @ $1.25
        stock.setDivRate(new BigDecimal("1.25"));
        position.addTransaction(TestUtils.createTransaction(4, 4L, TransactionType.DIVIDEND, symbol, 200, 1.25, 0.00));
        TestUtils.assertEquals(200, position.getNoOfShares());
        TestUtils.assertEquals(3010.00, position.getCurrentCost());
        TestUtils.assertEquals(4000.00, position.getCurrentValue());
        TestUtils.assertEquals(+990.00, position.getCurrentResult());
        TestUtils.assertEquals(+32.89, position.getCurrentResultPercentage());
        TestUtils.assertEquals(3010.00, position.getTotalCost());
        TestUtils.assertEquals(250.00, position.getAnnualIncome());
        TestUtils.assertEquals(8.31, position.getYieldOnCost());
        TestUtils.assertEquals(350.00, position.getTotalIncome());
        TestUtils.assertEquals(+1340.00, position.getTotalReturn());
        TestUtils.assertEquals(+44.52, position.getTotalReturnPercentage());

        // SELL 200 @ $20 ($10 costs)
        position.addTransaction(TestUtils.createTransaction(5, 5L, TransactionType.SELL, symbol, 200, 20.00, 10.00));
        TestUtils.assertEquals(0, position.getNoOfShares());
        TestUtils.assertEquals(0.00, position.getCurrentCost());
        TestUtils.assertEquals(0.00, position.getCurrentValue());
        TestUtils.assertEquals(0.00, position.getCurrentResult());
        TestUtils.assertEquals(0.00, position.getCurrentResultPercentage());
        TestUtils.assertEquals(3020.00, position.getTotalCost());
        TestUtils.assertEquals(0.00, position.getAnnualIncome());
        TestUtils.assertEquals(0.00, position.getYieldOnCost());
        TestUtils.assertEquals(350.00, position.getTotalIncome());
        TestUtils.assertEquals(+1330.00, position.getTotalReturn());
        TestUtils.assertEquals(+44.04, position.getTotalReturnPercentage());
    }
}
