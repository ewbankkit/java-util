/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ArrayBuilder<T> {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:49 ArrayBuilder.java NSI";

    private final List<T> list = new ArrayList<T>();

    public ArrayBuilder<T> add(T t) {
        list.add(t);
        return this;
    }

    public ArrayBuilder<T> add(T... ts) {
        for (T t : ts) {
            list.add(t);
        }
        return this;
    }

    public ArrayBuilder<T> addAll(Collection<? extends T> ts) {
        list.addAll(ts);
        return this;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> classOfT) {
        return list.toArray((T[])newArray(classOfT));
    }

    public double[] toArrayOfDouble() {
        return (double[])toArrayOfNumber(double.class, new NumericElementSetter() {
            public void setElement(Object array, int index, Number number) {
                Array.setDouble(array, index, number.doubleValue());
            }
        });
    }

    public int[] toArrayOfInt() {
        return (int[])toArrayOfNumber(int.class, new NumericElementSetter() {
            public void setElement(Object array, int index, Number number) {
                Array.setInt(array, index, number.intValue());
            }
        });
    }

    public long[] toArrayOfLong() {
        return (long[])toArrayOfNumber(long.class, new NumericElementSetter() {
            public void setElement(Object array, int index, Number number) {
                Array.setLong(array, index, number.longValue());
            }
        });
    }

    private Object newArray(Class<?> clazz) {
        return Array.newInstance(clazz, list.size());
    }

    private Object toArrayOfNumber(Class<?> clazz, NumericElementSetter numericElementSetter) {
        Object array = newArray(clazz);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            if (t instanceof Number) {
                numericElementSetter.setElement(array, i, (Number)t);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }
        return array;
    }

    private static interface NumericElementSetter {
        public abstract void setElement(Object array, int index, Number number);
    }
}
