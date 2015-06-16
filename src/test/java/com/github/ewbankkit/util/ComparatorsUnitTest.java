/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class ComparatorsUnitTest {
    private final Comparator<Date> dateNullLargestComparator = Comparators.newNullLargestComparator();
    private final Comparator<Date> dateNullSmallestComparator = Comparators.newNullSmallestComparator();
    private final Comparator<String> stringSimpleComparator = Comparators.newSimpleComparator();

    @Test
    public void simpleStringComparatorTest1() {
        String s1 = "Hello";
        String s2 = "Hello";

        assertEquals(0, stringSimpleComparator.compare(s1, s2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void simpleStringComparatorTest2() {
        String s1 = "Hello";
        String s2 = null;

        stringSimpleComparator.compare(s1, s2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void simpleStringComparatorTest3() {
        String s1 = null;
        String s2 = "Hello";

        stringSimpleComparator.compare(s1, s2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void simpleStringComparatorTest4() {
        String s1 = null;
        String s2 = null;

        stringSimpleComparator.compare(s1, s2);
    }

    @Test
    public void simpleStringComparatorTest5() {
        String s1 = "";
        String s2 = "Hello";

        assertTrue(stringSimpleComparator.compare(s1, s2) < 0);
    }

    @Test
    public void caseSensitiveStringComparatorTest1() {
        String s1 = "Hello";
        String s2 = "Hello";

        assertTrue("Case-sensitive string compare equals", Comparators.CASE_SENSITIVE_STRING_COMPARATOR.compare(s1, s2) == 0);
    }

    @Test
    public void caseSensitiveStringComparatorTest2() {
        String s1 = "Hello";
        String s2 = null;

        assertTrue("Case-sensitive string compare greater than", Comparators.CASE_SENSITIVE_STRING_COMPARATOR.compare(s1, s2) > 0);
    }

    @Test
    public void caseSensitiveStringComparatorTest3() {
        String s1 = null;
        String s2 = "Hello";

        assertTrue("Case-sensitive string compare less than", Comparators.CASE_SENSITIVE_STRING_COMPARATOR.compare(s1, s2) < 0);
    }

    @Test
    public void caseSensitiveStringComparatorTest4() {
        String s1 = null;
        String s2 = null;

        assertTrue("Case-sensitive string compare equals", Comparators.CASE_SENSITIVE_STRING_COMPARATOR.compare(s1, s2) == 0);
    }

    @Test
    public void caseSensitiveStringComparatorTest5() {
        String s1 = "";
        String s2 = "Hello";

        assertTrue("Case-sensitive string compare less than", Comparators.CASE_SENSITIVE_STRING_COMPARATOR.compare(s1, s2) < 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest1() {
        String s1 = "Hello";
        String s2 = "Hello";

        assertTrue("Case-insensitive string compare equals", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) == 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest2() {
        String s1 = "HellO";
        String s2 = "hELLo";

        assertTrue("Case-insensitive string compare equals", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) == 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest3() {
        String s1 = "Hello";
        String s2 = null;

        assertTrue("Case-insensitive string compare greater than", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) > 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest4() {
        String s1 = null;
        String s2 = "Hello";

        assertTrue("Case-insensitive string compare less than", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) < 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest5() {
        String s1 = null;
        String s2 = null;

        assertTrue("Case-insensitive string compare equals", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) == 0);
    }

    @Test
    public void caseInsensitiveStringComparatorTest6() {
        String s1 = "";
        String s2 = "Hello";

        assertTrue("Case-insensitive string compare less than", Comparators.CASE_INSENSITIVE_STRING_COMPARATOR.compare(s1, s2) < 0);
    }

    @Test
    public void dateComparatorTest1() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis);
        Date d2 = new Date(currentTimeMillis + 100L);

        assertTrue("Date compare less than", dateNullSmallestComparator.compare(d1, d2) < 0);
    }

    @Test
    public void dateComparatorTest2() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis + 1000L);
        Date d2 = new Date(currentTimeMillis - 1000L);

        assertTrue("Date compare greater than", dateNullSmallestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest3() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis);
        Date d2 = new Date(currentTimeMillis);

        assertTrue("Date compare equals", dateNullSmallestComparator.compare(d1, d2) == 0);
    }

    @Test
    public void dateComparatorTest4() {
        Date d1 = null;
        Date d2 = null;

        assertTrue("Date compare equals", dateNullSmallestComparator.compare(d1, d2) == 0);
    }

    @Test
    public void dateComparatorTest5() {
        Date d1 = new Date();
        Date d2 = null;

        assertTrue("Date compare greater than", dateNullSmallestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest6() {
        Date d1 = new Date(0L);
        Date d2 = null;

        assertTrue("Date compare greater than", dateNullSmallestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest7() {
        Date d1 = null;
        Date d2 = new Date(0L);

        assertTrue("Date compare less than", dateNullSmallestComparator.compare(d1, d2) < 0);
    }

    @Test
    public void dateComparatorTest8() {
        Date zero = new Date(0L);
        Date now = new Date();
        List<Date> dates = Arrays.asList(zero, null, now);
        Collections.sort(dates, dateNullSmallestComparator);

        assertEquals("dates[0]", null, dates.get(0));
        assertEquals("dates[1]", zero, dates.get(1));
        assertEquals("dates[2]", now, dates.get(2));
    }

    @Test
    public void dateComparatorTest9() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis);
        Date d2 = new Date(currentTimeMillis + 100L);

        assertTrue("Date compare less than", dateNullLargestComparator.compare(d1, d2) < 0);
    }

    @Test
    public void dateComparatorTest10() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis + 1000L);
        Date d2 = new Date(currentTimeMillis - 1000L);

        assertTrue("Date compare greater than", dateNullLargestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest11() {
        long currentTimeMillis = System.currentTimeMillis();
        Date d1 = new Date(currentTimeMillis);
        Date d2 = new Date(currentTimeMillis);

        assertTrue("Date compare equals", dateNullLargestComparator.compare(d1, d2) == 0);
    }

    @Test
    public void dateComparatorTest12() {
        Date d1 = null;
        Date d2 = null;

        assertTrue("Date compare equals", dateNullLargestComparator.compare(d1, d2) == 0);
    }

    @Test
    public void dateComparatorTest13() {
        Date d1 = null;
        Date d2 = new Date();

        assertTrue("Date compare greater than", dateNullLargestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest14() {
        Date d1 = null;
        Date d2 = new Date(0L);

        assertTrue("Date compare greater than", dateNullLargestComparator.compare(d1, d2) > 0);
    }

    @Test
    public void dateComparatorTest15() {
        Date d1 = new Date(0L);
        Date d2 = null;

        assertTrue("Date compare less than", dateNullLargestComparator.compare(d1, d2) < 0);
    }

    @Test
    public void dateComparatorTest16() {
        Date zero = new Date(0L);
        Date now = new Date();
        List<Date> dates = Arrays.asList(zero, null, now);
        Collections.sort(dates, dateNullLargestComparator);

        assertEquals("dates[0]", zero, dates.get(0));
        assertEquals("dates[1]", now, dates.get(1));
        assertEquals("dates[2]", null, dates.get(2));
    }
}
