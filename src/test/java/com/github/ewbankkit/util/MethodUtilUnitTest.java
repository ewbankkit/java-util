/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MethodUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:05 MethodUtilUnitTest.java NSI";

    @Test
    public void hasOverridenMethodTest1() {
        assertFalse(MethodUtil.hasOverridenMethod(A.class, A.class, "f", String.class));
    }

    @Test
    public void hasOverridenMethodTest2() {
        assertFalse(MethodUtil.hasOverridenMethod(A.class, B.class, "f", String.class));
    }

    @Test
    public void hasOverridenMethodTest3() {
        assertTrue(MethodUtil.hasOverridenMethod(A.class, C.class, "f", String.class));
    }

    @Test
    public void hasOverridenMethodTest4() {
        assertTrue(MethodUtil.hasOverridenMethod(A.class, D.class, "f", String.class));
    }

    @Test
    public void hasOverridenMethodTest5() {
        assertTrue(MethodUtil.hasOverridenMethod(B.class, C.class, "f", String.class));
    }

    @Test
    public void hasOverridenMethodTest6() {
        assertFalse(MethodUtil.hasOverridenMethod(C.class, D.class, "f", String.class));
    }

    public abstract class A {
        protected void f(String s) {}
    }

    public class B extends A {}

    public class C extends B {
        @Override
        protected void f(String s) {}
    }

    public class D extends C {}
}
