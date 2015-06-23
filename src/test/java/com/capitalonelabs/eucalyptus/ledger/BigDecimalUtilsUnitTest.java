//
// Copyright (C) Capital One Labs.
//

package com.capitalonelabs.eucalyptus.ledger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

public final class BigDecimalUtilsUnitTest {
    private Currency usd;

    @Before
    public void before() {
        usd = Currency.getInstance("USD");
    }

    @Test
    public void test1() {
        double d = 123.45D;
        BigDecimal bd = BigDecimalUtils.forCurrency(d, usd);
        Assert.assertNotNull(bd);
        Assert.assertEquals(new BigDecimal("123.45"), bd);
    }

    @Test
    public void test2() {
        double d = -123.45D;
        BigDecimal bd = BigDecimalUtils.forCurrency(d, usd);
        Assert.assertNotNull(bd);
        Assert.assertEquals(new BigDecimal("-123.45"), bd);
    }

    @Test
    public void test3() {
        double d = 0.123D;
        BigDecimal bd = BigDecimalUtils.forCurrency(d, usd);
        Assert.assertNotNull(bd);
        Assert.assertEquals(new BigDecimal("0.12"), bd);
    }
}
