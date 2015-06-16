/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;

/**
 * The type that allows only one value (and thus can hold no information).
 */
public final class Unit extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:09 Unit.java NSI";

    /**
     * The only value.
     */
    public static final Unit UNIT = new Unit();

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    private Unit() {}

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object get(int index) {
        throw new IndexOutOfBoundsException();
    }
}
