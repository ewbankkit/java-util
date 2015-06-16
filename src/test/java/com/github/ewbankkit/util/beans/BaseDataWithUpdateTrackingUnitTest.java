/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BaseDataWithUpdateTrackingUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:55 BaseDataWithUpdateTrackingUnitTest.java NSI";

    @Test
    public void baseDataWithUpdateTrackingTest1() {
        A a = new A();
        a.setAa(1);
        assertEquals(1, a.getAa());
        a.setAb("aabb");
        assertEquals("aabb", a.getAb());
    }

    @Test
    public void baseDataWithUpdateTrackingTest2() {
        A a = new A();
        a.setAa(-1);
        assertTrue(a.hasTrackedUpdates());
        a.clearTrackedUpdates();
        assertFalse(a.hasTrackedUpdates());
        a.setAb("bbaa");
        assertTrue(a.hasTrackedUpdates());
        a.clearTrackedUpdates();
        a.setAb("bbaa");
        assertTrue(a.hasTrackedUpdates());
    }

    @Test
    public void baseDataWithUpdateTrackingTest3() {
        A a = new A();
        a.setAa(1);
        a.setAb("aabb");
        CharSequence updateValuesSnippet = a.getUpdateValuesSnippet();
        assertEquals("a_a = ?, a_b = ?", updateValuesSnippet.toString());
    }

    @Test
    public void baseDataWithUpdateTrackingTest4() {
        B b = new B();
        b.setBc('x');
        assertEquals('x', b.getBc());
        assertTrue(b.hasTrackedUpdates());
        b.clearTrackedUpdates();
        b.setBc('y');
        assertTrue(b.hasTrackedUpdates());
        b.clearTrackedUpdates();
        b.setBc('y');
        assertFalse(b.hasTrackedUpdates());
    }

    @Test
    public void baseDataWithUpdateTrackingTest5() {
        C c = new C();
        c.setCd(1.1D);
        assertEquals(1.1D, c.getCd(), 0D);
        c.setBc('5');
        assertEquals('5', c.getBc());
        CharSequence updateValuesSnippet = c.getUpdateValuesSnippet();
        assertEquals("c_d = ?, b_c = ?", updateValuesSnippet.toString());
    }

    @Test
    public void baseDataWithUpdateTrackingTest6() {
        D d = new D();
        d.setDf(-2.2F);
        assertEquals(-2.2F, d.getDf(), 0F);
        d.setBc('6');
        d.setCd(99D);
        CharSequence updateValuesSnippet = d.getUpdateValuesSnippet();
        assertEquals("d_f = ?, xyz = ?", updateValuesSnippet.toString());
    }

    private static class A extends BaseDataWithUpdateTracking {
        @ColumnName("a_a")
        private int aa;
        @ColumnName("a_b")
        private String ab;

        public void setAa(int aa) {
            setTrackedField("aa", aa);
        }

        public int getAa() {
            return aa;
        }

        public void setAb(String ab) {
            setTrackedField("ab", ab);
        }

        public String getAb() {
            return ab;
        }
    }

    private static class B extends BaseDataWithUpdateTracking {
        @ColumnName("b_c")
        private char bc;

        public B() {
            super(true); // Check for equality when evaluating whether a tracked field has changed.
        }

        public void setBc(char bc) {
            setTrackedField("bc", bc);
        }

        public char getBc() {
            return bc;
        }
    }

    private static class C extends B {
        @ColumnName("c_d")
        private double cd;

        public void setCd(double cd) {
            setTrackedField("cd", cd);
        }

        public double getCd() {
            return cd;
        }
    }

    @ColumnNameOverride({"bc/xyz", "cd/"})
    private static class D extends C {
        @ColumnName("d_f")
        private float df;

        public void setDf(float df) {
            setTrackedField("df", df);
        }

        public float getDf() {
            return df;
        }
    }
}
