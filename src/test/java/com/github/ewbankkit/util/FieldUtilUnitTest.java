/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FieldUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:04 FieldUtilUnitTest.java NSI";

    @Test
    public void getFieldTest1() {
        assertNotNull(FieldUtil.getField(A.class, "i"));
        assertNull(FieldUtil.getField(A.class, "s"));
    }

    @Test
    public void getFieldTest2() {
        assertNotNull(FieldUtil.getField(new B(), "i"));
        assertNull(FieldUtil.getField(new B(), "s"));
    }

    @Test
    public void getFieldTest3() {
        assertNotNull(FieldUtil.getField(new C(), "i"));
        assertNotNull(FieldUtil.getField(new C(), "s"));
    }

    @Test
    public void getFieldTest4() {
        assertNotNull(FieldUtil.getField(D.class, "i"));
        assertNotNull(FieldUtil.getField(D.class, "s"));
    }

    public abstract class A {
        public int i;
    }

    public class B extends A {}

    public class C extends B {
        public String s;
    }

    public class D extends C {}
}
