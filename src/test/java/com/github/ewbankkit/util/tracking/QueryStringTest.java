/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import com.github.ewbankkit.util.F1;
import org.junit.Test;

import com.netsol.adagent.util.tracking.QueryString;

public class QueryStringTest {
    @Test
    public void testParse1() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        assertEquals("Query string: ", "parameter1=value1", queryString.toString());
    }

    @Test
    public void testParse2() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter3=value3&parameter2=value2&parameter2=value2.1");
        assertEquals("Query string: ", "parameter1=value1&parameter3=value3&parameter2=value2&parameter2=value2.1", queryString.toString());
    }

    @Test
    public void testParse3() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter2&parameter3=value3");
        assertEquals("Query string: ", "parameter1=value1&parameter2&parameter3=value3", queryString.toString());
    }

    @Test
    public void testParse4() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter2=&parameter3=value3&parameter4&parameter3=value3");
        assertEquals("Query string: ", "parameter1=value1&parameter2=&parameter3=value3&parameter3=value3&parameter4", queryString.toString());
    }
    @Test
    public void testParse5() {
        QueryString queryString = QueryString.fromString("&parameter1=value1&parameter1=&parameter1&=value2");
        assertEquals("Query string: ", "parameter1=value1&parameter1=&parameter1&=value2", queryString.toString());
    }

    @Test
    public void testGet1() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        assertEquals("value1", queryString.getFirst("parameter1"));
    }

    @Test
    public void testGet2() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        assertEquals("value1", queryString.getLast("parameter1"));
    }

    @Test
    public void testGet3() {
        QueryString queryString = QueryString.fromString("parameter2=value2&parameter1=value1");
        assertEquals("value1", queryString.getFirst("parameter1"));
    }

    @Test
    public void testGet4() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter2=value2&parameter1=value1.1");
        assertEquals("value1.1", queryString.getLast("parameter1"));
    }

    @Test
    public void testGet5() {
        QueryString queryString = QueryString.fromString("parameter2=value2&parameter1=value1");
        assertNull(queryString.getFirst("parameter3"));
    }

    @Test
    public void testGet6() {
        QueryString queryString = QueryString.fromString("parameter2=value2&parameter1=value1");
        assertNull(queryString.getLast("parameter3"));
    }

    @Test
    public void testGet7() {
        QueryString queryString = QueryString.fromString("parameter1");
        assertNull(queryString.getFirst("parameter1"));
    }

    @Test
    public void testGet8() {
        QueryString queryString = QueryString.fromString("parameter1");
        assertNull(queryString.getFirst(null));
    }

    @Test
    public void testGet9() {
        QueryString queryString = QueryString.fromString("parameter1=");
        assertEquals("", queryString.getFirst("parameter1"));
    }

    @Test
    public void testGet10() {
        QueryString queryString = QueryString.fromString("=value1");
        assertEquals("value1", queryString.getFirst(""));
    }

    @Test
    public void testEquals1() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1");
        assertEquals("Query string: ", queryString1, queryString1);
    }

    @Test
    public void testEquals2() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1");
        QueryString queryString2 = QueryString.fromString("parameter1=value1");
        assertEquals("Query string: ", queryString1, queryString2);
    }

    @Test
    public void testEquals3() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1&parameter3=value3&parameter2=value2&parameter2=value2.1");
        QueryString queryString2 = QueryString.fromString("parameter2=value2&parameter3=value3&parameter2=value2.1&parameter1=value1");
        assertEquals("Query string: ", queryString1, queryString2);
    }

    @Test
    public void testEquals4() {
        QueryString queryString1 = QueryString.fromString("&parameter1=value1&parameter1=&parameter1&=value2");
        QueryString queryString2 = QueryString.fromString("parameter1=value1&&parameter1=&&&parameter1&=value2");
        assertEquals("Query string: ", queryString1, queryString2);
    }

    @Test
    public void testUrlEncoding1() {
        QueryString queryString = QueryString.fromUrlEncodedString("keywords=refurbished%20at%26t%20phones&creative=3274520970&adGroup=14750", "UTF-8");
        assertEquals("Keywords: ", "refurbished at&t phones", queryString.getFirst("keywords"));
    }

    @Test
    public void testUrlEncoding2() {
        QueryString queryString = QueryString.fromUrlEncodedString("keywords=refurbished%20at%26t%20phones&creative=3274520970&adGroup=14750", "UTF-8");
        assertEquals("Keywords: ", "keywords=refurbished+at%26t+phones&creative=3274520970&adGroup=14750", queryString.toUrlEncodedString("UTF-8"));
    }

    @Test
    public void testUrlEncoding3() {
        QueryString queryString = QueryString.fromUrlEncodedString("ctl00$ContentPlaceHolder1$imgSearch.x=33&ctl00$ContentPlaceHolder1$city=los angeles 90019&ctl00$ContentPlaceHolder1$distance=10&ctl00$ContentPlaceHolder1$imgSearch.y=11&ctl00$ContentPlaceHolder1$state=California", "UTF-8");
        assertEquals("33", queryString.getFirst("ctl00$ContentPlaceHolder1$imgSearch.x"));
    }

    @Test
    public void testUrlEncoding4() {
        QueryString queryString = QueryString.fromUrlEncodedString("storeLocator1%24txtZip=11102&storeLocator1%24txtCity=&storeLocator1%24txtState=&storeLocator1%24ddlResults=10&storeLocator1%24btnSubmit=", "UTF-8");
        assertEquals("11102", queryString.getFirst("storeLocator1$txtZip"));
    }

    @Test
    public void testContains1() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        assertTrue(queryString1.contains(queryString1));
    }

    @Test
    public void testContains2() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("");
        assertTrue(queryString1.contains(queryString2));
    }

    @Test
    public void testContains3() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789");
        assertTrue(queryString1.contains(queryString2));
    }

    @Test
    public void testContains4() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&a=ghghg");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains5() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("cz=t&aaa=789");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains6() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&zz=top");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains7() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&zz=top&pp=abc");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains8() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&aaa=1");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains9() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=1&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&aaa=1");
        assertTrue(queryString1.contains(queryString2));
    }

    @Test
    public void testContains10() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=1&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&aaa=1&xy=uuu");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains11() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=1&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&aaa=2");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains12() {
        QueryString queryString1 = QueryString.fromString("pp=abc&aaa=1&aaa=789");
        QueryString queryString2 = QueryString.fromString("aaa=789&aaa=");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testContains13() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1.1&parameter2=value2.1&parameter3=value3&parameter2=value2.2&parameter1=value1.2");
        QueryString queryString2 = QueryString.fromString("parameter3=value3&parameter1");
        assertFalse(queryString1.contains(queryString2));
    }

    @Test
    public void testAdd1() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("parameter2", "value2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter2=value2");
    }

    @Test
    public void testAdd2() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter3=value3");
        queryString.add("parameter2", "value2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter3=value3&parameter2=value2");
    }

    @Test
    public void testAdd3() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("parameter1", "value2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter1=value2");
    }

    @Test
    public void testAdd4() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("parameter1", "value0");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter1=value0");
    }

    @Test
    public void testAdd5() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("parameter2", "");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter2=");
    }

    @Test
    public void testAdd6() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("parameter2", null);
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter2");
    }

    @Test
    public void testAdd7() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add("", "value2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&=value2");
    }

    @Test
    public void testAdd8() {
        QueryString queryString = QueryString.fromString("parameter1=value1");
        queryString.add(null, "value2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1");
    }

    @Test
    public void testMerge1() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1&parameter3=value3.1");
        QueryString queryString2 = QueryString.fromString("parameter2=value2&parameter3=value3.2");
        queryString1.merge(queryString2);
        assertEquals("Query string: ", queryString1.toString(), "parameter1=value1&parameter3=value3.1&parameter3=value3.2&parameter2=value2");
    }

    @Test
    public void testMerge2() {
        QueryString queryString1 = QueryString.fromString("parameter1=value1&parameter3=value3.1");
        QueryString queryString2 = QueryString.fromString("parameter2=value2&parameter3=value3.1");
        queryString1.merge(queryString2);
        assertEquals("Query string: ", queryString1.toString(), "parameter1=value1&parameter3=value3.1&parameter2=value2");
    }

    @Test
    public void testRemove() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter3=value3.1&parameter2=value2&parameter3=value3.2");
        queryString.remove("parameter2");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1&parameter3=value3.1&parameter3=value3.2");
        queryString.removeIgnoreCase("PARAMETER3");
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1");
        queryString.removeRegex(Pattern.compile("q.*"));
        assertEquals("Query string: ", queryString.toString(), "parameter1=value1");
        queryString.removeRegex(Pattern.compile("p.*"));
        assertEquals("Query string: ", queryString.toString(), "");
    }

    @Test
    public void testTransform1() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter3=value3.1&parameter2=value2&parameter3=value3.2");
        F1<String, String> f1 = new F1<String, String>() {
            @Override
            public String apply(String a) throws Exception {
                return a;
            }};
        assertEquals(queryString, queryString.transform(f1));
    }

    @Test
    public void testTransform2() {
        QueryString queryString = QueryString.fromString("parameter1=value1&parameter3=value3.1&parameter2=value2&parameter3=value3.2");
        F1<String, String> f1 = new F1<String, String>() {
            @Override
            public String apply(String a) throws Exception {
                return a.concat("-new");
            }};
        queryString = queryString.transform(f1);
        assertEquals("value2", queryString.getFirst("parameter2-new"));
    }
}
