/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a tuple.
 */
public abstract class Tuple extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.18.0.latest 09/07/12 08:38:01 Tuple.java NSI";

    private transient volatile Iterable<Object> iterable;
    private transient volatile List<Object> list;

    public abstract int arity();
    public abstract Object get(int index);

    @Override
    public int hashCode() {
        int result = 17;
        for (Object o : getIterable()) {
            int c = (o == null) ? 0 : o.hashCode();
            result = 31 * result + c;
        }
        return result;
    }

    public Object[] toArray() {
        return toList().toArray();
    }

    public List<Object> toList() {
        // Use the single-check idiom.
        List<Object> list = this.list;
        if (list == null) {
            this.list = list = newList();
        }
        return list;
    }

    protected static <T extends Tuple> boolean equals(T lhs, T rhs) {
        int arity = lhs.arity();
        for (int i = 0; i < arity; i++) {
            if (!objectsEqual(lhs.get(i), rhs.get(i))) {
                return false;
            }
        }
        return true;
    }

    private Iterable<Object> getIterable() {
        // Use the single-check idiom.
        Iterable<Object> iterable = this.iterable;
        if (iterable == null) {
            this.iterable = iterable = new Iterable<Object>() {
                public Iterator<Object> iterator() {
                    return new Iterator<Object>() {
                        private int index;

                        public boolean hasNext() {
                            return (index < arity());
                        }

                        public Object next() {
                            if (!hasNext()) {
                                throw new NoSuchElementException();
                            }
                            return get(index++);
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }};
                }};
        }
        return iterable;
    }

    private List<Object> newList() {
        List<Object> list = new ArrayList<Object>(arity());
        for (Object o : getIterable()) {
            list.add(o);
        }
        return Collections.unmodifiableList(list);
    }
}
