/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.netsol.adagent.util.Factory;
import com.netsol.adagent.util.MapBuilder;
import com.netsol.adagent.util.beans.Pair;

public class MapBuilderUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:05 MapBuilderUnitTest.java NSI";

    @Test
    public void mapBuilderTest1() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>();
        assertTrue(mb.map().size() == 0);
    }

    @Test
    public void mapBuilderTest2() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>();
        mb.put("A", "Apple");
        Map<String, String> map = mb.map();
        assertTrue(map.size() == 1);
        assertEquals("Apple", map.get("A"));
    }

    @Test
    public void mapBuilderTest3() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>();
        mb.put("A", "Apple");
        mb.put(Pair.from("B", "Beer"));
        Map<String, String> map = mb.map();
        assertTrue(map.size() == 2);
        assertEquals("Apple", map.get("A"));
        assertEquals("Beer", map.get("B"));
    }

    @Test
    public void mapBuilderTest4() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>();
        mb.put("A", "Apple");
        Map<String, String> map = mb.map();
        mb.put(Pair.from("B", "Beer"));
        assertTrue(map.size() == 2);
        assertEquals("Apple", map.get("A"));
        map = mb.map();
        assertTrue(map.size() == 2);
        assertEquals("Apple", map.get("A"));
        assertEquals("Beer", map.get("B"));
    }

    @Test
    public void mapBuilderTest5() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("A", "Apple");
        map.put("B", "Beer");
        MapBuilder<String, String> mb = new MapBuilder<String, String>();
        mb.putAll(map);
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        list.add(Pair.from("C", "Coffee"));
        mb.putAll(list);
        map = mb.map();
        assertTrue(map.size() == 3);
        assertEquals("Apple", map.get("A"));
        assertEquals("Beer", map.get("B"));
        assertEquals("Coffee", map.get("C"));
    }

    @Test
    public void mapBuilderTest6() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>(new Factory<Map<String, String>>() {
            public Map<String, String> newInstance() {
                return new LinkedHashMap<String, String>();
            }});
        mb.put("A", "Apple");
        Map<String, String> map = mb.map();
        assertTrue(map.size() == 1);
        assertEquals("Apple", map.get("A"));
    }

    @Test
    public void mapBuilderTest7() {
        MapBuilder<String, String> mb = new MapBuilder<String, String>(new Factory<Map<String, String>>() {
            public Map<String, String> newInstance() {
                return new TreeMap<String, String>();
            }});
        mb.put("A", "Apple");
        Map<String, String> map = mb.map();
        mb.put(Pair.from("B", "Beer"));
        assertTrue(map.size() == 2);
        assertEquals("Apple", map.get("A"));
        map = mb.map();
        assertTrue(map.size() == 2);
        assertEquals("Apple", map.get("A"));
        assertEquals("Beer", map.get("B"));
    }
}
