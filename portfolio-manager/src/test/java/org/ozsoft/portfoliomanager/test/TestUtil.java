package org.ozsoft.portfoliomanager.test;

import org.ozsoft.portfoliomanager.domain.Transaction;
import org.ozsoft.portfoliomanager.domain.TransactionType;

public class TestUtil {

    /**
     * Private constructor to deny instantiation.
     */
    private TestUtil() {
        // Empty implementation.
    }

    public static Transaction createTransaction(int id, long date, TransactionType type, String symbol, int noOfShares, double price, double cost) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setDate(date);
        tx.setType(type);
        tx.setSymbol(symbol);
        tx.setNoOfShares(noOfShares);
        tx.setPrice(price);
        tx.setCost(cost);
        return tx;
    }
}
