/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.geo;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.Quadruple;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.geo.ZipCodeCalculator;

public class ZipCodeCalculatorUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:59 ZipCodeCalculatorUnitTest.java NSI";

    private static Connection gdbConnection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        gdbConnection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(gdbConnection);
    }

    @Test
    public void getCityStateLatitudeAndLongitudeTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        assertNull(zipCodeCalculator.getCityStateLatitudeAndLongitude(logTag, gdbConnection, null));
    }

    @Test
    public void getCitiesStatesLatitudesAndLongitudesTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        Collection<String> zipCodes = Collections.emptyList();
        assertTrue(zipCodeCalculator.getCitiesStatesLatitudesAndLongitudes(logTag, gdbConnection, zipCodes).isEmpty());
    }

    @Test
    public void getCitiesStatesLatitudesAndLongitudesTest2() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        Collection<Quadruple<String, String, Double, Double>> citiesStatesLatitudesAndLongitudes = zipCodeCalculator.getCitiesStatesLatitudesAndLongitudes(logTag, gdbConnection, Arrays.asList("02193", "20194"));
        assertEquals(2, citiesStatesLatitudesAndLongitudes.size());
    }

    @Test
    public void getZipCodesWithinRadiusTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        assertTrue(zipCodeCalculator.getZipCodesWithinRadius(logTag, gdbConnection, 0D, 0D).isEmpty());
    }

    @Test
    public void getZipCodesWithinRadiusKilometersTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        assertTrue(zipCodeCalculator.getZipCodesWithinRadiusKilometers(logTag, gdbConnection, 0D, 0D, 0D).isEmpty());
    }

    @Test
    public void getZipCodesWithinRadiusKilometersTest2() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        Quadruple<String, String, Double, Double> q = zipCodeCalculator.getCityStateLatitudeAndLongitude(logTag, gdbConnection, "98103");
        Collection<String> zipCodes = zipCodeCalculator.getZipCodesWithinRadiusKilometers(logTag, gdbConnection, q.getThird(), q.getFourth(), 100D);
        assertEquals(302, zipCodes.size());
    }

    @Test
    public void getClosestZipCodeWithinRadiusTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        assertNull(zipCodeCalculator.getClosestZipCodeWithinRadius(logTag, gdbConnection, 0D, 0D));
    }

    @Test
    public void getClosestZipCodeWithinRadiusTest2() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        Quadruple<String, String, Double, Double> q = zipCodeCalculator.getCityStateLatitudeAndLongitude(logTag, gdbConnection, "98103");
        assertEquals("98003", zipCodeCalculator.getClosestZipCodeWithinRadius(logTag, gdbConnection, q.getThird(), q.getFourth()));
    }

    @Test
    public void getClosestZipCodeWithinRadiusKilometersTest1() throws SQLException {
        ZipCodeCalculator zipCodeCalculator = new ZipCodeCalculator("");
        assertNull(zipCodeCalculator.getClosestZipCodeWithinRadiusKilometers(logTag, gdbConnection, 0D, 0D, 0D));
    }
}
