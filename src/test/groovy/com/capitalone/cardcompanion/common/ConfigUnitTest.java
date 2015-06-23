//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import org.apache.commons.configuration.ConversionException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test application configuration.
 */
public final class ConfigUnitTest {
    private Config config;

    @Before
    public void before() {
        System.setProperty("config.name", "common");
        System.setProperty("common.env", "test");
        config = Config.getInstance();
    }

    @Test
    public void testGetString1() {
        Optional<String> value = config.getString("test.a");
        assertTrue(value.isPresent());
        assertEquals("Present", value.get());
    }

    @Test
    public void testGetString2() {
        Optional<String> value = config.getString("test.b");
        assertTrue(value.isPresent());
        assertEquals("true", value.get());
    }

    @Test
    public void testGetString3() {
        Optional<String> value = config.getString("test.c");
        assertTrue(value.isPresent());
        assertEquals("100", value.get());
    }

    @Test
    public void testGetString4() {
        Optional<String> value = config.getString("test.d");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetString5() {
        String value = config.getString("test.d", "DEFAULT VALUE");
        assertEquals("DEFAULT VALUE", value);
    }

    @Test
    public void testGetString6() {
        Optional<String> value = config.getString("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetString7() {
        Optional<String> value = config.getString("test.g");
        assertTrue(value.isPresent());
        assertEquals(System.getProperty("java.version"), value.get());
    }

    @Test
    public void testGetString8() {
        Optional<String> value = config.getString("test.h");
        assertTrue(value.isPresent());
        assertEquals(System.getProperty("user.name"), value.get());
    }

    @Test
    public void testGetString9() {
        Optional<String> value = config.getString("test.i");
        assertTrue(value.isPresent());
        assertEquals(String.format("/test/%s/1234/%s/more", System.getProperty("user.name"), System.getProperty("java.version")), value.get());
    }

    @Test
    public void testGetString10() {
        Optional<String> value = config.getString("test.j");
        assertTrue(value.isPresent());
        assertEquals("/test/${does.not.exist}/1234", value.get());
    }

    @Test
    public void testGetString11() {
        Optional<String> value = config.getString("test.k");
        assertTrue(value.isPresent());
        assertEquals("10", value.get());
    }

    @Test
    public void testGetString12() {
        Optional<String> value = config.getString("test.l");
        assertTrue(value.isPresent());
    }

    @Test
    public void testGetString13() {
        Optional<String> value = config.getString("test.o");
        assertTrue(value.isPresent());
        assertEquals("XYZ", value.get());
    }

    @Test
    public void testGetString14() {
        Optional<String> value = config.getString("test.p");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetInteger1() {
        Optional<Integer> value = config.getInteger("test.c");
        assertTrue(value.isPresent());
        assertEquals(100, value.get().intValue());
    }

    @Test
    public void testGetInteger2() {
        Optional<Integer> value = config.getInteger("test.e");
        assertTrue(value.isPresent());
        assertEquals(-123, value.get().intValue());
    }

    @Test(expected = ConversionException.class)
    public void testGetInteger3() {
        Optional<Integer> value = config.getInteger("test.a");
        assertFalse(value.isPresent());
    }

    @Test(expected = ConversionException.class)
    public void testGetInteger4() {
        int value = config.getInteger("test.a", 42);
        assertEquals(42, value);
    }

    @Test
    public void testGetInteger5() {
        Optional<Integer> value = config.getInteger("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetInteger6() {
        int value = config.getInteger("test.abc", 42);
        assertEquals(42, value);
    }

    @Test
    public void testGetLong1() {
        Optional<Long> value = config.getLong("test.c");
        assertTrue(value.isPresent());
        assertEquals(100L, value.get().longValue());
    }

    @Test
    public void testGetLong2() {
        Optional<Long> value = config.getLong("test.e");
        assertTrue(value.isPresent());
        assertEquals(-123L, value.get().longValue());
    }

    @Test(expected = ConversionException.class)
    public void testGetLong3() {
        Optional<Long> value = config.getLong("test.a");
        assertFalse(value.isPresent());
    }

    @Test(expected = ConversionException.class)
    public void testGetLong4() {
        long value = config.getLong("test.a", Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, value);
    }

    @Test
    public void testGetLong5() {
        Optional<Long> value = config.getLong("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetLong6() {
        long value = config.getLong("test.abc", Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, value);
    }

    @Test
    public void testGetBoolean1() {
        Optional<Boolean> value = config.getBoolean("test.b");
        assertTrue(value.isPresent());
        assertTrue(value.get());
    }

    @Test(expected = ConversionException.class)
    public void testGetBoolean2() {
        Optional<Boolean> value = config.getBoolean("test.a");
        assertTrue(value.isPresent());
        assertFalse(value.get());
    }

    @Test
    public void testGetBoolean3() {
        Optional<Boolean> value = config.getBoolean("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetBoolean4() {
        boolean value = config.getBoolean("test.abc", true);
        assertTrue(value);
    }

    @Test
    public void testGetList1() {
        Optional<List<String>> value = config.getList("test.a");
        assertTrue(value.isPresent());
        assertEquals(1, value.get().size());
        assertEquals("Present", value.get().get(0));
    }

    @Test
    public void testGetList2() {
        Optional<List<String>> value = config.getList("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetList3() {
        Optional<List<String>> value = config.getList("test.f");
        assertTrue(value.isPresent());
        assertEquals(3, value.get().size());
        assertEquals("First", value.get().get(0));
        assertEquals("Second", value.get().get(1));
        assertEquals("Third", value.get().get(2));
    }

    @Test
    public void testGetMap1() {
        Optional<Map<String, String>> value = config.getMap("test.m");
        assertTrue(value.isPresent());
        assertEquals(2, value.get().size());
        assertEquals("Un", value.get().get("A"));
        assertEquals("Deux Trois", value.get().get("B"));
    }

    @Test
    public void testGetMap2() {
        Optional<Map<String, String>> value = config.getMap("test.abc");
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetBigDecimal1() {
        Optional<BigDecimal> value = config.getBigDecimal("test.n");
        assertTrue(value.isPresent());
        assertEquals(0, BigDecimal.valueOf(12.34).compareTo(value.get()));
    }

    @Test
    public void testGetProperties1() {
        Properties properties = config.getProperties();
        assertNotNull(properties);
        String property = properties.getProperty("test.n");
        assertEquals("12.34", property);
    }

    @Test
    public void testGetMultimap1() {
        Optional<Map<String, String>> mapValue = config.getMap("test.q");
        assertTrue(mapValue.isPresent());
        assertEquals(3, mapValue.get().size());
        assertEquals("999 99", mapValue.get().get("BBB"));

        Optional<Multimap<String, String>> multimapValue = config.getMultimap("test.q");
        assertTrue(multimapValue.isPresent());
        assertFalse(multimapValue.get().isEmpty());
        assertEquals(3, multimapValue.get().keySet().size());
        assertEquals(6, multimapValue.get().size());

        assertEquals(2, multimapValue.get().get("BBB").size());
        assertTrue(multimapValue.get().get("BB").isEmpty());
    }
}
