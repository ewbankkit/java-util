/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class ConfigUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:39 ConfigUnitTest.java NSI";

    @Test
    public void getStringTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertNull(config.get(null));
    }

    @Test
    public void getStringTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertNull(config.get("boop"));
    }

    @Test
    public void getStringTest3() {
        Properties properties = new Properties();
        properties.put("goop", "yes");
        Config config = new Config(properties) {};
        assertNull(config.get("boop"));
    }

    @Test
    public void getStringTest4() {
        Properties properties = new Properties();
        properties.put("boop", "yes");
        Config config = new Config(properties) {};
        assertEquals("yes", config.get("boop"));
    }

    @Test
    public void getBooleanTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertFalse(config.getBoolean(null));
    }

    @Test
    public void getBooleanTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertFalse(config.getBoolean("boop"));
    }

    @Test
    public void getBooleanTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertFalse(config.getBoolean("boop"));
    }

    @Test
    public void getBooleanTest4() {
        Properties properties = new Properties();
        properties.put("boop", "true");
        Config config = new Config(properties) {};
        assertTrue(config.getBoolean("boop"));
    }

    @Test
    public void getIntTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0, config.getInt(null));
    }

    @Test
    public void getIntTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0, config.getInt("boop"));
    }

    @Test
    public void getIntTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertEquals(0, config.getInt("boop"));
    }

    @Test
    public void getIntTest4() {
        Properties properties = new Properties();
        properties.put("boop", "42");
        Config config = new Config(properties) {};
        assertEquals(42, config.getInt("boop"));
    }

    @Test
    public void getDoubleTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0D, config.getDouble(null), 0D);
    }

    @Test
    public void getDoubleTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0D, config.getDouble("boop"), 0D);
    }

    @Test
    public void getDoubleTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertEquals(0D, config.getDouble("boop"), 0D);
    }

    @Test
    public void getDoubleTest4() {
        Properties properties = new Properties();
        properties.put("boop", "42.24");
        Config config = new Config(properties) {};
        assertEquals(42.24D, config.getDouble("boop"), 0D);
    }

    @Test
    public void getFloatTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0F, config.getFloat(null), 0F);
    }

    @Test
    public void getFloatTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0F, config.getFloat("boop"), 0F);
    }

    @Test
    public void getFloatTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertEquals(0F, config.getFloat("boop"), 0F);
    }

    @Test
    public void getFloatTest4() {
        Properties properties = new Properties();
        properties.put("boop", "42.24");
        Config config = new Config(properties) {};
        assertEquals(42.24F, config.getFloat("boop"), 0F);
    }

    @Test
    public void getLongTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0L, config.getLong(null));
    }

    @Test
    public void getLongTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertEquals(0L, config.getLong("boop"));
    }

    @Test
    public void getLongTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertEquals(0L, config.getLong("boop"));
    }

    @Test
    public void getLongTest4() {
        Properties properties = new Properties();
        properties.put("boop", "42");
        Config config = new Config(properties) {};
        assertEquals(42L, config.getLong("boop"));
    }

    @Test
    public void getListTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getList(null).isEmpty());
    }

    @Test
    public void getListTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getList("boop").isEmpty());
    }

    @Test
    public void getListTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertEquals(1, config.getList("boop").size());
        assertEquals("nein", config.getList("boop").get(0));
    }

    @Test
    public void getListTest4() {
        Properties properties = new Properties();
        properties.put("boop", "goop  42 ");
        Config config = new Config(properties) {};
        assertEquals(2, config.getList("boop").size());
        assertEquals("goop", config.getList("boop").get(0));
        assertEquals("42", config.getList("boop").get(1));
    }

    @Test
    public void getListTest5() {
        Properties properties = new Properties();
        properties.put("boop", "77 -2");
        Config config = new Config(properties) {};
        assertEquals(2, config.getList("boop", Integer.class).size());
        assertEquals(77, config.getList("boop", Integer.class).get(0).intValue());
        assertEquals(-2, config.getList("boop", Integer.class).get(1).intValue());
    }

    @Test
    public void getTableTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getTable(null).isEmpty());
    }

    @Test
    public void getTableTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getTable("boop").isEmpty());
    }

    @Test
    public void getTableTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertTrue(config.getTable("boop").isEmpty());
    }

    @Test
    public void getTableTest4() {
        Properties properties = new Properties();
        properties.put("boop.a", "goop");
        Config config = new Config(properties) {};
        assertEquals(1, config.getTable("boop").size());
        assertEquals("goop", config.getTable("boop").get("a"));
    }

    @Test
    public void getTableTest5() {
        Properties properties = new Properties();
        properties.put("boop.a", "goop");
        properties.put("boop.b", "doop");
        Config config = new Config(properties) {};
        assertEquals(2, config.getTable("boop").size());
        assertEquals("goop", config.getTable("boop").get("a"));
        assertEquals("doop", config.getTable("boop").get("b"));
    }

    @Test
    public void getTableTest6() {
        Properties properties = new Properties();
        properties.put("boop.a", "true");
        properties.put("boop.b", "false");
        Config config = new Config(properties) {};
        assertEquals(2, config.getTable("boop", Boolean.class).size());
        assertTrue(config.getTable("boop", Boolean.class).get("a"));
        assertFalse(config.getTable("boop", Boolean.class).get("b"));
    }

    @Test
    public void getTableTest7() {
        Properties properties = new Properties();
        properties.put("boop.a", "-1");
        properties.put("boop.b", "1");
        Config config = new Config(properties) {};
        assertEquals(2, config.getTable("boop", Long.class).size());
        assertEquals(-1L, config.getTable("boop", Long.class).get("a").longValue());
        assertEquals(1L, config.getTable("boop", Long.class).get("b").longValue());
    }

    @Test
    public void getTableTest8() {
        Properties properties = new Properties();
        properties.put("boop.a", "AA BB CC");
        properties.put("boop.b", "XX YY ZZ");
        Config config = new Config(properties) {};
        assertEquals(2, config.getTable("boop", List.class).size());
        assertEquals(3, config.getTable("boop", List.class).get("a").size());
        assertEquals("YY", config.getTable("boop", List.class, String.class).get("b").get(1));
    }

    @Test
    public void getTableTest9() {
        Properties properties = new Properties();
        properties.put("boop.a", "0.9 -8.5 100.01");
        Config config = new Config(properties) {};
        assertEquals(1, config.getTable("boop", List.class).size());
        assertEquals(-8.5D, config.getTable("boop", List.class, Double.class).get("a").get(1));
    }

    @Test
    public void getListOfPairsTest1() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getListOfPairs(null, null, null).isEmpty());
    }

    @Test
    public void getListOfPairsTest2() {
        Properties properties = new Properties();
        Config config = new Config(properties) {};
        assertTrue(config.getListOfPairs("boop", "from", "to").isEmpty());
    }

    @Test
    public void getListOfPairsTest3() {
        Properties properties = new Properties();
        properties.put("boop", "nein");
        Config config = new Config(properties) {};
        assertTrue(config.getListOfPairs("boop", "from", "to").isEmpty());
    }

    @Test
    public void getListOfPairsTest4() {
        Properties properties = new Properties();
        properties.put("boop.0.from", "ff");
        Config config = new Config(properties) {};
        assertTrue(config.getListOfPairs("boop", "from", "to").isEmpty());
    }

    @Test
    public void getListOfPairsTest5() {
        Properties properties = new Properties();
        properties.put("boop.0.to", "tt");
        Config config = new Config(properties) {};
        assertTrue(config.getListOfPairs("boop", "from", "to").isEmpty());
    }

    @Test
    public void getListOfPairsTest6() {
        Properties properties = new Properties();
        properties.put("boop.0.from", "ff");
        properties.put("boop.0.to", "tt");
        Config config = new Config(properties) {};
        assertEquals(1, config.getListOfPairs("boop", "from", "to").size());
        assertEquals("ff", config.getListOfPairs("boop", "from", "to").get(0).getFirst());
        assertEquals("tt", config.getListOfPairs("boop", "from", "to").get(0).getSecond());
    }

    @Test
    public void getListOfPairsTest7() {
        Properties properties = new Properties();
        properties.put("boop.0.from", "ff0");
        properties.put("boop.0.to", "tt0");
        properties.put("boop.1.from", "ff1");
        properties.put("boop.1.to", "tt1");
        Config config = new Config(properties) {};
        assertEquals(2, config.getListOfPairs("boop", "from", "to").size());
        assertEquals("ff0", config.getListOfPairs("boop", "from", "to").get(0).getFirst());
        assertEquals("tt0", config.getListOfPairs("boop", "from", "to").get(0).getSecond());
        assertEquals("ff1", config.getListOfPairs("boop", "from", "to").get(1).getFirst());
        assertEquals("tt1", config.getListOfPairs("boop", "from", "to").get(1).getSecond());
    }

    @Test
    public void getListOfPairsTest8() {
        Properties properties = new Properties();
        properties.put("boop.0.from", "0");
        properties.put("boop.0.to", "false");
        properties.put("boop.1.from", "1");
        properties.put("boop.1.to", "true");
        Config config = new Config(properties) {};
        assertEquals(2, config.getListOfPairs("boop", "from", Integer.class, "to", Boolean.class).size());
        assertEquals(0, config.getListOfPairs("boop", "from", Integer.class, "to", Boolean.class).get(0).getFirst().intValue());
        assertFalse(config.getListOfPairs("boop", "from", Integer.class, "to", Boolean.class).get(0).getSecond().booleanValue());
        assertEquals(1, config.getListOfPairs("boop", "from", Integer.class, "to", Boolean.class).get(1).getFirst().intValue());
        assertTrue(config.getListOfPairs("boop", "from", Integer.class, "to", Boolean.class).get(1).getSecond().booleanValue());
    }

    @Test
    public void getListOfPairsTest9() {
        Properties properties = new Properties();
        properties.put("boop.0", "a");
        properties.put("boop.0.modules", "A AA AAA");
        properties.put("boop.1", "b");
        properties.put("boop.1.modules", "B BBBB");
        Config config = new Config(properties) {};
        assertEquals(2, config.getListOfPairs("boop", null, String.class, "modules", List.class).size());
        assertEquals(2, config.getListOfPairs("boop", null, String.class, "modules", List.class).get(1).getSecond().size());
    }
}
