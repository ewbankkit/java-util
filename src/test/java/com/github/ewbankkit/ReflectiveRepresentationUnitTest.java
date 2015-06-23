//
// Kit's Java Utils.
//

package com.github.ewbankkit;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

public final class ReflectiveRepresentationUnitTest {
    @Test
    public void test1() {
        Inner1 inner1 = new Inner1();
        inner1.setA(new int[] {1, 2, 3});
        inner1.setS("A STRING");
        String s = inner1.toString();
        Assert.assertNotNull(s);
        Assert.assertTrue(s.length() > 0);
    }

    @Test
    public void test2() {
        Inner2 inner2 = new Inner2();
        inner2.setI(Integer.valueOf(42));
        inner2.ss = Collections.singleton("A STRING");
        String s = inner2.toString();
        Assert.assertNotNull(s);
        Assert.assertTrue(s.length() > 0);
    }

    @Test
    public void test3() {
        Inner1 inner1 = new Inner1();
        inner1.setA(new int[] {-1, 0, 1});
        inner1.setS("ANOTHER STRING");
        Inner3 inner3 = new Inner3();
        inner3.setF(12.3F);
        inner3.setInner1(inner1);
        inner3.setSb(new StringBuilder("STRING BUILDER"));
        String s = ReflectiveRepresentation.toString(inner3);
        Assert.assertNotNull(s);
        Assert.assertTrue(s.length() > 0);
    }

    public final class Inner1 {
        private int[] a;
        private String s;

        public int[] getA() {
            return a;
        }

        public void setA(int[] a) {
            this.a = a;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return ReflectiveRepresentation.toString(this);
        }
    }

    public final class Inner2 {
        public Collection<String> ss;

        private Integer i;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return ReflectiveRepresentation.toString(this);
        }
    }

    public final class Inner3 {
        private float f;
        private Inner1 inner1;
        private StringBuilder sb;

        public float getF() {
            return f;
        }

        public void setF(float f) {
            this.f = f;
        }

        public Inner1 getInner1() {
            return inner1;
        }

        public void setInner1(Inner1 inner1) {
            this.inner1 = inner1;
        }

        public StringBuilder getSb() {
            return sb;
        }

        public void setSb(StringBuilder sb) {
            this.sb = sb;
        }
    }
}
