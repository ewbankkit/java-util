/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import com.netsol.adagent.util.PropertiesBuilder;

public class PropertiesBuilderUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:05 PropertiesBuilderUnitTest.java NSI";

    @Test
    public void propertiesBuilderTest1() throws Exception {
        PropertiesBuilder pb = new PropertiesBuilder();
        assertTrue(pb.toProperties().size() == 0);
    }

    @Test
    public void propertiesBuilderTest2() throws Exception {
        PropertiesBuilder pb = new PropertiesBuilder();
        pb.setProperty("A", "Apple");
        Properties properties = pb.toProperties();
        assertTrue(properties.size() == 1);
        assertEquals("Apple", properties.get("A"));
    }

    @Test
    public void propertiesBuilderTest3() throws Exception {
        PropertiesBuilder pb = new PropertiesBuilder();
        pb.setProperty("A", "Apple");
        Properties properties = pb.toProperties();
        pb.setProperty("B", "Beer");
        assertTrue(properties.size() == 1);
        assertEquals("Apple", properties.get("A"));
        properties = pb.toProperties();
        assertTrue(properties.size() == 2);
        assertEquals("Apple", properties.get("A"));
        assertEquals("Beer", properties.get("B"));
    }
}
