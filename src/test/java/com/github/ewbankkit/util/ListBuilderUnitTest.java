/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class ListBuilderUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:04 ListBuilderUnitTest.java NSI";

    @Test
    public void listBuilderTest1() {
        ListBuilder<String> lb = new ListBuilder<String>();
        assertTrue(lb.list().size() == 0);
    }

    @Test
    public void listBuilderTest2() {
        ListBuilder<String> lb = new ListBuilder<String>();
        lb.add("Hello");
        List<String> list = lb.list();
        assertTrue(list.size() == 1);
        assertEquals("Hello", list.get(0));
    }

    @Test
    public void listBuilderTest3() {
        ListBuilder<String> lb = new ListBuilder<String>();
        lb.addAll(Collections.singletonList("Hello"));
        lb.add("Goodbye");
        List<String> list = lb.list();
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        assertEquals("Goodbye", list.get(1));
    }

    @Test
    public void listBuilderTest4() {
        ListBuilder<String> lb = new ListBuilder<String>(new Factory<List<String>>() {
            public List<String> newInstance() {
                return new LinkedList<String>();
            }});
        lb.addAll(Collections.singletonList("Hello"));
        lb.add("Goodbye");
        List<String> list = lb.list();
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        assertEquals("Goodbye", list.get(1));
    }

    @Test
    public void listBuilderTest5() {
        ListBuilder<String> lb = new ListBuilder<String>();
        lb.add("Hello");
        List<String> list = lb.list();
        lb.add("Goodbye");
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        list = lb.list();
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        assertEquals("Goodbye", list.get(1));
    }

    @Test
    public void listBuilderTest6() {
        ListBuilder<String> lb = new ListBuilder<String>(new Factory<List<String>>() {
            public List<String> newInstance() {
                return new LinkedList<String>();
            }});
        lb.add("Hello");
        List<String> list = lb.list();
        lb.add("Goodbye");
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        list = lb.list();
        assertTrue(list.size() == 2);
        assertEquals("Hello", list.get(0));
        assertEquals("Goodbye", list.get(1));
    }
}
