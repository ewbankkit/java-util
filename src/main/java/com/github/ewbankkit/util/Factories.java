/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Factories {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:58 Factories.java NSI";

    /**
     * Return a new array list factory.
     */
    public static <T> Factory<List<T>> newArrayListFactory() {
        return new Factory<List<T>>() {
            public List<T> newInstance() {
                return new ArrayList<T>();
            }};
    }

    /**
     * Return a new hash map factory.
     */
    public static <K, V> Factory<Map<K, V>> newHashMapFactory() {
        return new Factory<Map<K, V>>() {
            public Map<K, V> newInstance() {
                return new HashMap<K, V>();
            }};
    }

    /**
     * Return a new hash map factory.
     */
    public static <K, V> Factory<Map<K, V>> newLinkedHashMapFactory() {
        return new Factory<Map<K, V>>() {
            public Map<K, V> newInstance() {
                return new LinkedHashMap<K, V>();
            }};
    }

    /**
     * Return a new array list factory.
     */
    public static <T> Factory<List<T>> newLinkedListFactory() {
        return new Factory<List<T>>() {
            public List<T> newInstance() {
                return new LinkedList<T>();
            }};
    }

    private Factories() {}
}
