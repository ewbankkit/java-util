/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.util.Collections;
import java.util.Map;

import com.netsol.adagent.util.beans.Pair;

public class MapBuilder<K, V> {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:11 MapBuilder.java NSI";

    private final Factory<Map<K, V>> factory;
    private final Map<K, V> map;

    public MapBuilder() {
        this.factory = Factories.newHashMapFactory();
        map = factory.newInstance();
    }

    public MapBuilder(Factory<Map<K, V>> factory) {
        this.factory = factory;
        map = factory.newInstance();
    }

    public MapBuilder<K, V> put(Pair<? extends K, ? extends V> keyValuePair) {
        return put(keyValuePair.getFirst(), keyValuePair.getSecond());
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(Map<? extends K,? extends V> map) {
        this.map.putAll(map);
        return this;
    }

    public MapBuilder<K, V> putAll(Iterable<Pair<K, V>> keyValuePairs) {
        for (Pair<? extends K, ? extends V> keyValuePair : keyValuePairs) {
            put(keyValuePair);
        }
        return this;
    }

    public Map<K, V> map() {
        return map;
    }

    public Map<K, V> unmodifiableMap() {
        return Collections.unmodifiableMap(map());
    }
}
