/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.netsol.adagent.util.beans.Singleton;

public class SingletonUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:57 SingletonUnitTest.java NSI";

    @Test
    public void singletonTest1() {
        Singleton<String> singleton = Singleton.from("Hello");
        assertEquals("Hello", singleton.getFirst());
    }

    @Test
    public void singletonTest2() {
        Singleton<String> singleton1 = Singleton.from("Hello");
        assertTrue(singleton1.equals(singleton1));
    }

    @Test
    public void singletonTest3() {
        Singleton<String> singleton1 = Singleton.from("Hello");
        Singleton<String> singleton2 = Singleton.from("Hello");
        assertTrue(singleton1.equals(singleton2));
    }

    @Test
    public void singletonTest4() {
        Singleton<String> singleton1 = Singleton.from("Hello");
        Singleton<Integer> singleton2 = Singleton.from(Integer.valueOf(2));
        assertFalse(singleton1.equals(singleton2));
    }

    @Test
    public void singletonTest5() {
        Singleton<String> singleton1 = Singleton.from("Hello");
        assertFalse(singleton1.equals(Singleton.from(null)));
    }

    @Test
    public void singletonTest6() {
        assertTrue(Singleton.from(null).equals(Singleton.from(null)));
    }

    @Test
    public void singletonTest7() {
        Singleton<Object> singleton = Singleton.from(null);
        assertTrue(singleton.toArray().length == 1);
        assertArrayEquals(new Object[] {null}, singleton.toArray());
    }

    @Test
    public void singletonTest8() {
        Singleton<String> singleton = Singleton.from("boop");
        assertTrue(singleton.toArray().length == 1);
        assertArrayEquals(new Object[] {"boop"}, singleton.toArray());
    }

    @Test
    public void singletonTest9() {
        Singleton<Object> singleton = Singleton.from(null);
        assertTrue(singleton.toList().size() == 1);
        assertEquals(null, singleton.toList().get(0));
    }

    @Test
    public void singletonTest10() {
        Singleton<String> singleton = Singleton.from("boop");
        assertTrue(singleton.toList().size() == 1);
        assertEquals("boop", singleton.toList().get(0));
    }

    @Test
    public void singletonTest11() {
        Singleton<String> singleton = Singleton.from("boop");
        assertEquals("boop", singleton.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void singletonTest12() {
        Singleton.from("boop").get(1);
    }
}
