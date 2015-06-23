//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.base.AbstractInstant;

import javax.annotation.Nullable;

/**
 * Static utility methods pertaining to Date instances.
 */
public final class Dates {
    private Dates() {}

    @Nullable
    public static java.util.Date from(@Nullable AbstractInstant abstractInstant) {
        return (abstractInstant == null) ? null : abstractInstant.toDate();
    }

    @Nullable
    public static java.util.Date from(@Nullable LocalDate localDate) {
        return (localDate == null) ? null : localDate.toDate();
    }

    @Nullable
    public static DateTime toDateTime(@Nullable Object instant) {
        return (instant == null) ? null : new DateTime(instant);
    }

    @Nullable
    public static LocalDate toLocalDate(@Nullable Object instant) {
        return (instant == null) ? null : new LocalDate(instant);
    }

    @Nullable
    public static java.sql.Date toSqlDate(@Nullable LocalDate localDate) {
        return (localDate == null) ? null : new java.sql.Date(localDate.toDate().getTime());
    }
}
