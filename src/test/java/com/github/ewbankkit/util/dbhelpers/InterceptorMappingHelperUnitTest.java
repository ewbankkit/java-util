/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.InterceptorMapping;
import com.netsol.adagent.util.dbhelpers.InterceptorMappingHelper;

public class InterceptorMappingHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:47 InterceptorMappingHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getMappingByAliasTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertNull(interceptorMappingHelper.getMappingByAlias(logTag, connection, null));
    }

    @Test
    public void getMappingByAliasTest2() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        assertNull(interceptorMappingHelper.getMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com"));
    }

    @Test
    public void getMappingByAliasTest3() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        interceptorMapping = interceptorMappingHelper.getMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        assertEquals("oh-my-this-is-a-very-long-test-mapping.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getMappingByProdInstIdAndAliasTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertNull(interceptorMappingHelper.getMappingByProdInstIdAndAlias(logTag, connection, null, null));
    }

    @Test
    public void getMappingByProdInstIdAndAliasTest2() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAliasAndProdInstId(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com", prodInstId);
        assertNull(interceptorMappingHelper.getMappingByProdInstIdAndAlias(logTag, connection, prodInstId, "oh-my-this-is-a-very-long-test-mapping.netsolads.com"));
    }

    @Test
    public void getMappingByProdInstIdAndAliasTest3() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAliasAndProdInstId(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com", prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        interceptorMapping = interceptorMappingHelper.getMappingByProdInstIdAndAlias(logTag, connection, prodInstId, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        assertEquals("oh-my-this-is-a-very-long-test-mapping.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getMappingByProdInstIdAndRealHostTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertNull(interceptorMappingHelper.getMappingByProdInstIdAndRealHost(logTag, connection, null, null));
    }

    @Test
    public void getMappingByProdInstIdAndRealHostTest2() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        assertNull(interceptorMappingHelper.getMappingByProdInstIdAndRealHost(logTag, connection, prodInstId, "www.example.com"));
    }

    @Test
    public void getMappingByProdInstIdAndRealHostTest3() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingByAlias(logTag, connection, "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        interceptorMapping = interceptorMappingHelper.getMappingByProdInstIdAndRealHost(logTag, connection, prodInstId, "www.example.com");
        assertEquals("oh-my-this-is-a-very-long-test-mapping.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getMappingsByProdInstIdTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertTrue(interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, null).isEmpty());
    }

    @Test
    public void getMappingsByProdInstIdTest2() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, connection, prodInstId);
        assertTrue(interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getMappingsByProdInstIdTest3() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, connection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        List<InterceptorMapping> interceptorMappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, prodInstId);
        assertEquals(1, interceptorMappings.size());
        interceptorMapping = interceptorMappings.get(0);
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getMappingsByProdInstIdTest4() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, connection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        interceptorMappingHelper.updateAlias(logTag, connection, prodInstId, "www-example-com.netsolads.com", "oh-my-this-is-a-very-long-test-mapping.netsolads.com");
        List<InterceptorMapping> interceptorMappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, prodInstId);
        assertEquals(1, interceptorMappings.size());
        interceptorMapping = interceptorMappings.get(0);
        assertEquals("oh-my-this-is-a-very-long-test-mapping.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getMappingsByProdInstIdTest5() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, connection, prodInstId);
        InterceptorMapping interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("www-example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("www.example.com");
        interceptorMapping.setRealPort(8080);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        interceptorMapping = new InterceptorMapping();
        interceptorMapping.setAlias("example-com.netsolads.com");
        interceptorMapping.setProdInstId(prodInstId);
        interceptorMapping.setRealHost("example.com");
        interceptorMapping.setRealPort(81);
        interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        List<InterceptorMapping> interceptorMappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, prodInstId);
        assertEquals(2, interceptorMappings.size());
        interceptorMapping = interceptorMappings.get(0);
        assertEquals("example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("example.com", interceptorMapping.getRealHost());
        assertEquals(81, interceptorMapping.getRealPort());
        interceptorMapping = interceptorMappings.get(1);
        assertEquals("www-example-com.netsolads.com", interceptorMapping.getAlias());
        assertEquals(prodInstId, interceptorMapping.getProdInstId());
        assertEquals("www.example.com", interceptorMapping.getRealHost());
        assertEquals(8080, interceptorMapping.getRealPort());
    }

    @Test
    public void getPrimaryAliasTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertNull(interceptorMappingHelper.getPrimaryAlias(logTag, connection, null));
    }

    @Test
    public void getPrimaryInterceptorMappingTest1() throws SQLException {
        InterceptorMappingHelper interceptorMappingHelper = new InterceptorMappingHelper("");
        assertNull(interceptorMappingHelper.getPrimaryInterceptorMapping(logTag, connection, null));
    }
}
