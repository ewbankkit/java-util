//
// Copyright (C) Capital One Labs.
//

package com.capitalonelabs.eucalyptus.ledger;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * BigDecimal utilities.
 */
public final class BigDecimalUtils {
    public static BigDecimal forCurrency(double d, Currency currency) {
        int powerOfTen = 1;
        for (int i = 0; i < Objects.requireNonNull(currency).getDefaultFractionDigits(); i++) {
            powerOfTen *= 10;
        }
        long l = (long)(d * (double)powerOfTen);
        return new BigDecimal(l).divide(new BigDecimal(powerOfTen));
    }
}
