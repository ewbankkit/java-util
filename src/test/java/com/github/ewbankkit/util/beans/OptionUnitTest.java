/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.ewbankkit.util.F1;
import org.junit.Test;

public class OptionUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:56 OptionUnitTest.java NSI";

    @Test
    public void optionTest1() {
        Option<Object> option = Option.none();
        assertTrue(option.isNone());
        assertFalse(option.isSome());
    }

    @Test(expected = NullPointerException.class)
    public void optionTest2() {
        Option.none().getValue();
    }

    @Test
    public void optionTest3() {
        Option<Object> option = Option.some(null);
        assertTrue(option.isSome());
        assertFalse(option.isNone());
        assertEquals(null, option.getValue());
    }

    @Test
    public void optionTest4() {
        Option<String> option = Option.some("boop");
        assertTrue(option.isSome());
        assertFalse(option.isNone());
        assertEquals("boop", option.getValue());
    }

    @Test
    public void optionTest5() {
        assertTrue(Option.none().equals(Option.none()));
    }

    @Test
    public void optionTest6() {
        assertTrue(Option.some(null).equals(Option.some(null)));
    }

    @Test
    public void optionTest7() {
        assertFalse(Option.some(null).equals(Option.none()));
    }

    @Test
    public void optionTest8() {
        assertTrue(Option.some("boop").equals(Option.some("boop")));
    }

    @Test
    public void optionTest9() {
        assertFalse(Option.some("boop").equals(Option.some("goop")));
    }

    @Test
    public void optionTest10() {
        assertTrue(Option.none().toArray().length == 0);
    }

    @Test
    public void optionTest11() {
        assertTrue(Option.none().toList().isEmpty());
    }

    @Test
    public void optionTest12() {
        Option<Object> option = Option.some(null);
        assertTrue(option.toArray().length == 1);
        assertArrayEquals(new Object[] {null}, option.toArray());
    }

    @Test
    public void optionTest13() {
        Option<String> option = Option.some("boop");
        assertTrue(option.toArray().length == 1);
        assertArrayEquals(new String[] {"boop"}, option.toArray());
    }

    @Test
    public void optionTest14() {
        Option<Object> option = Option.some(null);
        assertTrue(option.toList().size() == 1);
        assertEquals(null, option.toList().get(0));
    }

    @Test
    public void optionTest15() {
        Option<String> option = Option.some("boop");
        assertTrue(option.toList().size() == 1);
        assertEquals("boop", option.toList().get(0));
    }

    @Test
    public void optionTest16() {
        Option<String> option = Option.some("boop");
        String s = option.toString();
        assertTrue(s.length() > 0);
    }

    @Test
    public void optionTest17() {
        Option<String> option = Option.none();
        String s = option.toString();
        assertTrue(s.length() > 0);
    }

    @Test
    public void OptionTest18() throws Exception {
        Option<String> option = Option.some("123");
        Option<Integer> optionOut = option.bind(new F1<String, Option<Integer>>() {
            @Override
            public Option<Integer> apply(String a) throws Exception {
                return Option.some(Integer.parseInt(a));
            }});

        assertTrue(optionOut.isSome());
        assertEquals(Integer.valueOf(123), optionOut.getValue());
    }

    @Test
    public void OptionTest19() throws Exception {
        Option<String> option = Option.none();
        Option<Integer> optionOut = option.bind(new F1<String, Option<Integer>>() {
            @Override
            public Option<Integer> apply(String a) throws Exception {
                return Option.some(Integer.parseInt(a));
            }});

        assertTrue(optionOut.isNone());
    }
}
