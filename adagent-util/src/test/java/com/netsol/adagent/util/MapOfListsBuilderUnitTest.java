/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.netsol.adagent.util.Factory;
import com.netsol.adagent.util.MapOfListsBuilder;
import com.netsol.adagent.util.beans.Pair;

public class MapOfListsBuilderUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:05 MapOfListsBuilderUnitTest.java NSI";

    @Test
    public void mapOfListsBuilderTest1() {
        MapOfListsBuilder<String, String> mb = new MapOfListsBuilder<String, String>();
        assertEquals(0, mb.map().size());
    }

    @Test
    public void mapOfListsBuilderTest2() {
        MapOfListsBuilder<String, String> mb = new MapOfListsBuilder<String, String>();
        mb.put("A", "Apple");
        Map<String, List<String>> map = mb.map();
        assertEquals(1, map.size());
        assertEquals(1, map.get("A").size());
        assertEquals("Apple", map.get("A").get(0));
    }

    @Test
    public void mapOfListsBuilderTest3() {
        MapOfListsBuilder<String, String> mb = new MapOfListsBuilder<String, String>();
        mb.put("A", "Apple");
        mb.put(Pair.from("B", "Beer"));
        mb.put("A", "Avacado");
        Map<String, List<String>> map = mb.map();
        assertEquals(2, map.size());
        assertEquals(2, map.get("A").size());
        assertEquals(1, map.get("B").size());
        assertEquals("Apple", map.get("A").get(0));
        assertEquals("Avacado", map.get("A").get(1));
        assertEquals("Beer", map.get("B").get(0));
    }

    @Test
    public void mapOfListsBuilderTest4() {
        MapOfListsBuilder<String, String> mb = new MapOfListsBuilder<String, String>();
        mb.put("A", "Apple");
        Map<String, List<String>> map = mb.map();
        mb.put(Pair.from("B", "Beer"));
        mb.put("A", "Avacado");
        assertEquals(2, map.size());
        assertEquals(2, map.get("A").size());
        assertEquals("Apple", map.get("A").get(0));
        map = mb.map();
        assertEquals(2, map.size());
        assertEquals(2, map.get("A").size());
        assertEquals("Apple", map.get("A").get(0));
        assertEquals("Beer", map.get("B").get(0));
    }

    @Test
    public void mapOfListsBuilderTest5() {
        MapOfListsBuilder<String, String> mb = new MapOfListsBuilder<String, String>(
                new Factory<Map<String, List<String>>>() {
                    public Map<String, List<String>> newInstance() {
                        return new LinkedHashMap<String, List<String>>();
                    }},
                new Factory<List<String>>() {
                    public List<String> newInstance() {
                        return new LinkedList<String>();
                    }});
        mb.put("A", "Apple");
        Map<String, List<String>> map = mb.map();
        mb.put(Pair.from("B", "Beer"));
        mb.put("A", "Avacado");
        assertEquals(2, map.size());
        assertEquals(2, map.get("A").size());
        assertEquals("Apple", map.get("A").get(0));
        map = mb.map();
        assertEquals(2, map.size());
        assertEquals(2, map.get("A").size());
        assertEquals("Apple", map.get("A").get(0));
        assertEquals("Beer", map.get("B").get(0));
    }
}
