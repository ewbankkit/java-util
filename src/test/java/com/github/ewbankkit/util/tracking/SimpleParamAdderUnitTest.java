/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.netsol.adagent.util.tracking.SimpleParamAdder;

public class SimpleParamAdderUnitTest {
    private static final String[][] TO_ADD_1 = new String[][] {new String[] {"param1", "value1"}, new String[] {"param2", "value2"}};
    private static final String[][] TO_ADD_2 = new String[][] {new String[] {"param1", "value1"}, new String[] {"param2", "value2"}, new String[] {}};

    @Test
    public void addParams1() {
        assertEquals(
                "http://www.example.com/?param3=value3&param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com/?param3=value3"));
    }

    @Test
    public void addParams2() {
        assertEquals(
                "http://www.example.com/?param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com/"));
    }

    @Test
    public void addParams3() {
        assertEquals(
                "http://www.example.com/?param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com/?"));
    }

    @Test
    public void addParams4() {
        assertEquals(
                "http://www.example.com/?param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com"));
    }

    @Test
    public void addParams5() {
        assertEquals(
                "http://www.example.com/?param3&param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com/?param3"));
    }

    @Test
    public void addParams6() {
        assertEquals(
                "http://www.example.com/?param3=&param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_1).addParams("http://www.example.com/?param3="));
    }

    @Test
    public void addParams7() {
        assertEquals(
                "http://www.example.com/?param3=value3&param1=value1&param2=value2",
                new SimpleParamAdder(TO_ADD_2).addParams("http://www.example.com/?param3=value3"));
    }
}
