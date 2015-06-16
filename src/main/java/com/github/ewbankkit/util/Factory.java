/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

public interface Factory<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:58 Factory.java NSI";

    public abstract T newInstance();
}
