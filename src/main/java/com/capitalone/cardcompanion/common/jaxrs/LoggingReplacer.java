//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.capitalone.cardcompanion.common.StringReplacer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class LoggingReplacer {
    private final StringReplacer stringReplacer = new StringReplacer("jaxrs.loggingReplacements.json");

    /**
     * Constructor.
     */
    private LoggingReplacer() {}

    /**
     * Returns the single instance.
     */
    static LoggingReplacer getInstance() {
         return LazyHolder.INSTANCE;
    }

    String replaceAll(@Nullable String original) {
        return stringReplacer.replaceAll(original);
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        static final LoggingReplacer INSTANCE = new LoggingReplacer();
    }
}
