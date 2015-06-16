/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.stringIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotBlank;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.netsol.adagent.util.Predicate;

public class PredicateUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:05 PredicateUnitTest.java NSI";

    private final static Predicate<String> IS_EMPTY = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return stringIsEmpty(s);
        }};

    private final static Predicate<String> STARTS_WITH_LETTER = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return stringIsNotBlank(s) && Character.isLetter(s.charAt(0));
        }};

    @Test
    public void predicateTest1() {
        assertTrue(Predicate.alwaysTrue().apply(null));
    }

    @Test
    public void predicateTest2() {
        assertFalse(Predicate.alwaysFalse().apply(null));
    }

    @Test
    public void predicateTest3() {
        assertFalse(Predicate.alwaysTrue().not().apply(null));
    }

    @Test
    public void predicateTest4() {
        assertTrue(Predicate.not(Predicate.alwaysFalse()).apply(null));
    }

    @Test
    public void predicateTest5() {
        assertTrue(Predicate.isNull().apply(null));
    }

    @Test
    public void predicateTest6() {
        assertFalse(Predicate.notNull().apply(null));
    }

    @Test
    public void predicateTest7() {
        assertTrue(IS_EMPTY.apply(""));
    }

    @Test
    public void predicateTest8() {
        assertFalse(IS_EMPTY.apply("Hello"));
    }

    @Test
    public void predicateTest9() {
        assertTrue(IS_EMPTY.or(STARTS_WITH_LETTER).apply("Hello"));
    }

    @Test
    public void predicateTest10() {
        assertTrue(Predicate.or(IS_EMPTY, STARTS_WITH_LETTER).apply("Hello"));
    }

    @Test
    public void predicateTest12() {
        assertFalse(IS_EMPTY.and(STARTS_WITH_LETTER).apply("Hello"));
    }

    @Test
    public void predicateTest11() {
        assertFalse(Predicate.and(IS_EMPTY, STARTS_WITH_LETTER).apply("Hello"));
    }
}
