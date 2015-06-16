/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.tracking.ParamAdder;
import com.netsol.adagent.util.tracking.ParamTools;

public class GoogleParamAdderTest {
    private static ParamAdder paramAdder;
    private static ParamAdder sitelinksParamAdder;

    @BeforeClass
    public static void setUp() {
        paramAdder = ParamTools.getParamAdder(VendorId.GOOGLE, 42L);
        sitelinksParamAdder = ParamTools.getSitelinksParamAdder(VendorId.GOOGLE);
    }

    @Test
    public void testAddParams1() {
        assertEquals(
                "http://www.google.com/?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("http://www.google.com/")
            );
    }

    @Test
    public void testAddParams2() {
        assertEquals(
                "http://www.google.com/?param1=value1&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("http://www.google.com/?param1=value1")
            );
    }

    @Test
    public void testAddParams3() {
        assertEquals(
                "/?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("/")
            );
    }

    @Test
    public void testAddParams4() {
        assertEquals(
                "/?param1=value1&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("/?param1=value1")
            );
    }

    @Test
    public void testAddParams5() {
        assertEquals(
                "/path/page.html?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("/path/page.html")
            );
    }

    @Test
    public void testAddParams6() {
        assertEquals(
                "/path/page.html?param1=value1&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("/path/page.html?param1=value1")
            );
    }

    @Test
    public void testAddParams7() {
        assertEquals(
                "http://domain.com:8080/path/page.html?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("domain.com:8080/path/page.html")
            );
    }

    @Test
    public void testAddParams8() {
        assertEquals(
                "http://domain.com/path/page.html?param1=value1&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42",
                paramAdder.addParams("domain.com/path/page.html?param1=value1")
            );
    }

    @Test
    public void testAddSitelinksParams1() {
        assertEquals(
                "http://www.google.com/?creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}",
                sitelinksParamAdder.addParams("http://www.google.com/")
            );
    }

    @Test
    public void testAddSitelinksParams2() {
        assertEquals(
                "http://domain.com:8080/path/page.html?creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}",
                sitelinksParamAdder.addParams("domain.com:8080/path/page.html")
            );
    }
}
