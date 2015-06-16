/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents a singleton.
 */
public final class Singleton<First> extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:08 Singleton.java NSI";

    private static final long serialVersionUID = 1L;

    private static final Singleton<Object> SINGLETON_OF_NULL = new Singleton<Object>(null);

    private final First first;

    /**
     * Constructor.
     */
    private Singleton(First first) {
        this.first = first;
    }

    public First getFirst() {
        return first;
    }
    
    @Override
    public int arity() {
        return 1;
    }
    
    @SuppressWarnings("unchecked")
    public static <First> Singleton<First> from(First first) {
        if (first == null) {
            return (Singleton<First>)SINGLETON_OF_NULL;
        }
        return new Singleton<First>(first);
    }
    
    @Override
    public Object get(int index) {
        switch (index) {
            case 0: return first;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Singleton)) {
            return false;
        }

        return equals(this, (Singleton)o);
    }
}
