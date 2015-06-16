/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListBuilder<T> {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:07 ListBuilder.java NSI";

    private final Factory<List<T>> factory;
    private final List<T> list;

    public ListBuilder() {
        this.factory = Factories.newArrayListFactory();
        list = factory.newInstance();
    }

    public ListBuilder(Factory<List<T>> factory) {
        this.factory = factory;
        list = factory.newInstance();
    }

    public ListBuilder<T> add(T t) {
        list.add(t);
        return this;
    }

    public ListBuilder<T> add(T... ts) {
        for (T t : ts) {
            list.add(t);
        }
        return this;
    }

    public ListBuilder<T> addAll(Collection<? extends T> ts) {
        list.addAll(ts);
        return this;
    }

    public List<T> list() {
        return list;
    }

    public List<T> unmodifiableList() {
        return Collections.unmodifiableList(list());
    }
}
