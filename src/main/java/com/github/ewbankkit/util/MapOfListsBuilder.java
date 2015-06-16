/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.ewbankkit.util.beans.Pair;

public class MapOfListsBuilder<K, V> {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:12 MapOfListsBuilder.java NSI";

    private final Factory<List<V>> listFactory;
    private final Map<K, List<V>> map;
    private final Factory<Map<K, List<V>>> mapFactory;

    public MapOfListsBuilder() {
        this.listFactory = Factories.newArrayListFactory();
        this.mapFactory = Factories.newHashMapFactory();
        map = mapFactory.newInstance();
    }

    public MapOfListsBuilder(Factory<Map<K, List<V>>> mapFactory, Factory<List<V>> listFactory) {
        this.listFactory = listFactory;
        this.mapFactory = mapFactory;
        map = mapFactory.newInstance();
    }

    public MapOfListsBuilder<K, V> put(Pair<? extends K, ? extends V> keyValuePair) {
        return put(keyValuePair.getFirst(), keyValuePair.getSecond());
    }

    public MapOfListsBuilder<K, V> put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = listFactory.newInstance();
            map.put(key, list);
        }
        list.add(value);
        return this;
    }

    public Map<K, List<V>> map() {
        return map;
    }

    public Map<K, List<V>> unmodifiableMap() {
        return Collections.unmodifiableMap(map());
    }
}
