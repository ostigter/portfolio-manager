package org.ozsoft.portfoliomanager.util;

import java.math.BigDecimal;
import java.math.MathContext;

public abstract class MathUtils {

    public static final BigDecimal MINUS_ONE = new BigDecimal(-1);

    public static final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * Private constructor to deny instantation.
     */
    private MathUtils() {
        // Empty.
    }

    /**
     * Returns the percentage of one decimal value of another.
     *
     * @param arg1
     *            First value.
     * @param arg2
     *            Second value.
     *
     * @return The percentage.
     */
    public static BigDecimal perc(BigDecimal arg1, BigDecimal arg2) {
        if (arg2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        } else {
            return arg1.divide(arg2, MathContext.DECIMAL64).multiply(HUNDRED);
        }
    }

    public static BigDecimal negate(BigDecimal value) {
        return value.multiply(MINUS_ONE);
    }

    /**
     * Returns the absolute distance (ABS) between two decimal values.
     *
     * @param arg1
     *            First value.
     * @param arg2
     *            Second value.
     * @return The absolute distance.
     */
    public static BigDecimal abs(BigDecimal arg1, BigDecimal arg2) {
        BigDecimal diff = arg1.subtract(arg2, MathContext.DECIMAL64);
        return diff.abs(MathContext.DECIMAL64);
    }
}
