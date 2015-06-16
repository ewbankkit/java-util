/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.InterceptorFeatures;
import com.netsol.adagent.util.beans.InterceptorMapping;
import com.netsol.adagent.util.beans.InterceptorReplacement;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.Quadruple;
import com.netsol.adagent.util.beans.Triple;
import com.netsol.adagent.util.codes.LeadTrackingType;
import com.netsol.adagent.util.codes.ProdId;
import com.netsol.adagent.util.codes.ProductStatus;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorFeaturesHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorMappingHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorReplacementHelper;
import com.netsol.adagent.util.dbhelpers.ProductSecondaryUrlHelper;

public class TrackingUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:06 TrackingUtilUnitTest.java NSI";

    private static Connection gdbConnection;
    private static Connection pdbConnection;
    private static final InterceptorFeaturesHelper interceptorFeaturesHelper = new InterceptorFeaturesHelper("");
    private static final InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
    private static final InterceptorReplacementHelper interceptorReplacementHelper = new InterceptorReplacementHelper("");
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";
    private static final ProductSecondaryUrlHelper productSecondaryUrlHelper = new ProductSecondaryUrlHelper("");
    private static final String updatedBySystem = "unit test";
    private static final String updatedByUser = "unit test";

    @BeforeClass
    public static void setup() throws SQLException {
        gdbConnection = BaseHelper.createDevGdbConnection();
        pdbConnection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(gdbConnection);
        BaseHelper.close(pdbConnection);
    }


    @Test
    public void extractHostNameTest1() {
        assertNull(TrackingUtil.extractHostName(null));
    }

    @Test
    public void extractHostNameTest2() {
        assertEquals("", TrackingUtil.extractHostName(""));
    }

    @Test
    public void extractHostNameTest3() {
        assertEquals("", TrackingUtil.extractHostName("http://"));
    }

    @Test
    public void extractHostNameTest4() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com"));
    }

    @Test
    public void extractHostNameTest5() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com/"));
    }

    @Test
    public void extractHostNameTest6() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com:8080"));
    }

    @Test
    public void extractHostNameTest7() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com:8080/"));
    }

    @Test
    public void extractHostNameTest9() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com:"));
    }

    @Test
    public void extractHostNameTest10() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com:/"));
    }

    @Test
    public void extractHostNameTest11() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4"));
    }

    @Test
    public void extractHostNameTest12() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4/"));
    }

    @Test
    public void extractHostNameTest13() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4:8080"));
    }

    @Test
    public void extractHostNameTest14() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4:8080/"));
    }

    @Test
    public void extractHostNameTest15() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4:"));
    }

    @Test
    public void extractHostNameTest16() {
        assertEquals("1.2.3.4", TrackingUtil.extractHostName("http://1.2.3.4:/"));
    }

    @Test
    public void extractHostNameTest17() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com/whatever.html?xyz=123&abc=999"));
    }

    @Test
    public void extractHostNameTest18() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://example.com:8080/whatever.html?xyz=123&abc=999"));
    }

    @Test
    public void extractHostNameTest19() {
        assertEquals("", TrackingUtil.extractHostName("http://:8080/whatever.html?xyz=123&abc=999"));
    }

    @Test
    public void extractHostNameTest20() {
        assertEquals("example.com", TrackingUtil.extractHostName("example.com"));
    }

    @Test
    public void extractHostNameTest21() {
        assertEquals("example.com", TrackingUtil.extractHostName("example.com:8080"));
    }

    @Test
    public void extractHostNameTest22() {
        assertEquals("example.com", TrackingUtil.extractHostName("EXAMPLE.COM"));
    }

    @Test
    public void extractHostNameTest23() {
        assertEquals("example.com", TrackingUtil.extractHostName("HTTP://EXAMPLE.COM"));
    }

    @Test
    public void extractHostNameTest24() {
        assertEquals("example.com", TrackingUtil.extractHostName("http://EXAMPLE.COM"));
    }

    @Test
    public void extractHostNameAndPortTest1() {
        assertEquals(Pair.from(null, null), TrackingUtil.extractHostNameAndPort(null));
    }

    @Test
    public void extractHostNameAndPortTest2() {
        assertEquals(Pair.from("", null), TrackingUtil.extractHostNameAndPort(""));
    }

    @Test
    public void extractHostNameAndPortTest3() {
        assertEquals(Pair.from("example.com", null), TrackingUtil.extractHostNameAndPort("example.com"));
    }

    @Test
    public void extractHostNameAndPortTest4() {
        assertEquals(Pair.from("example.com", null), TrackingUtil.extractHostNameAndPort("http://example.com"));
    }

    @Test
    public void extractHostNameAndPortTest5() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("example.com:8080"));
    }

    @Test
    public void extractHostNameAndPortTest6() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("http://example.com:8080"));
    }

    @Test
    public void extractHostNameAndPortTest7() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("http://example.com:8080/"));
    }

    @Test
    public void extractHostNameAndPortTest8() {
        assertEquals(Pair.from("example.com", null), TrackingUtil.extractHostNameAndPort("http://example.com/whatever.html"));
    }

    @Test
    public void extractHostNameAndPortTest9() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("http://example.com:8080/whatever.html"));
    }

    @Test
    public void extractHostNameAndPortTest10() {
        assertEquals(Pair.from("example.com", null), TrackingUtil.extractHostNameAndPort("EXAMPLE.COM"));
    }

    @Test
    public void extractHostNameAndPortTest11() {
        assertEquals(Pair.from("example.com", null), TrackingUtil.extractHostNameAndPort("HTTP://EXAMPLE.COM"));
    }

    @Test
    public void extractHostNameAndPortTest12() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("EXAMPLE.COM:8080"));
    }

    @Test
    public void extractHostNameAndPortTest13() {
        assertEquals(Pair.from("example.com", 8080), TrackingUtil.extractHostNameAndPort("http://EXAMPLE.COM:8080"));
    }

    @Test
    public void extractHostNamePlusPortTest1() {
        assertNull(TrackingUtil.extractHostNamePlusPort(null));
    }

    @Test
    public void extractHostNamePlusPortTest2() {
        assertEquals("", TrackingUtil.extractHostNamePlusPort(""));
    }

    @Test
    public void extractHostNamePlusPortTest3() {
        assertEquals("example.com", TrackingUtil.extractHostNamePlusPort("example.com"));
    }

    @Test
    public void extractHostNamePlusPortTest4() {
        assertEquals("example.com:8080", TrackingUtil.extractHostNamePlusPort("example.com:8080"));
    }

    @Test
    public void extractHostNamePlusPortTest5() {
        assertEquals("example.com", TrackingUtil.extractHostNamePlusPort("http://example.com"));
    }

    @Test
    public void extractHostNamePlusPortTest6() {
        assertEquals("example.com:8080", TrackingUtil.extractHostNamePlusPort("http://example.com:8080"));
    }

    @Test
    public void extractHostNamePlusPortTest7() {
        assertEquals("example.com", TrackingUtil.extractHostNamePlusPort("http://example.com/"));
    }

    @Test
    public void extractHostNamePlusPortTest8() {
        assertEquals("example.com:8080", TrackingUtil.extractHostNamePlusPort("http://example.com:8080/"));
    }

    @Test
    public void extractHostNamePlusPortTest9() {
        assertEquals("example.com", TrackingUtil.extractHostNamePlusPort("http://example.com/whatever.html"));
    }

    @Test
    public void extractHostNamePlusPortTest10() {
        assertEquals("example.com:8080", TrackingUtil.extractHostNamePlusPort("http://example.com:8080/whatever.html"));
    }

    @Test
    public void extractPathAndParametersTest1() {
        assertEquals(Pair.from(null, null), TrackingUtil.extractPathAndParameters(null));
    }

    @Test
    public void extractPathAndParametersTest2() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters(""));
    }

    @Test
    public void extractPathAndParametersTest3() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("example.com"));
    }

    @Test
    public void extractPathAndParametersTest4() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("http://example.com"));
    }

    @Test
    public void extractPathAndParametersTest5() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("example.com/"));
    }

    @Test
    public void extractPathAndParametersTest6() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("http://example.com/"));
    }

    @Test
    public void extractPathAndParametersTest7() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("/"));
    }

    @Test
    public void extractPathAndParametersTest8() {
        assertEquals(Pair.from("/whatever.html", null), TrackingUtil.extractPathAndParameters("/whatever.html"));
    }

    @Test
    public void extractPathAndParametersTest9() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("whatever.html"));
    }

    @Test
    public void extractPathAndParametersTest10() {
        assertEquals(Pair.from("/whatever.html", null), TrackingUtil.extractPathAndParameters("http://example.com/whatever.html"));
    }

    @Test
    public void extractPathAndParametersTest11() {
        assertEquals(Pair.from("/whatever.html", null), TrackingUtil.extractPathAndParameters("http://example.com:8080/whatever.html"));
    }

    @Test
    public void extractPathAndParametersTest12() {
        assertEquals(Pair.from("/whatever.html", "abc=123"), TrackingUtil.extractPathAndParameters("http://example.com/whatever.html?abc=123"));
    }

    @Test
    public void extractPathAndParametersTest13() {
        assertEquals(Pair.from("/whatever.html", "abc=123"), TrackingUtil.extractPathAndParameters("http://example.com:8080/whatever.html?abc=123"));
    }

    @Test
    public void extractPathAndParametersTest14() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("EXAMPLE.COM"));
    }

    @Test
    public void extractPathAndParametersTest15() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("HTTP://EXAMPLE.COM"));
    }

    @Test
    public void extractPathAndParametersTest16() {
        assertEquals(Pair.from("/", "abc=123"), TrackingUtil.extractPathAndParameters("http://example.com?abc=123"));
    }

    @Test
    public void extractPathAndParametersTest17() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("http://example.com/?"));
    }

    @Test
    public void extractPathAndParametersTest18() {
        assertEquals(Pair.from("/", null), TrackingUtil.extractPathAndParameters("http://example.com?"));
    }


    @Test
    public void extractPathPlusParametersTest1() {
        assertNull(TrackingUtil.extractPathPlusParameters(null));
    }

    @Test
    public void extractPathPlusParametersTest2() {
        assertEquals("/", TrackingUtil.extractPathPlusParameters(""));
    }

    @Test
    public void extractPathPlusParametersTest3() {
        assertEquals("/", TrackingUtil.extractPathPlusParameters("example.com"));
    }

    @Test
    public void extractPathPlusParametersTest4() {
        assertEquals("/", TrackingUtil.extractPathPlusParameters("http://example.com"));
    }

    @Test
    public void extractPathPlusParametersTest5() {
        assertEquals("/", TrackingUtil.extractPathPlusParameters("http://example.com/"));
    }

    @Test
    public void extractPathPlusParametersTest6() {
        assertEquals("/whatever.html", TrackingUtil.extractPathPlusParameters("http://example.com/whatever.html"));
    }

    @Test
    public void extractPathPlusParametersTest7() {
        assertEquals("/whatever.html?abc=123", TrackingUtil.extractPathPlusParameters("http://example.com/whatever.html?abc=123"));
    }

    @Test
    public void extractPathPlusParametersTest8() {
        assertEquals("/?abc=123", TrackingUtil.extractPathPlusParameters("http://example.com?abc=123"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest1() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter(null));
    }

    @Test
    public void extractProtocolPlusDelimiterTest2() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter(""));
    }

    @Test
    public void extractProtocolPlusDelimiterTest3() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("www.amazon.com"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest4() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("http://www.amazon.com"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest5() {
        assertEquals("https://", TrackingUtil.extractProtocolPlusDelimiter("https://www.amazon.com"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest6() {
        assertEquals("ftp://", TrackingUtil.extractProtocolPlusDelimiter("ftp://www.amazon.com"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest7() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("WWW.AMAZON.COM"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest8() {
        assertEquals("HTTP://", TrackingUtil.extractProtocolPlusDelimiter("HTTP://WWW.AMAZON.COM"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest9() {
        assertEquals("HTTPS://", TrackingUtil.extractProtocolPlusDelimiter("HTTPS://WWW.AMAZON.COM"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest10() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("www.amazon.com:8080"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest11() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("http://www.amazon.com:8080"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest12() {
        assertEquals("https://", TrackingUtil.extractProtocolPlusDelimiter("https://www.amazon.com:8080"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest13() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("www.amazon.com/"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest14() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("http://www.amazon.com/"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest15() {
        assertEquals("https://", TrackingUtil.extractProtocolPlusDelimiter("https://www.amazon.com/"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest16() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("www.amazon.com/whatever.html?abc=123"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest17() {
        assertEquals("http://", TrackingUtil.extractProtocolPlusDelimiter("http://www.amazon.com/whatever.html?abc=123"));
    }

    @Test
    public void extractProtocolPlusDelimiterTest18() {
        assertEquals("https://", TrackingUtil.extractProtocolPlusDelimiter("https://www.amazon.com/whatever.html?abc=123"));
    }

    @Test
    public void extractProtocolTest1() {
        assertEquals("http", TrackingUtil.extractProtocol(null));
    }

    @Test
    public void extractProtocolTest2() {
        assertEquals("http", TrackingUtil.extractProtocol(""));
    }

    @Test
    public void extractProtocolTest3() {
        assertEquals("http", TrackingUtil.extractProtocol("www.amazon.com"));
    }

    @Test
    public void extractProtocolTest4() {
        assertEquals("http", TrackingUtil.extractProtocol("http://www.amazon.com"));
    }

    @Test
    public void extractProtocolTest5() {
        assertEquals("https", TrackingUtil.extractProtocol("https://www.amazon.com"));
    }

    @Test
    public void extractProtocolTest6() {
        assertEquals("ftp", TrackingUtil.extractProtocol("ftp://www.amazon.com"));
    }

    @Test
    public void fixUrlTest1() {
        assertNull(TrackingUtil.fixUrl(null));
    }

    @Test
    public void fixUrlTest2() {
        assertEquals("http://", TrackingUtil.fixUrl(""));
    }

    @Test
    public void fixUrlTest3() {
        assertEquals("http://", TrackingUtil.fixUrl("http://"));
    }

    @Test
    public void fixUrlTest4() {
        assertEquals("http://", TrackingUtil.fixUrl("://"));
    }

    @Test
    public void fixUrlTest5() {
        assertEquals("http://example.com", TrackingUtil.fixUrl("example.com"));
    }

    @Test
    public void fixUrlTest6() {
        assertEquals("http://example.com", TrackingUtil.fixUrl("http://example.com"));
    }

    @Test
    public void fixUrlTest7() {
        assertEquals("https://example.com", TrackingUtil.fixUrl("https://example.com"));
    }

    @Test
    public void fixUrlTest8() {
        assertEquals("http://example.com", TrackingUtil.fixUrl("ftp://example.com"));
    }

    @Test
    public void getInterceptorMappingTest1() {
        assertNull(TrackingUtil.getInterceptorMapping(null, null));
    }

    @Test
    public void getInterceptorMappingTest2() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping(null, "");
        assertNull(interceptorMapping);
    }

    @Test
    public void getInterceptorMappingTest3() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("", "");
        assertNull(interceptorMapping);
    }

    @Test
    public void getInterceptorMappingTest4() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "www.example.com");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest5() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "WWW.EXAMPLE.COM");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest6() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "http://www.example.com");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest7() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "HTTP://WWW.EXAMPLE.COM");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest8() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "https://www.example.com");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest9() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "HTTPS://WWW.EXAMPLE.COM");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest10() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "http://www.example.com:8080/");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest11() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "HTTP://WWW.EXAMPLE.COM:8080/");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest12() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "http://www.example.com:80/");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(80, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest13() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "https://www.example.com:443/");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(443, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest14() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "http://www.example.com/index.html?boop=123");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest15() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "HTTP://WWW.EXAMPLE.COM/index.html?boop=123");
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getInterceptorMappingTest16() {
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping("PROD.INST.ID", "http://www.example.com/index.html?boop=123", null, "alfajores.com.ar");
        assertEquals("www-example-com.alfajores.com.ar", interceptorMapping.getAlias());
        assertEquals("PROD.INST.ID", interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(0, interceptorMapping.getRealPort());
    }

    @Test
    public void getAlternateHostNameTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertNull(TrackingUtil.getAlternateHostName(validTopLevelDomainNames, null));
    }

    @Test
    public void getAlternateHostNameTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals("www.example.com", TrackingUtil.getAlternateHostName(validTopLevelDomainNames, "example.com"));
    }

    @Test
    public void getAlternateHostNameTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals("example.com", TrackingUtil.getAlternateHostName(validTopLevelDomainNames, "www.example.com"));
    }

    @Test
    public void getAlternateHostNameTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals("example.co.uk", TrackingUtil.getAlternateHostName(validTopLevelDomainNames, "www.example.co.uk"));
    }

    @Test
    public void getAlternateHostNameTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals("www.example.co.uk", TrackingUtil.getAlternateHostName(validTopLevelDomainNames, "example.co.uk"));
    }

    @Test
    public void getAlternateHostNameTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertNull(TrackingUtil.getAlternateHostName(validTopLevelDomainNames, "kit.example.com"));
    }

    @Test
    public void getAlternateInterceptorMappingTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        InterceptorMapping interceptorMapping = trackingUtil.getAlternateInterceptorMapping(logTag, gdbConnection, null, validTopLevelDomainNames);
        assertNull(interceptorMapping);
    }

    @Test
    public void getAlternateInterceptorMappingTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.example.com", updatedByUser, updatedBySystem);
        InterceptorMapping interceptorMapping = trackingUtil.getAlternateInterceptorMapping(logTag, gdbConnection, prodInstId, validTopLevelDomainNames);
        assertEquals("example.com", interceptorMapping.getRealHost());
    }

    @Test
    public void getAlternateInterceptorMappingTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "example.com", updatedByUser, updatedBySystem);
        InterceptorMapping interceptorMapping = trackingUtil.getAlternateInterceptorMapping(logTag, gdbConnection, prodInstId, validTopLevelDomainNames);
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
    }

    @Test
    public void getHostNamePlusPortReplacementRegexTest1() {
        String realHost = "adagentsite.com";
        int realPort = 8080;
        String realHostNameAndPortRegex = TrackingUtil.getHostNamePlusPortReplacementRegex(realHost, realPort);
        String replacement = TrackingUtil.getHostNamePlusPortReplacement("adagentsite-com.netsolads.com", 0);
        String redirectUrl = "http://adagentsite.com:8080/default.html";
        redirectUrl = redirectUrl.replaceFirst(realHostNameAndPortRegex, replacement);
        assertEquals("http://adagentsite-com.netsolads.com/default.html", redirectUrl);
    }

    @Test
    public void getHostNamePlusPortReplacementRegexTest2() {
        String realHost = "adagentsite.com";
        int realPort = 0;
        String realHostNameAndPortRegex = TrackingUtil.getHostNamePlusPortReplacementRegex(realHost, realPort);
        String replacement = TrackingUtil.getHostNamePlusPortReplacement("adagentsite-com.netsolads.com", 0);
        String redirectUrl = "http://adagentsite.com:8080/default.html";
        redirectUrl = redirectUrl.replaceFirst(realHostNameAndPortRegex, replacement);
        assertEquals("http://adagentsite-com.netsolads.com:8080/default.html", redirectUrl);
    }

    @Test
    public void getHostNamePlusPortReplacementRegexTest3() {
        String realHost = "adagentsite.com";
        int realPort = 8080;
        String realHostNameAndPortRegex = TrackingUtil.getHostNamePlusPortReplacementRegex(realHost, realPort);
        String replacement = TrackingUtil.getHostNamePlusPortReplacement("adagentsite-com.netsolads.com", 0);
        String redirectUrl = "http://adagentsite.com/default.html";
        redirectUrl = redirectUrl.replaceFirst(realHostNameAndPortRegex, replacement);
        assertEquals("http://adagentsite.com/default.html", redirectUrl);
    }

    @Test
    public void getHostNamePlusPortReplacementRegexTest5() {
        String textContent = "<a href=\"http://adagentsite.com:8080/home.html\">";
        String realHost = "adagentsite.com";
        int realPort = 8080;
        String replacement = TrackingUtil.getHostNamePlusPortReplacement("adagentsite-com.netsolads.com", 0);
        String realHostNameAndPortRegex = TrackingUtil.getHostNamePlusPortReplacementRegex(realHost, realPort);
        textContent = textContent.replaceAll(realHostNameAndPortRegex, replacement);
        assertEquals("<a href=\"http://adagentsite-com.netsolads.com/home.html\">", textContent);
    }

    @Test
    public void isCallLeadRecordingEnabledTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertFalse(trackingUtil.isCallLeadRecordingEnabled(logTag, pdbConnection, null));
    }

    @Test
    public void isCallLeadRecordingEnabledTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.setCallLeadRecordingEnabled(logTag, pdbConnection, prodInstId, false, updatedByUser, updatedBySystem);
        assertFalse(trackingUtil.isCallLeadRecordingEnabled(logTag, pdbConnection, prodInstId));
    }

    @Test
    public void isCallLeadRecordingEnabledTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.setCallLeadRecordingEnabled(logTag, pdbConnection, prodInstId, true, updatedByUser, updatedBySystem);
        assertTrue(trackingUtil.isCallLeadRecordingEnabled(logTag, pdbConnection, prodInstId));
    }

    @Test
    public void isCallTrackingEnabledTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertFalse(trackingUtil.isCallTrackingEnabled(logTag, pdbConnection, null));
    }

    @Test
    public void isCallTrackingEnabledTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.setCallTrackingEnabled(logTag, pdbConnection, prodInstId, false, updatedByUser, updatedBySystem);
        assertFalse(trackingUtil.isCallTrackingEnabled(logTag, pdbConnection, prodInstId));
    }

    @Test
    public void isCallTrackingEnabledTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.setCallTrackingEnabled(logTag, pdbConnection, prodInstId, true, updatedByUser, updatedBySystem);
        assertTrue(trackingUtil.isCallTrackingEnabled(logTag, pdbConnection, prodInstId));
    }

    @Test
    public void getStatusTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductStatus(logTag, gdbConnection, null));
    }

    @Test
    public void getStatusTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.INACTIVE, updatedByUser, updatedBySystem);
        assertEquals(ProductStatus.INACTIVE, trackingUtil.getProductStatus(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getStatusTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        assertEquals(ProductStatus.ACTIVE, trackingUtil.getProductStatus(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getTrackingTypeTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductTrackingType(logTag, gdbConnection, null));
    }

    @Test
    public void getTrackingTypeTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        assertEquals(LeadTrackingType.INTERCEPTOR_TRACKING, trackingUtil.getProductTrackingType(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getTrackingTypeTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        assertEquals(LeadTrackingType.JAVASCRIPT_TRACKING, trackingUtil.getProductTrackingType(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getUrlTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductUrl(logTag, gdbConnection, null));
    }

    @Test
    public void getUrlTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "", updatedByUser, updatedBySystem);
        assertEquals("", trackingUtil.getProductUrl(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getUrlTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.example.com", updatedByUser, updatedBySystem);
        assertEquals("www.example.com", trackingUtil.getProductUrl(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getUrlAndTrackingTypeTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductUrlAndTrackingType(logTag, gdbConnection, null));
    }

    @Test
    public void getUrlAndTrackingTypeTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        assertEquals(Pair.from("www.domain.com", LeadTrackingType.INTERCEPTOR_TRACKING), trackingUtil.getProductUrlAndTrackingType(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getUrlStatusAndTrackingTypeTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductUrlStatusAndTrackingType(logTag, gdbConnection, null));
    }

    @Test
    public void getUrlStatusAndTrackingTypeTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.co.uk", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.UNKNOWN, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        assertEquals(Triple.from("www.domain.co.uk", ProductStatus.UNKNOWN, LeadTrackingType.JAVASCRIPT_TRACKING), trackingUtil.getProductUrlStatusAndTrackingType(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getProdIdUrlStatusAndTrackingTypeTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getProductProdIdUrlStatusAndTrackingType(logTag, gdbConnection, null));
    }

    @Test
    public void getProdIdUrlStatusAndTrackingTypeTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com.ar", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.DEACTIVATED, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        assertEquals(Quadruple.from(Long.valueOf(ProdId.DIFM_PPC), "www.domain.com.ar", ProductStatus.DEACTIVATED, LeadTrackingType.INTERCEPTOR_TRACKING), trackingUtil.getProductProdIdUrlStatusAndTrackingType(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getInterceptorFeaturesTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorFeatures interceptorFeatures = interceptorFeaturesHelper.getFeatures(logTag, gdbConnection, prodInstId);
        assertTrue(interceptorFeatures.isPerformReplacements());
        assertTrue(interceptorFeatures.isPropagateAdParams());
        assertTrue(interceptorFeatures.isTrackEmail());
        assertTrue(interceptorFeatures.isTrackForm());
        assertTrue(interceptorFeatures.isTrackHighValuePage());
        assertTrue(interceptorFeatures.isTrackShoppingCart());
        assertEquals(prodInstId, interceptorFeatures.getProdInstId());
    }

    @Test
    public void getPrimaryAliasTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("www-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("www-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("secure-domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("secure.domain.com");
        interceptorMapping.setRealPort(443);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("www-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "secure.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.domain.com");
        interceptorMapping.setRealPort(80);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("secure-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("domain.com");
        interceptorMapping.setRealPort(80);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("www-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.domain.com");
        interceptorMapping.setRealPort(80);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void getPrimaryAliasTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("domain.com");
        interceptorMapping.setRealPort(80);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("secure-domain-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("secure.domain.com");
        interceptorMapping.setRealPort(443);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("www-domain-com.netsolads.com", trackingUtil.getPrimaryAlias(logTag, gdbConnection, prodInstId));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(null, null), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, null));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(null, null), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, " "));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(null, "com"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "com"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(null, null), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "con"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("example.com", "com"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "example.com"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("example.com", "com"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "www.example.com"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("example.com", "com"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "www.sub-domain.example.com"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest8() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("guardian.co.uk", "co.uk"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "http://www.guardian.co.uk/commentisfree/2011/feb/06/capitalism-multiculturalism-cameron-flawed-analysis"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest9() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("freddo.com.ar", "com.ar"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "http://www.freddo.com.ar/"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest10() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("my.army.mil", "army.mil"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "HTTPS://MY.ARMY.MIL/"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest11() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("state.sc.us", "sc.us"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "http://www.museum.state.sc.us/events/coming.aspx"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest12() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from("google.us", "us"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, "http://www.google.us/"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest13() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(".com", "com"), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, ".com"));
    }

    @Test
    public void extractPrimaryAndTopLevelDomainNamesTest14() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        List<String> validTopLevelDomainNames = trackingUtil.getValidTopLevelDomainNames(logTag, gdbConnection);
        assertEquals(Pair.from(null, null), TrackingUtil.extractPrimaryAndTopLevelDomainNames(validTopLevelDomainNames, ".con"));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest1() {
        assertNull(TrackingUtil.getStandardPhoneNumberReplacement(null));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest2() {
        assertEquals("", TrackingUtil.getStandardPhoneNumberReplacement(""));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest3() {
        assertEquals("(800) 555-1234", TrackingUtil.getStandardPhoneNumberReplacement("8005551234"));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest4() {
        assertEquals("800555123", TrackingUtil.getStandardPhoneNumberReplacement("800555123"));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest5() {
        assertEquals("80055512345", TrackingUtil.getStandardPhoneNumberReplacement("80055512345"));
    }

    @Test
    public void getStandardPhoneNumberReplacementTest6() {
        assertEquals("800-555-1234", TrackingUtil.getStandardPhoneNumberReplacement("800-555-1234"));
    }

    @Test
    public void getVanityNumberRegexTest1() {
        assertNull(TrackingUtil.getVanityNumberRegex(null));
    }

    @Test
    public void getVanityNumberRegexTest2() {
        assertNull(TrackingUtil.getVanityNumberRegex(""));
    }

    @Test
    public void getVanityNumberRegexTest3() {
        assertNull(TrackingUtil.getVanityNumberRegex("1234567"));
    }

    @Test
    public void getVanityNumberRegexTest4() {
        assertEquals("8005551234", TrackingUtil.getVanityNumberRegex("8005551234"));
    }

    @Test
    public void getVanityNumberRegexTest5() {
        assertEquals("18005551234", TrackingUtil.getVanityNumberRegex("18005551234"));
    }

    @Test
    public void getVanityNumberRegexTest6() {
        assertEquals("\\(800\\)5551234", TrackingUtil.getVanityNumberRegex("(800)5551234"));
    }

    @Test
    public void getVanityNumberRegexTest7() {
        assertEquals("1\\(800\\)5551234", TrackingUtil.getVanityNumberRegex("1(800)5551234"));
    }

    @Test
    public void getVanityNumberRegexTest8() {
        assertEquals("1[\\s\\./-]?\\(800\\)[\\s\\./-]?555[\\s\\./-]?BOOP", TrackingUtil.getVanityNumberRegex("1-(800)-555-BOOP"));
    }

    @Test
    public void getVanityNumberRegexTest9() {
        assertEquals("800[\\s\\./-]?555[\\s\\./-]?BOOP", TrackingUtil.getVanityNumberRegex("800.555 BOOP"));
    }

    @Test
    public void getVanityNumberRegexTest10() {
        assertEquals("\\+1[\\s\\./-]?908[\\s\\./-]?500[\\s\\./-]?2691", TrackingUtil.getVanityNumberRegex("+1.908.500.2691"));
    }

    @Test
    public void getOriginalNumberRegexTest1() {
        assertNull(TrackingUtil.getOriginalNumberRegex(null));
    }

    @Test
    public void getOriginalNumberRegexTest2() {
        assertNull(TrackingUtil.getOriginalNumberRegex(""));
    }

    @Test
    public void getOriginalNumberRegexTest3() {
        assertNull(TrackingUtil.getOriginalNumberRegex("1234567"));
    }

    @Test
    public void getOriginalNumberRegexTest4() {
        assertEquals("1?[\\s*-\\.]?\\(?[703]{0,3}\\)?[\\s\\./-]*668[\\s\\./-]*4751", TrackingUtil.getOriginalNumberRegex("7036684751"));
    }

    @Test
    public void getOriginalNumberRegexTest5() {
        assertEquals("1?[\\s*-\\.]?\\(?[170]{0,3}\\)?[\\s\\./-]*366[\\s\\./-]*8475", TrackingUtil.getOriginalNumberRegex("17036684751"));
    }

    @Test
    public void replaceNumberTest1() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 7036684751 today";
        assertEquals("Call(800) 555-1234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegex(originalNumber), TrackingUtil.getStandardPhoneNumberReplacement(replacementNumber)));
    }

    @Test
    public void replaceNumberTest2() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 703  668 4751 today";
        assertEquals("Call(800) 555-1234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegex(originalNumber), TrackingUtil.getStandardPhoneNumberReplacement(replacementNumber)));
    }

    @Test
    public void replaceNumberTest3() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 1-703.668.4751 today";
        assertEquals("Call (800) 555-1234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegex(originalNumber), TrackingUtil.getStandardPhoneNumberReplacement(replacementNumber)));
    }

    @Test
    public void getStandardPhoneNumberReplacementNewTest1() {
        assertNull(TrackingUtil.getStandardPhoneNumberReplacementNew(null));
    }

    @Test
    public void getStandardPhoneNumberReplacementNewTest2() {
        assertEquals("", TrackingUtil.getStandardPhoneNumberReplacementNew(""));
    }

    @Test
    public void getStandardPhoneNumberReplacementNewTest3() {
        assertEquals("$1$2800$3$4555$51234", TrackingUtil.getStandardPhoneNumberReplacementNew("8005551234"));
    }

    @Test
    public void getOriginalNumberRegexNewTest1() {
        assertNull(TrackingUtil.getOriginalNumberRegexNew(null));
    }

    @Test
    public void getOriginalNumberRegexNewTest2() {
        assertNull(TrackingUtil.getOriginalNumberRegexNew(""));
    }

    @Test
    public void getOriginalNumberRegexNewTest3() {
        assertNull(TrackingUtil.getOriginalNumberRegexNew("1234567"));
    }

    @Test
    public void getOriginalNumberRegexNewTest4() {
        assertEquals("(1[\\s*-\\.])?(\\()?703(\\))?([\\s\\./-]*)668([\\s\\./-]*)4751", TrackingUtil.getOriginalNumberRegexNew("7036684751"));
    }

    @Test
    public void replaceNumberTestNew1() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 7036684751 today";
        assertEquals("Call 8005551234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegexNew(originalNumber), TrackingUtil.getStandardPhoneNumberReplacementNew(replacementNumber)));
    }

    @Test
    public void replaceNumberTestNew2() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 703  668 4751 today";
        assertEquals("Call 800  555 1234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegexNew(originalNumber), TrackingUtil.getStandardPhoneNumberReplacementNew(replacementNumber)));
    }

    @Test
    public void replaceNumberTestNew3() {
        String originalNumber = "7036684751";
        String replacementNumber = "8005551234";
        String originalText = "Call 1-703.668.4751 today";
        assertEquals("Call 1-800.555.1234 today", originalText.replaceAll(TrackingUtil.getOriginalNumberRegexNew(originalNumber), TrackingUtil.getStandardPhoneNumberReplacementNew(replacementNumber)));
    }

    @Test
    public void modifyHostNameAndPortTest1() {
        assertNull(TrackingUtil.modifyHostNameAndPort(null, null, 0));
    }

    @Test
    public void modifyHostNameAndPortTest2() {
        assertEquals("http://www.example.com/", TrackingUtil.modifyHostNameAndPort("http://www.example.com/", null, 0));
    }

    @Test
    public void modifyHostNameAndPortTest3() {
        assertEquals("http://domain.com/", TrackingUtil.modifyHostNameAndPort("http://www.example.com/", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest4() {
        assertEquals("http://domain.com/", TrackingUtil.modifyHostNameAndPort("http://www.example.com", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest5() {
        assertEquals("http://domain.com/", TrackingUtil.modifyHostNameAndPort("www.example.com", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest6() {
        assertEquals("http://domain.com/", TrackingUtil.modifyHostNameAndPort("www.example.com/", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest7() {
        assertEquals("http://domain.com:8080/", TrackingUtil.modifyHostNameAndPort("http://www.example.com", "domain.com", 8080));
    }

    @Test
    public void modifyHostNameAndPortTest8() {
        assertEquals("http://domain.com:8080/", TrackingUtil.modifyHostNameAndPort("http://www.example.com:81", "domain.com", 8080));
    }

    @Test
    public void modifyHostNameAndPortTest9() {
        assertEquals("http://domain.com:8080/", TrackingUtil.modifyHostNameAndPort("http://www.example.com:81/", "domain.com", 8080));
    }

    @Test
    public void modifyHostNameAndPortTest10() {
        assertEquals("http://domain.com:81/", TrackingUtil.modifyHostNameAndPort("http://www.example.com:81/", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest11() {
        assertEquals("http://domain.com/path/whatever.html", TrackingUtil.modifyHostNameAndPort("http://www.example.com/path/whatever.html", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest12() {
        assertEquals("http://domain.com:81/path/whatever.html", TrackingUtil.modifyHostNameAndPort("http://www.example.com:81/path/whatever.html", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest13() {
        assertEquals("http://domain.com:8080/path/whatever.html", TrackingUtil.modifyHostNameAndPort("http://www.example.com/path/whatever.html", "domain.com", 8080));
    }

    @Test
    public void modifyHostNameAndPortTest14() {
        assertEquals("http://domain.com/path/whatever.html?abc=123&xyz=h", TrackingUtil.modifyHostNameAndPort("http://www.example.com/path/whatever.html?abc=123&xyz=h", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest15() {
        assertEquals("http://domain.com:81/path/whatever.html?abc=123&xyz=h", TrackingUtil.modifyHostNameAndPort("http://www.example.com:81/path/whatever.html?abc=123&xyz=h", "domain.com", 0));
    }

    @Test
    public void modifyHostNameAndPortTest16() {
        assertEquals("http://domain.com:8080/path/whatever.html?abc=123&xyz=h", TrackingUtil.modifyHostNameAndPort("http://www.example.com/path/whatever.html?abc=123&xyz=h", "domain.com", 8080));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, null, null));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, null, null));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, null));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, ""));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?boop"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&keywords={keyword}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest8() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest9() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest10() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest11() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest12() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "https://www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest13() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "https://www.domain.com/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest14() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest15() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest16() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping(prodInstId, "www.domain.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("http://www.domain.com:8080/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest17() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com:8080/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "http://www.domain.com:8080/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest18() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest19() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "www.domain.com/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest20() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com:8080", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com:8080/whatever.html?boop=true", trackingUtil.getNsDestinationUrlFromVendor(logTag, pdbConnection, prodInstId, "www-domain-com.netsolads.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest21() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.domain.com:8080/whatever.html?boop=true", Boolean.TRUE), trackingUtil.getNsDestinationUrlAndValidHostFlagFromVendor(logTag, pdbConnection, prodInstId, "www.domain.com:8080/whatever.html?adGroup=42&MSADID={AdId}&boop=true&MSKWID={OrderItemId}&MSKWMT={MatchType}"));
    }

    @Test
    public void getNsDestinationUrlFromVendorTest22() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.example.com/whatever.html?boop=true", Boolean.FALSE), trackingUtil.getNsDestinationUrlAndValidHostFlagFromVendor(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?adGroup=42&creative={creative}&boop=true&keywords={keyword}&matchtype={matchtype}"));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, null, null, 0, 0L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, null, null, VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, null, VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/", VendorId.TELMETRICS, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "https://www.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest8() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest9() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest10() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www-domain-com.netsolads.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "https://www.domain.com/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest11() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest12() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest13() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www2.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "https://www2.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest14() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest15() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest16() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www2.domain.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "https://www2.domain.com/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest17() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest18() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com:8080/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com:8080/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest19() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com:81", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com:81/whatever.html?boop&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/whatever.html?boop", VendorId.MICROSOFT, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest20() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "www.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest21() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "www.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest22() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("https://www2.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "https://www2.domain.com/whatever.html?boop", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest23() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?adGroup=42&boop&keywords={keyword}", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest24() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?adGroup=42&boop&keywords={keyword}", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest25() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/path/whatever.html?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/path/whatever.html", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getVendorDestinationUrlFromNsTest26() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.domain.com/path/whatever.html?creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", trackingUtil.getVendorDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "/path/whatever.html", VendorId.GOOGLE, 42L));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, null, null));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, null));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, ""));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals("/whatever.html?boop", trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals("http://www.example.com/whatever.html?boop", trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, pdbConnection, prodInstId, Collections.singletonList("www.domain.net"));
        assertEquals("http://www.example.com/whatever.html?boop", trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, pdbConnection, prodInstId, Arrays.asList("www.domain.net", "http://www.example.com"));
        assertEquals("/whatever.html?boop",
                     trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}"));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest8() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www.example.com/whatever.html?boop", trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHostTest9() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(0);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals("/whatever.html?boop", trackingUtil.getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(logTag, pdbConnection, prodInstId, "http://www-example-com.netsolads.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, null, null));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertEquals(Pair.from(null, null), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, null));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertEquals(Pair.from(null, null), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, ""));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.domain.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.example.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, pdbConnection, prodInstId, Collections.singletonList("www.domain.net"));
        assertEquals(Pair.from("http://www.example.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.JAVASCRIPT_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, pdbConnection, prodInstId, Arrays.asList("www.domain.net", "http://www.example.com"));
        assertEquals(Pair.from("http://www.example.com", "/whatever.html?boop"),
                     trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest8() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.domain.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest9() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.example.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www.example.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest10() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(0);
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals(Pair.from("http://www.example.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www-example-com.netsolads.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getNsHostAndLandingPageFromVendorDestinationUrlTest11() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "HTTP://WWW.DOMAIN.COM", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals(Pair.from("http://www.domain.com", "/whatever.html?boop"), trackingUtil.getNsHostAndLandingPageFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, "http://www-domain-com.netsolads.com/whatever.html?boop&_nsag=42"));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, null, null, 0));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, null, null, VendorId.GOOGLE));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, prodInstId, null, VendorId.GOOGLE));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "", VendorId.GOOGLE));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertNull(trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/", VendorId.TELMETRICS));
    }

    @Test
    public void getVendorSitelinkDestinationUrlFromNsTest6() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        trackingUtil.deleteInterceptorSettingsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.domain.com", updatedByUser, updatedBySystem);
        trackingUtil.updateStatus(logTag, gdbConnection, prodInstId, ProductStatus.ACTIVE, updatedByUser, updatedBySystem);
        trackingUtil.updateTrackingType(logTag, gdbConnection, prodInstId, LeadTrackingType.INTERCEPTOR_TRACKING, updatedByUser, updatedBySystem);
        trackingUtil.insertInterceptorSettings(logTag, gdbConnection, prodInstId);
        assertEquals("http://www-domain-com.netsolads.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}",
                     trackingUtil.getVendorSitelinkDestinationUrlFromNs(logTag, pdbConnection, prodInstId, "http://www.domain.com/whatever.html?boop", VendorId.GOOGLE));
    }

    @Test
    public void getProductSecondaryUrlsTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertTrue(trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, null).isEmpty());
    }

    @Test
    public void getProductSecondaryUrlsTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[0]);
        assertTrue(trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, prodInstId).isEmpty());
    }

    @Test
    public void getProductSecondaryUrlsTest3() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[0]);
        assertTrue(trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, prodInstId).isEmpty());
    }

    @Test
    public void getProductSecondaryUrlsTest4() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        String productUrl = trackingUtil.getProductUrl(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.somethingelse.com", updatedByUser, updatedBySystem);
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "www.somethingelse.com");
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "somethingelse.com");
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[] {"www.example.com", "http://www.domain.com:8080/"});
        List<String> secondaryUrls = trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(2, secondaryUrls.size());
        assertTrue(secondaryUrls.contains("www.example.com"));
        assertTrue(secondaryUrls.contains("http://www.domain.com:8080/"));
        List<InterceptorMapping> mappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        assertEquals(4, mappings.size());
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, productUrl, updatedByUser, updatedBySystem);
    }

    @Test
    public void getProductSecondaryUrlsTest5() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        String productUrl = trackingUtil.getProductUrl(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.somethingelse.com", updatedByUser, updatedBySystem);
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "www.somethingelse.com");
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "somethingelse.com");
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[] {"www.example.com", "http://www.domain.com:8080/"});
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[] {"www.example.net"});
        List<String> secondaryUrls = trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(1, secondaryUrls.size());
        assertTrue(secondaryUrls.contains("www.example.net"));
        assertFalse(secondaryUrls.contains("www.example.com"));
        List<InterceptorMapping> mappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        assertEquals(5, mappings.size());
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, productUrl, updatedByUser, updatedBySystem);
    }

    @Test
    public void getProductSecondaryUrlsTest7() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        String productUrl = trackingUtil.getProductUrl(logTag, gdbConnection, prodInstId);
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, "www.example.xxx", updatedByUser, updatedBySystem);
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "www.example.xxx");
        trackingUtil.insertInterceptorMapping(logTag, gdbConnection, prodInstId, "example.xxx");
        trackingUtil.setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, new String[] {"www.example.biz", "www.example.xxx"});
        List<String> secondaryUrls = trackingUtil.getProductSecondaryUrls(logTag, pdbConnection, prodInstId);
        assertEquals(1, secondaryUrls.size());
        assertTrue(secondaryUrls.contains("www.example.biz"));
        Collection<InterceptorReplacement> replacements = interceptorReplacementHelper.getReplacements(logTag, gdbConnection, prodInstId);
        assertEquals(1, replacements.size());
        trackingUtil.updateUrl(logTag, gdbConnection, prodInstId, productUrl, updatedByUser, updatedBySystem);
    }

    @Test
    public void updateDestinationUrlHostNameAndPortTest1() {
        assertNull(TrackingUtil.updateDestinationUrlHostNameAndPort(null, 0, 0L, null, 0));
    }

    @Test
    public void updateDestinationUrlHostNameAndPortTest2() {
        assertNull(TrackingUtil.updateDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", 0, 0L, null, 0));
    }

    @Test
    public void updateDestinationUrlHostNameAndPortTest3() {
        assertEquals("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=43", TrackingUtil.updateDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", VendorId.GOOGLE, 43L, null, 0));
    }

    @Test
    public void updateDestinationUrlHostNameAndPortTest4() {
        assertEquals("http://www.example.net/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=41", TrackingUtil.updateDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&matchtype={matchtype}&adGroup=42", VendorId.GOOGLE, 41L, "www.example.net", 0));
    }

    @Test
    public void updateSitelinkDestinationUrlHostNameAndPortTest1() {
        assertNull(TrackingUtil.updateSitelinkDestinationUrlHostNameAndPort(null, 0, null, 0));
    }

    @Test
    public void updateSitelinkDestinationUrlHostNameAndPortTest2() {
        assertNull(TrackingUtil.updateSitelinkDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}", 0, null, 0));
    }

    @Test
    public void updateSitelinkDestinationUrlHostNameAndPortTest3() {
        assertEquals("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}", TrackingUtil.updateSitelinkDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}", VendorId.GOOGLE, null, 0));
    }

    @Test
    public void updateSitelinkDestinationUrlHostNameAndPortTest4() {
        assertEquals("http://www.example.net/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}", TrackingUtil.updateSitelinkDestinationUrlHostNameAndPort("http://www.example.com/whatever.html?boop&creative={creative}&keywords={keyword}&{copy:matchtype}&{copy:adGroup}", VendorId.GOOGLE, "www.example.net", 0));
    }

    @Test
    public void insertInterceptorReplacementForMappingTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping(prodInstId, "http://www.example.com");
        trackingUtil.insertInterceptorReplacementForMapping(logTag, gdbConnection, interceptorMapping);
        InterceptorReplacement interceptorReplacement = TrackingUtil.getInterceptorReplacementForMapping(interceptorMapping);
        assertEquals(0L, interceptorReplacementHelper.getLimitId(logTag, gdbConnection, interceptorReplacement).longValue());
    }

    @Test
    public void getAllInterceptorMappingsTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        assertTrue(trackingUtil.getAllInterceptorMappings(logTag, gdbConnection, null).isEmpty());
    }

    @Test
    public void getAllInterceptorMappingsTest2() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping(prodInstId, "http://www.example.com");
        interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals(1, trackingUtil.getAllInterceptorMappings(logTag, gdbConnection, prodInstId).size());
    }

    @Test
    public void deleteInterceptorReplacementForMappingTest1() throws SQLException {
        TrackingUtil trackingUtil = new TrackingUtil("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, gdbConnection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, gdbConnection, prodInstId);
        InterceptorMapping interceptorMapping = TrackingUtil.getInterceptorMapping(prodInstId, "http://www.example.com");
        trackingUtil.insertInterceptorReplacementForMapping(logTag, gdbConnection, interceptorMapping);
        assertEquals(1, interceptorReplacementHelper.getReplacements(logTag, gdbConnection, prodInstId).size());
        trackingUtil.deleteInterceptorReplacementForMapping(logTag, gdbConnection, interceptorMapping);
        assertTrue(interceptorReplacementHelper.getReplacements(logTag, gdbConnection, prodInstId).isEmpty());
    }
}
