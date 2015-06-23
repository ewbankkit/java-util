//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.base;

import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Test ReflectiveRepresentation.
 */
public final class ReflectiveRepresentationUnitTest {
    private class X {
        private final short a = 1;
        private final double b = -2.0D;
        private final String[] c = {"ABCD", "1234"};
        @SuppressWarnings("unused")
        public final List<Serializable> d = Arrays.asList(new Date(), new int[] {9, 10}, "C1");

        @SuppressWarnings("unused")
        public int getA() {
            return a;
        }

        @SuppressWarnings("unused")
        public double getB() {
            return b;
        }

        @SuppressWarnings("unused")
        public String[] getC() {
            return c;
        }
    }

    @Test
    public void testToString1() throws Exception {
        String s = ReflectiveRepresentation.toString(null);
        System.out.print(s);
        assertNotNull(s);
    }

    @Test
    public void testToString2() throws Exception {
        String s = ReflectiveRepresentation.toString(Long.valueOf(42L));
        System.out.print(s);
        assertNotNull(s);
    }

    @Test
    public void testToString3() throws Exception {
        String s = ReflectiveRepresentation.toString(new X());
        System.out.println(s);
        assertNotNull(s);
    }
}
