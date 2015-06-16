/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:58 UnitUnitTest.java NSI";

    @Test
    public void unitTest1() {
        assertTrue(Unit.UNIT.equals(Unit.UNIT));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void unitTest2() {
        Unit.UNIT.get(1);
    }

    @Test
    public void unitTest3() {
        assertTrue(Unit.UNIT.toArray().length == 0);
        assertArrayEquals(new Object[0], Unit.UNIT.toArray());
    }

    @Test
    public void unitTest4() {
        assertTrue(Unit.UNIT.toList().isEmpty());
    }
}
