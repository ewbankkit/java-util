/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.netsol.adagent.util.ProxyFactory;

public class ProxyFactoryUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:06 ProxyFactoryUnitTest.java NSI";

    private static final InvocationHandler gooInvocationHandler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(new Goo(), args);
        }};
    private static final InvocationHandler hooInvocationHandler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(new Hoo(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }};

    @Test
    public void test1() throws Exception {
        Foo foo = ProxyFactory.newProxy(Foo.class, gooInvocationHandler);
        assertEquals("42", foo.a(41));
        assertEquals(42, foo.b("41"));
    }

    @Test(expected=FooException.class)
    public void test2() throws Exception {
        Foo foo = ProxyFactory.newProxy(Foo.class, hooInvocationHandler);
        assertEquals("42", foo.a(41));
    }

    private static interface Foo {
        public abstract String a(int i) throws Exception;
        public abstract int b(String s) throws Exception;
    }

    private static class Goo implements Foo {
        public String a(int i) throws Exception {
            return Integer.toString(i + 1);
        }

        public int b(String s) throws Exception {
            return Integer.parseInt(s) + 1;
        }
    }

    private static class Hoo implements Foo {
        public String a(int i) throws Exception {
            throw new FooException();
        }

        public int b(String s) throws Exception {
            throw new FooException();
        }
    }

    @SuppressWarnings("serial")
    private static class FooException extends Exception {}
}
