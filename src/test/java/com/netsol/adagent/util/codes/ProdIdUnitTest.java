/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.netsol.adagent.util.codes.ProdId;

public class ProdIdUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:27 ProdIdUnitTest.java NSI";

    @Test
    public void isAdagentProduct1() {
        assertTrue(ProdId.isAdagentProduct(ProdId.DIFM_PPC));
    }

    @Test
    public void isAdagentProduct2() {
        assertFalse(ProdId.isAdagentProduct(ProdId.WEBSTATS));
    }

    @Test
    public void isPpcProduct1() {
        assertTrue(ProdId.isPpcProduct(ProdId.DIY_PPC));
    }

    @Test
    public void isPpcProduct2() {
        assertFalse(ProdId.isPpcProduct(ProdId.LSV));
    }

    @Test
    public void isAmpProduct1() {
        assertTrue(ProdId.isAmpProduct(ProdId.LSV));
    }

    @Test
    public void isAmpProduct2() {
        assertFalse(ProdId.isAmpProduct(ProdId.DIFM_PPC));
    }
}
