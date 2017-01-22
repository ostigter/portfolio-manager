package org.ozsoft.portfoliomanager.util;

import java.math.BigDecimal;

import org.junit.Test;
import org.ozsoft.portfoliomanager.test.TestUtils;

public class MathUtilsTest {

    @Test
    public void abs() {
        TestUtils.assertEquals(1, MathUtils.abs(new BigDecimal("1"), new BigDecimal("2")));
        TestUtils.assertEquals(1, MathUtils.abs(new BigDecimal("2"), new BigDecimal("1")));
        TestUtils.assertEquals(1, MathUtils.abs(new BigDecimal("-1"), new BigDecimal("-2")));
        TestUtils.assertEquals(1, MathUtils.abs(new BigDecimal("-2"), new BigDecimal("-1")));
        TestUtils.assertEquals(new BigDecimal("0.8"), MathUtils.abs(new BigDecimal("0.1"), new BigDecimal("0.9")));
        TestUtils.assertEquals(new BigDecimal("0.8"), MathUtils.abs(new BigDecimal("0.9"), new BigDecimal("0.8")));
    }
}
