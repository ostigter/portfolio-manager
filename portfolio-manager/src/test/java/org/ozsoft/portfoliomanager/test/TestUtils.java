package org.ozsoft.portfoliomanager.test;

import java.math.BigDecimal;

import org.junit.Assert;
import org.ozsoft.portfoliomanager.domain.Transaction;
import org.ozsoft.portfoliomanager.domain.TransactionType;

public abstract class TestUtils {

    /** Comparison delta for monetary values. */
    private static final BigDecimal DELTA = new BigDecimal("0.01");

    /**
     * Private constructor to deny instantiation.
     */
    private TestUtils() {
        // Empty implementation.
    }

    public static void assertEquals(BigDecimal expectedValue, BigDecimal actualValue) {
        if (actualValue.subtract(expectedValue).compareTo(DELTA) > 0) {
            Assert.fail(String.format("Actual value [%s] not equal to expected value [%s]", actualValue, expectedValue));
        }
    }

    public static void assertEquals(long expectedValue, BigDecimal actualValue) {
        assertEquals(new BigDecimal(expectedValue), actualValue);
    }

    public static void assertEquals(double expectedValue, BigDecimal actualValue) {
        assertEquals(new BigDecimal(expectedValue), actualValue);
    }

    public static void assertEquals(double expectedValue, double actualValue) {
        assertEquals(new BigDecimal(expectedValue), new BigDecimal(actualValue));
    }

    public static Transaction createTransaction(int id, long date, TransactionType type, String symbol, int noOfShares, double price, double cost) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setDate(date);
        tx.setType(type);
        tx.setSymbol(symbol);
        tx.setNoOfShares(new BigDecimal(noOfShares));
        tx.setPrice(new BigDecimal(price));
        tx.setCost(new BigDecimal(cost));
        return tx;
    }
}
