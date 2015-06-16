/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReflectiveRepresentationUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:57 ReflectiveRepresentationUnitTest.java NSI";

    @Test
    public void test1() {
        QQQ qqq = new QQQ();
        String s = qqq.toString();
        System.out.println(s);
        assertNotNull(s);
    }

    @Test
    public void test2() {
        QQQ qqq = new QQQ();
        String s = BaseData.toString(qqq);
        System.out.println(s);
        assertNotNull(s);
    }

    @Test
    public void test3() {
        QQQ qqq = new QQQ();
        String s = BaseData.toString(new QQQ[] {qqq});
        System.out.println(s);
        assertNotNull(s);
    }

    @Test
    public void test4() {
        QQQ qqq = new QQQ();
        Map<Integer, QQQ> map = new HashMap<Integer, QQQ>();
        map.put(Integer.valueOf(42), qqq);
        String s = BaseData.toString(map);
        System.out.println(s);
        assertNotNull(s);
    }

    @SuppressWarnings("unused")
    private static class NNN {
        public String getString() {
            return "-- NO --";
        }

        @Override
        public String toString() {
            return "++ YES +++";
        }
    }

    @SuppressWarnings("unused")
    private static class OOO {
        public final Object z = Collections.enumeration(Collections.singleton(Calendar.getInstance()));
    }

    @SuppressWarnings("unused")
    private static class PPP {
        private Date d = new Date();
        private Integer i = Integer.valueOf(66);
        private OOO ooo = new OOO();

        public Date getD() {
            return d;
        }

        public Integer getI() {
            return i;
        }

        public OOO getOOO() {
            return ooo;
        }

        @Override
        public String toString() {
            return BaseData.toString(this);
        }
    }

    @SuppressWarnings("unused")
    private static class QQQ extends BaseData {
        private float[] a = new float[] {1.1F, 2.2F, 3.3F};
        private Collection<String> c = new ArrayList<String>();
        private Collection<Long> d = new ArrayList<Long>();
        private Map<Short, Object> m = new HashMap<Short, Object>();
        private int n = -123;
        private NNN nnn = new NNN();
        private PPP ppp = new PPP();
        private PPP[] ppps = new PPP[] {new PPP()};
        private String s = "Hello";
        private Pair<String, Integer> p = Pair.from("First", Integer.valueOf(2));

        public QQQ() {
            c.add("Hello");
            c.add("Goodbye");
            m.put(Short.valueOf((short)1), "Uno");
            m.put(Short.valueOf((short)3), new String[] {"Trois", "Drei"});
        }

        public float[] getA() {
            return a;
        }

        public Collection<String> getC() {
            return c;
        }

        public Collection<Long> getD() {
            return d;
        }

        public Map<Short, Object> getM() {
            return m;
        }

        public int getN() {
            return getNumber();
        }

        public NNN getNNN() {
            return nnn;
        }

        public PPP getPPP() {
            return ppp;
        }

        public PPP[] getPPPs() {
            return ppps;
        }

        public String getS() {
            return getString();
        }

        public boolean isCool() {
            return true;
        }

        protected String getString() {
            return s;
        }

        private int getNumber() {
            return n;
        }

        public Pair<String, Integer> getPair() {
            return p;
        }
    };
}
