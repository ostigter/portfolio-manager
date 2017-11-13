package org.ozsoft.portfoliomanager.util;

import java.math.BigDecimal;
import java.math.MathContext;

public abstract class MathUtils {

    public static final BigDecimal MINUS_ONE = new BigDecimal(-1);

    public static final BigDecimal HUNDRED = new BigDecimal(100);

    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    /**
     * Private constructor to deny instantation.
     */
    private MathUtils() {
        // Empty.
    }

    /**
     * Indicates whether a decimal value is exactly 0.
     * 
     * @param value
     *            The value.
     * 
     * @return {@code true} if exactly 0, otherwise {@code false}.
     */
    public static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Safely divides a decimal value with another. <br />
     * <br />
     * 
     * If the divider is exactly 0, 0 is returned.
     * 
     * @param arg1
     *            The first value.
     * @param arg2
     *            The second value.
     * 
     * @return The division.
     */
    public static BigDecimal divide(BigDecimal arg1, BigDecimal arg2) {
        if (isZero(arg2)) {
            return BigDecimal.ZERO;
        } else {
            return arg1.divide(arg2, MATH_CONTEXT);
        }
    }

    /**
     * Returns the percentage of one decimal value of another. <br />
     * <br />
     * 
     * If the divider is exactly 0, 0 is returned.
     * 
     * @param arg1
     *            First value.
     * @param arg2
     *            Second value.
     *
     * @return The percentage.
     */
    public static BigDecimal perc(BigDecimal arg1, BigDecimal arg2) {
        if (isZero(arg2)) {
            return BigDecimal.ZERO;
        } else {
            return arg1.divide(arg2, MATH_CONTEXT).multiply(HUNDRED);
        }
    }

    /**
     * Returns the percentage change between two values.
     * 
     * @param oldValue
     *            The old value.
     * @param newValue
     *            The new value.
     * 
     * @return The percentage change.
     */
    public static BigDecimal percChange(BigDecimal oldValue, BigDecimal newValue) {
        if (isZero(oldValue) || isZero(newValue)) {
            return BigDecimal.ZERO;
        } else {
            return newValue.subtract(oldValue).divide(oldValue, MATH_CONTEXT).multiply(HUNDRED);
        }
    }

    /**
     * Returns the negation of a decimal value.
     * 
     * @param value
     *            The value.
     * 
     * @return The negated value.
     */
    public static BigDecimal negate(BigDecimal value) {
        return value.multiply(MINUS_ONE, MATH_CONTEXT);
    }

    /**
     * Returns the absolute distance (ABS) between two decimal values.
     *
     * @param arg1
     *            First value.
     * @param arg2
     *            Second value.
     * 
     * @return The absolute distance.
     */
    public static BigDecimal abs(BigDecimal arg1, BigDecimal arg2) {
        BigDecimal diff = arg1.subtract(arg2, MATH_CONTEXT);
        return diff.abs(MATH_CONTEXT);
    }
}
