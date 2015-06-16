/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.xmlrpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.netsol.adagent.util.xmlrpc.XmlRpcClientUtil;

public class XmlRpcClientUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:38 XmlRpcClientUtilUnitTest.java NSI";

    @Test
    public void fromResponseTest1() throws Exception {
        @SuppressWarnings("unused")
        class C {
            public String data;
        };
        C c = XmlRpcClientUtil.fromResponse(null, new C());
        assertNotNull(c);
    }

    @Test
    public void fromResponseTest2() throws Exception {
        class C {
            public String data;
        };
        Map<String, String> response = new HashMap<String, String>();
        response.put("data", "Hello");
        C c = XmlRpcClientUtil.fromResponse(response, new C());
        assertEquals("Hello", c.data);
    }

    @Test
    public void fromResponseTest3() throws Exception {
        class C {
            public String data;
            public int number;
            public String[] values;
        };
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("data", "Hello");
        response.put("number", 42);
        response.put("values", new Object[] {"First", "Second"});
        C c = XmlRpcClientUtil.fromResponse(response, new C());
        assertEquals("Hello", c.data);
        assertEquals(42, c.number);
        assertNotNull(c.values);
        assertEquals(2, c.values.length);
        assertEquals("First", c.values[0]);
        assertEquals("Second", c.values[1]);
    }

    @Test
    public void fromResponseTest4() throws Exception {
        class C {
            class D {
                public String[] values;
            }

            public String data;
            public int number;
            final public D someValues = new D();
        };
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("data", "Hello");
        response.put("number", 42);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("values", new Object[] {"First", "Second"});
        response.put("someValues", values);
        C c = XmlRpcClientUtil.fromResponse(response, new C());
        assertEquals("Hello", c.data);
        assertEquals(42, c.number);
        assertNotNull(c.someValues);
        assertNotNull(c.someValues.values);
        assertEquals(2, c.someValues.values.length);
        assertEquals("First", c.someValues.values[0]);
        assertEquals("Second", c.someValues.values[1]);
    }

    @Test
    public void toRequestTest1() throws Exception {
        class C {
            public String data;
        };
        C c = new C();
        c.data = "Hello";
        Map<String, Object> request = XmlRpcClientUtil.toRequest(c);
        assertNotNull(request);
        assertEquals(1, request.size());
        assertEquals("Hello", request.get("data"));
    }

    @Test
    public void toRequestTest2() throws Exception {
        class C {
            public String data;
            public int number;
        };
        C c = new C();
        c.number = 42;
        Map<String, Object> request = XmlRpcClientUtil.toRequest(c);
        assertNotNull(request);
        assertEquals(1, request.size());
        assertEquals(42, request.get("number"));
    }

    @Test
    public void toRequestTest3() throws Exception {
        class C {
            public String data;
            public int number;
            public String[] values;
        };
        C c = new C();
        c.number = 42;
        c.values = new String[] {"First", "Second"};
        Map<String, Object> request = XmlRpcClientUtil.toRequest(c);
        assertNotNull(request);
        assertEquals(2, request.size());
        assertEquals(42, request.get("number"));
        assertNotNull(request.get("values"));
        assertEquals(2, ((Object[])request.get("values")).length);
    }
}
