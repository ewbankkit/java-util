/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Holds a value of type T.
 */
public final class Holder<T> extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:54 Holder.java NSI";

    private static final long serialVersionUID = 1L;

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
